package by.iba.sbs.library.repository

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.data.remote.Guidelines
import by.iba.sbs.library.data.remote.NetworkBoundResource
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.data.remote.Steps
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.model.request.GuidelineCreate
import by.iba.sbs.library.model.response.GuidelineView
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

    suspend fun getAllSteps(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Step>>>
    suspend fun insertGuideline(data: Guideline):Response<GuidelineView>
}

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(settings: LocalSettings) :
    IGuidelineRepository {
    @UnstableDefault
    private val guidelines = Guidelines(settings)
    private val steps = Steps(settings)
    private val sbsDb = createDb()
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries

    @UnstableDefault
    override suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {
        if (forceRefresh) clearCache()
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
                return@coroutineScope guidelinesQueries.selectAllGuidelines().executeAsList().map {
                    Guideline(it.id, it.name, it.description)
                }
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = guidelines.getAllGuidelines()
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
                val item = guidelinesQueries.selectGuidelineById(guidelineId).executeAsOne()
                return@coroutineScope Guideline(item.id, item.name, item.description)
            }

            override fun createCallAsync(): Deferred<Guideline> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = guidelines.getGuideline(guidelineId)
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

    private fun clearCache() {
        guidelinesQueries.deleteAllSteps()
        guidelinesQueries.deleteAllGuidelines()
    }
    @UnstableDefault
    override suspend fun getAllSteps(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Step>>> {
        return object : NetworkBoundResource<List<Step>, List<Step>>() {
            override fun processResponse(response: List<Step>): List<Step> = response

            override suspend fun saveCallResults(data: List<Step>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertStep(
                        it.stepId,
                        guidelineId,
                        it.name,
                        it.description,
                        it.weight.toLong()
                    )
                }
            }

            override fun shouldFetch(data: List<Step>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Step> = coroutineScope {
                return@coroutineScope guidelinesQueries.selectAllSteps(guidelineId).executeAsList()
                    .map {
                        Step(it.id, it.name, it.description, it.weight!!.toInt())
                    }
            }

            override fun createCallAsync(): Deferred<List<Step>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = steps.getAllSteps(guidelineId)
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Step(
                                item.id,
                                item.name,
                                item.description,
                                item.weight
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
    override suspend fun insertGuideline(data: Guideline): Response<GuidelineView> = coroutineScope {
        val result = guidelines.postGuideline(GuidelineCreate(data.name, data.description))
        if (result.isSuccess) {
            val item = result.data!!
            guidelinesQueries.insertGuideline(item.id, item.name, item.description?:"")
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }
}