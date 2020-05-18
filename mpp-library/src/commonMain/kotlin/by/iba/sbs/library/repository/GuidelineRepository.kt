package by.iba.sbs.library.repository

import by.iba.sbs.db.SBSDB
import by.iba.sbs.library.data.remote.Client
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IGuidelineRepository{
    suspend fun getAllGuidelines(): LiveData<Response<List<Guideline>>>
}

expect fun createDb(): SBSDB

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(private val settings: Settings) :
    IGuidelineRepository {
    private val localStorage: LocalSettings by lazy { LocalSettings(settings) }

    @UnstableDefault
    private val galwayBusApi = Client(settings)
    private val sbsDb = createDb()
    private val galwayBusQueries = sbsDb.guidelinesEntityQueries
    override suspend fun getAllGuidelines(): LiveData<Response<List<Guideline>>> {
        TODO("Not yet implemented")
    }

}