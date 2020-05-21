package by.iba.sbs.library.repository

import by.iba.sbs.db.SBSDB
import by.iba.sbs.library.data.remote.Client
import by.iba.sbs.library.data.remote.NetworkBoundResource
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.coroutines.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IGuidelineRepository{
    suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>>
    suspend fun getGuideline(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<Guideline>>
}

expect fun createDb(): SBSDB

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(settings: LocalSettings) :
    IGuidelineRepository {
    @UnstableDefault
    private val remote = Client(settings)
    private val sbsDb = createDb()
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries

    @UnstableDefault
    override suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(it.id, it.name, it.description)
                }
            }

            override fun shouldFetch(data: List<Guideline>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Guideline> = coroutineScope {
                val result: MutableList<Guideline> = mutableListOf()
                guidelinesQueries.selectAll().executeAsList().forEach {
                    result.add(Guideline(it.id, it.name, it.description))
                }
                return@coroutineScope result
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = remote.getAllGuidelines()
                    //val result = remote.getGuideline("3483dcf1-9497-49be-b12d-e73cd47c8e94")
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Guideline(
                                item.id,
                                item.name,
                                item.description!!
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
    override suspend fun getGuideline(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<Guideline>> {
        return object : NetworkBoundResource<Guideline, Guideline>() {
            override fun processResponse(response: Guideline): Guideline = response

            override suspend fun saveCallResults(data: Guideline) = coroutineScope {
                guidelinesQueries.insertGuideline(data.id, data.name, data.description)
            }

            override fun shouldFetch(data: Guideline?): Boolean =
                data == null || forceRefresh

            override suspend fun loadFromDb(): Guideline = coroutineScope {
                val item = guidelinesQueries.selectById(guidelineId).executeAsOne()
                return@coroutineScope Guideline(item.id, item.name, item.description)
            }

            override fun createCallAsync(): Deferred<Guideline> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = remote.getGuideline(guidelineId)
                    //val result = remote.getGuideline("3483dcf1-9497-49be-b12d-e73cd47c8e94")
                    if (result.isSuccess) {
                        val item = result.data!!
                        Guideline(
                            item.id,
                            item.name,
                            item.description!!
                        )
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        Guideline()

                    }
                }
            }

        }.build()
            .asLiveData()
    }
}