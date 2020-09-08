package by.iba.sbs.library.repository

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.data.remote.NetworkBoundResource
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.data.remote.Users
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.User
import by.iba.sbs.library.model.request.UserCreate
import by.iba.sbs.library.model.response.FavoriteView
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

    @UnstableDefault
    suspend fun getUserFavorites(userId: String): Response<List<FavoriteView>>
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
    private val favoritesQueries = sbsDb.favoritesEntityQueries
    private val imagesQueries = sbsDb.imagesEntityQueries

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
        val imagesCache = imagesQueries.selectImagesForGuidelines().executeAsList()
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(
                        it.id,
                        it.name,
                        it.descr,
                        it.favourited.toLong(),
                        it.authorId,
                        it.author,
                        it.remoteImageId,
                        it.updateImageDateTime
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
                return@coroutineScope guidelinesQueries.selectGuidelinesByAuthorId(userId)
                    .executeAsList()
                    .map {
                        val rating = ratingSummaryQueries.selectAllRatings().executeAsList()
                            .firstOrNull { rating -> rating.id == it.id }
                        if (rating != null) {
                            Guideline(
                                it.id, it.name, it.description, it.favourited!!.toInt(),
                                author = it.author,
                                authorId = it.authorId,
                                rating = RatingSummary(
                                    rating.positive!!.toInt(),
                                    rating.negative!!.toInt(),
                                    rating.overall!!.toInt()
                                ),
                                imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                                remoteImageId = it.remoteImageId,
                                updateImageDateTime = it.updateImageDateTime
                            )
                        } else {
                            Guideline(it.id,
                                it.name,
                                it.description,
                                it.favourited!!.toInt(),
                                it.author,
                                it.authorId,
                                imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                                remoteImageId = it.remoteImageId,
                                updateImageDateTime = it.updateImageDateTime
                            )
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
                                item.favourited,
                                rating = item.rating,
                                authorId = item.activity.createdBy.id,
                                author = item.activity.createdBy.name,
                                imagePath = imagesCache.firstOrNull{im->im.guidelineId == item.id}?.localImagePath.orEmpty(),
                                remoteImageId = item.preview?.id.orEmpty(),
                                updateImageDateTime = item.preview?.activity?.updatedAt.orEmpty()
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

//    @UnstableDefault
//    override suspend fun getUserFavorites(userId: String
//    ): LiveData<Response<List<FavoriteView>>> {
//        favoritesQueries.clearFavorites()
//        return object : NetworkBoundResource<List<FavoriteView>, List<FavoriteView>>() {
//            override fun processResponse(response: List<FavoriteView>): List<FavoriteView> = response
//
//            override suspend fun saveCallResults(data: List<FavoriteView>) = coroutineScope {
//                data.forEach {
//                    favoritesQueries.addGuidelineToFavorites(
//                        it.id,
//                        it.entity.type,
//                        it.entity.id
//                    )
//                }
//            }
//
//            override fun shouldFetch(data: List<FavoriteView>?): Boolean = true
//
//            override fun createCallAsync(): Deferred<List<FavoriteView>> {
//                return GlobalScope.async(applicationDispatcher) {
//                    val result = users.getUserFavorites(userId)
//                    if (result.isSuccess) {
//                        result.data!!
//                    } else {
//                        if (result.status == Response.Status.ERROR) error(result.error!!)
//                        listOf()
//                    }
//                }
//            }
//
//            override suspend fun loadFromDb(): List<FavoriteView>? {
//                TODO("Not yet implemented")
//            }
//
//        }.build()
//            .asLiveData()
//    }

    @UnstableDefault
    override suspend fun getUserFavorites(userId: String
    ): Response<List<FavoriteView>> = coroutineScope {
        val result = users.getUserFavorites(userId)
        if (result.isSuccess) {
            result.data!!.forEach {
                    favoritesQueries.addGuidelineToFavorites(
                        it.id,
                        it.entity.type,
                        it.entity.id
                    )
                }
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }
}