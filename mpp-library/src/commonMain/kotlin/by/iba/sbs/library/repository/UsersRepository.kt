package by.iba.sbs.library.repository

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.data.remote.NetworkBoundResource
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.data.remote.Users
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.User
import by.iba.sbs.library.model.request.UserCreate
import by.iba.sbs.library.model.response.RatingSummary
import by.iba.sbs.library.model.response.UserView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.applicationDispatcher
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IUsersRepository {
    suspend fun addUser(data: User): Response<UserView>

    suspend fun getAllUsers(forceRefresh: Boolean): LiveData<Response<List<User>>>
    suspend fun getUserGuidelines(
        userId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Guideline>>>

    suspend fun getUser(
        userId: String,
        forceRefresh: Boolean
    ): LiveData<Response<User>>

    suspend fun deleteUser(userId: String): Response<UserView>
}

@ImplicitReflectionSerializer
class UsersRepository @UnstableDefault constructor(settings: LocalSettings) :
    IUsersRepository {
    @UnstableDefault
    private val users by lazy { Users(settings) }

    private val sbsDb = createDb()
    private val usersQueries = sbsDb.usersEntityQueries
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries
    private val ratingSummaryQueries = sbsDb.ratingSummaryQueries

    @UnstableDefault
    override suspend fun addUser(data: User): Response<UserView> = coroutineScope {
        val result = users.postUser(UserCreate(data.name, data.email))
        if (result.isSuccess) {
            val item = result.data!!
            usersQueries.addUser(item.id, item.name, item.email)
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun getAllUsers(forceRefresh: Boolean): LiveData<Response<List<User>>> {
        if (forceRefresh) clearCache()
        return object : NetworkBoundResource<List<User>, List<User>>() {
            override fun processResponse(response: List<User>): List<User> = response

            override suspend fun saveCallResults(data: List<User>) = coroutineScope {
                data.forEach {
                    usersQueries.addUser(it.id, it.name, it.email)
                }
            }

            override fun shouldFetch(data: List<User>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<User> = coroutineScope {
                return@coroutineScope usersQueries.getAllUsers().executeAsList().map {
                    User(
                        it.id,
                        it.name,
                        it.email
                    )
                }
            }

            override fun createCallAsync(): Deferred<List<User>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = users.getAllUsers()
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            User(
                                item.id,
                                item.name,
                                item.email
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()
                    }
                }
            }
        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun getUserGuidelines(
        userId: String, forceRefresh: Boolean
    ): LiveData<Response<List<Guideline>>> {
        if (forceRefresh) {
            clearCache()
        }

        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(
                        it.id,
                        it.name,
                        it.descr,
                        it.authorId,
                        it.author
                    )
                    it.rating.apply {
                        ratingSummaryQueries.insertRating(
                            it.id,
                            positive.toLong(),
                            negative.toLong(),
                            overall.toLong()
                        )
                    }
                }
            }

            override fun shouldFetch(data: List<Guideline>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Guideline> = coroutineScope {

                val ratingSummaryCache = ratingSummaryQueries.selectAllRatings().executeAsList()
                return@coroutineScope guidelinesQueries.selectGuidelinesByAuthorId(userId)
                    .executeAsList()
                    //      .filter { it.authorId == user.id }
                    .map {
                        val rating = ratingSummaryCache.firstOrNull { rating -> rating.id == it.id }
                        if (rating != null) {
                            Guideline(
                                it.id, it.name, it.description,
                                author = it.author,
                                authorId = it.authorId,
                                rating = RatingSummary(
                                    rating.positive!!.toInt(),
                                    rating.negative!!.toInt(),
                                    rating.overall!!.toInt()
                                )
                            )
                        } else {
                            Guideline(it.id, it.name, it.description, it.author, it.authorId)
                        }
                    }
            }


            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = users.getUserGuidelines(userId)
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Guideline(
                                item.id,
                                item.name,
                                item.description ?: "",
                                rating = item.rating,
                                authorId = userId,
                                author = item.activity.createdBy.name
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun getUser(
        userId: String,
        forceRefresh: Boolean
    ): LiveData<Response<User>> {
        return object : NetworkBoundResource<User, User>() {
            override fun processResponse(response: User): User = response

            override suspend fun saveCallResults(data: User) = coroutineScope {
                usersQueries.addUser(data.id, data.name, data.email)
            }

            override fun shouldFetch(data: User?): Boolean =
                data == null || forceRefresh

            override suspend fun loadFromDb(): User? = coroutineScope {
                val item =
                    usersQueries.getUser(userId).executeAsOneOrNull() ?: return@coroutineScope null
                return@coroutineScope User(
                    item.id,
                    item.name,
                    item.email
                )
            }

            override fun createCallAsync(): Deferred<User> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = users.getUserById(userId)
                    if (result.isSuccess) {
                        val item = result.data!!
                        User(
                            item.id,
                            item.name,
                            item.email
                        )
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        User()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun deleteUser(userId: String): Response<UserView> = coroutineScope {
        val result = users.deleteUser(userId)
        if (result.isSuccess) {
            result.data!!
            usersQueries.deleteUser(userId)
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    suspend fun clearCache() {
        usersQueries.deleteAllUsers()
    }
}