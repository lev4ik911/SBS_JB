package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain


class SplashViewModel : BaseViewModel(), EventsDispatcherOwner<SplashViewModel.EventsListener> {

   override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()

    val appVersion: String = "settings.appVersion"
    fun checkCredentials() {
        //if(settings.userCredentials.isValid())
        //    eventsDispatcher.dispatchEvent { routeToMainScreen() }
        //else
            eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }
    interface EventsListener {
        fun routeToMainScreen()
        fun routeToLoginScreen()
    }
}
