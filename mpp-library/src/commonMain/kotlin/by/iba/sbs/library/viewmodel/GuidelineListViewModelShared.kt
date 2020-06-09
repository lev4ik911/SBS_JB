package by.iba.sbs.library.viewmodel


import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel

import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class GuidelineListViewModelShared(
    private val settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModel(),
    EventsDispatcherOwner<GuidelineListViewModelShared.EventsListener> {
    val instructions = MutableLiveData<List<Guideline>>(mutableListOf())
    val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val localStorage: LocalSettings by lazy { LocalSettings(settings) }
    var isLoading: LiveData<Boolean> = _isLoading.readOnly()

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadInstructions(forceRefresh: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isSuccess && it.isNotEmpty) {
                        val guidelines = it.data!!
                        instructions.postValue(guidelines.sortedBy { item -> item.id }
                            .toList())
                    } else if (it.error != null)
                        eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage(
                                    it.error.toString(),
                                    MessageType.ERROR
                                )
                            )
                        }
                    _isLoading.postValue(it.status == Response.Status.LOADING)
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    interface EventsListener {
        fun showToast(message: ToastMessage)
    }
}