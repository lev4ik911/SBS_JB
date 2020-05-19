package by.iba.sbs.library.repository

import by.iba.sbs.db.SBSDB
import by.iba.sbs.library.data.remote.Client
import by.iba.sbs.library.data.remote.NetworkBoundResource
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.coroutines.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IGuidelineRepository{
    suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>>
}

expect fun createDb(): SBSDB

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(private val settings: Settings) :
    IGuidelineRepository {
    private val localStorage: LocalSettings by lazy { LocalSettings(settings) }

    @UnstableDefault
    private val remote = Client(settings)
    private val sbsDb = createDb()
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries

    @UnstableDefault
    override suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(items: List<Guideline>) = coroutineScope {
                items.forEach {
                    guidelinesQueries.insertGuideline(it.id, it.name, it.description)
                }
            }

            override fun shouldFetch(data: List<Guideline>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Guideline> = coroutineScope {
                var result: MutableList<Guideline> = mutableListOf()
                guidelinesQueries.selectAll().executeAsList().forEach {
                    result.add(Guideline(it.id, it.name, it.description))
                }
                return@coroutineScope result
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = remote.getAllGuidelines()
                    if (result.isSuccess)
                        result.data!!
                    else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()

                    }
                }
            }

        }.build()
            .asLiveData()
    }

}