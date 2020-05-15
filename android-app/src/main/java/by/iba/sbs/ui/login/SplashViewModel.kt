package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.service.SystemInformation


class SplashViewModel(systemInfo: SystemInformation) : BaseViewModel(),
    EventsDispatcherOwner<SplashViewModel.EventsListener> {

    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()

    val appVersion: String = systemInfo.getAppVersion()
    fun checkCredentials() {
        //if(settings.userCredentials.isValid())
        //    eventsDispatcher.dispatchEvent { routeToMainScreen() }
        //else
        //   eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }

    fun onLoginButtonClick() {
        eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }

    fun onRegisterButtonClick() {
        eventsDispatcher.dispatchEvent { routeToRegisterScreen() }
    }

    fun onJustLookButtonClick() {
        eventsDispatcher.dispatchEvent { routeToMainScreen() }
    }

    interface EventsListener {
        fun routeToMainScreen()
        fun routeToLoginScreen()
        fun routeToRegisterScreen()
    }
}
