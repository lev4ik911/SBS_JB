package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel

open class ViewModelExt(private val settings: Settings) : ViewModel() {
    val localStorage: LocalSettings by lazy { LocalSettings(settings) }
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLoading: LiveData<Boolean> = loading.readOnly()
    val offlineMode: MutableLiveData<Boolean> = MutableLiveData(localStorage.offlineMode)
        .also {
            it.addObserver { item ->
                localStorage.offlineMode = item
            }
        }
    var isOfflineMode: LiveData<Boolean> = offlineMode.readOnly()
//
//    companion object{
//        val offlineMode = MutableLiveData(false)
//    }
//    fun setOfflineMode(value:Boolean){
//        offlineMode.value = value
//    }
}