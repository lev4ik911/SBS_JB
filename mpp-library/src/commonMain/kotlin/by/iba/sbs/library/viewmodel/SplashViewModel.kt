package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.service.SystemInformation
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner

class SplashViewModel(
    settings: Settings,
    systemInfo: SystemInformation,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<SplashViewModel.EventsListener> {

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
