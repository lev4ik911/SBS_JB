package by.iba.sbs.ui.guidelines

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.MessageType
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.mvvmbase.model.ToastMessage
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.AndroidSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class GuidelineListViewModel(context: Context) : BaseViewModel(),
    EventsDispatcherOwner<GuidelineListViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val instructions = MutableLiveData<List<Guideline>>()
    private val localStorage: LocalSettings by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalSettings(settings)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadInstructions(forceRefresh: Boolean) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isSuccess && it.isNotEmpty) {
                        val guidelines = it.data!!
                        instructions.postValue(guidelines.sortedBy { item -> item.id }
                            .toList())
                    } else if (it.error != null)
                        notificationsQueue.value =
                            ToastMessage(it.error!!.toString(), MessageType.ERROR)
                    isLoading.postValue(it.status == Response.Status.LOADING)
                }
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    interface EventsListener {

    }
}