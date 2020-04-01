package by.iba.sbs.ui.login

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain

class LoginViewModel : BaseViewModel(), EventsDispatcherOwner<LoginViewModel.EventsListener> {

    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val appVersion: String = "version"//settings.appVersion
    val isLoginEnabled = MutableLiveData(true)

    val keepLogin = MutableLiveData(true).apply {
//        value = settings.keepLogin
//        observeForever {
//            settings.keepLogin = it
//        }
    }

    val login = MutableLiveData("settings.login")
        .also {
            it.observeForever { value ->
                isLoginEnabled.value = value.isNotBlank()
                //settings.login = if (keepLogin.value!!) value else ""
            }
        }

    val password = MutableLiveData("")//TODO
    fun onLoginButtonPressed() {

    }

    fun onNextButtonPressed() {

        eventsDispatcher.dispatchEvent { flipToPassword() }
    }

    fun onBackButtonPressed() {
        eventsDispatcher.dispatchEvent { flipToLogin() }
    }

    interface EventsListener {
        fun routeToMainScreen()
        fun routeToLoginScreen()
        fun flipToPassword()
        fun flipToLogin()
    }
}
