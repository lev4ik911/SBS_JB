package by.iba.sbs.library.repository

import by.iba.sbs.library.data.remote.Client
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IGuidelineRepository{
    suspend fun getAllGuidelines(): LiveData<Response<List<Guideline>>>
}
@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(private val remote: Client):IGuidelineRepository{
    override suspend fun getAllGuidelines(): LiveData<Response<List<Guideline>>> {
        TODO("Not yet implemented")
    }

}