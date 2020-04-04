package by.iba.sbs.ui.login

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain

class ResetViewModel : BaseViewModel(),
    EventsDispatcherOwner<ResetViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<ResetViewModel.EventsListener> = eventsDispatcherOnMain()
    val oldPassword = MutableLiveData("")
    val isOldPasswordValid = MutableLiveData(false)
    val newPassword = MutableLiveData("")
    val newPasswordConfirm = MutableLiveData("")
    val isPasswordValid = MutableLiveData(false)
    fun onConfirmButtonPressed(){}
    fun onBackButtonPressed(){ eventsDispatcher.dispatchEvent { onBackButtonPressed() }}
    fun onNextButtonPressed(){
        eventsDispatcher.dispatchEvent { onNextButtonPressed() }
    }
    interface EventsListener {
        fun onConfirmButtonPressed(){}
        fun onBackButtonPressed(){}
        fun onNextButtonPressed(){}
    }

}
