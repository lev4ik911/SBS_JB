package by.iba.sbs.library.viewmodel

import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData

class ResetViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<ResetViewModel.EventsListener> {
    val oldPassword = MutableLiveData("")
    val isOldPasswordValid = MutableLiveData(false)
    val newPassword = MutableLiveData("")
    val newPasswordConfirm = MutableLiveData("")
    val isPasswordValid = MutableLiveData(false)
    fun onConfirmButtonPressed() {}
    fun onBackButtonPressed() {
        eventsDispatcher.dispatchEvent { onBackButtonPressed() }
    }

    fun onNextButtonPressed() {
        eventsDispatcher.dispatchEvent { onNextButtonPressed() }
    }

    interface EventsListener {
        fun onConfirmButtonPressed() {}
        fun onBackButtonPressed() {}
        fun onNextButtonPressed() {}
    }

}
