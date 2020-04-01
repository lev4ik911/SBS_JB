package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseViewModel


class SplashViewModel : BaseViewModel() {
    val appVersion: String = "settings.appVersion"

    interface EventsListener {
        fun routeToMainScreen()
        fun routeToLoginScreen()
    }
}
