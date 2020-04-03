package by.iba.sbs.ui.login

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.service.LocalStorage
import com.russhwolf.settings.AndroidSettings

class LoginViewModel(context: Context) : BaseViewModel(),
    EventsDispatcherOwner<LoginViewModel.EventsListener> {

    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val appVersion: String = "version"//settings.appVersion
    val isLoginEnabled = MutableLiveData(true)
    private val localStorage: LocalStorage by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalStorage(settings)
    }
    val keepLogin = MutableLiveData(true).apply {
        value = localStorage.keepLogin
        observeForever {
            localStorage.keepLogin = it
        }
    }

    val login = MutableLiveData(localStorage.login)
        .also {
            it.observeForever { value ->
                isLoginEnabled.value = value.isNotBlank()
                localStorage.login = if (keepLogin.value!!) value else ""
            }
        }
    fun init() {
        if (login.value!!.isNotEmpty())
            eventsDispatcher.dispatchEvent { flipToPassword() }
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
