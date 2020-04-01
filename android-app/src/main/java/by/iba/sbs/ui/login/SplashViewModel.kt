package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain


class SplashViewModel : BaseViewModel(), EventsDispatcherOwner<SplashViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<SplashViewModel.EventsListener> = eventsDispatcherOnMain()
    val appVersion: String = "settings.appVersion"

    interface EventsListener {
        fun routeToMainScreen()
        fun routeToLoginScreen()
    }
}
