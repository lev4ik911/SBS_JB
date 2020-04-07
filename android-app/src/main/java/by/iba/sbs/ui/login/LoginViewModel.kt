package by.iba.sbs.ui.login

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.SystemInformation
import com.russhwolf.settings.AndroidSettings

class LoginViewModel(context: Context, systemInfo:SystemInformation) : BaseViewModel(),
    EventsDispatcherOwner<LoginViewModel.EventsListener> {

    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()

    val appVersion: String = systemInfo.getAppVersion()
    val isLoginEnabled = MutableLiveData(true)
    private val localStorage: LocalSettings by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalSettings(settings)
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

    val password = MutableLiveData("")

    fun onLoginButtonClick() {
    }

    fun onNextButtonClick() {
        eventsDispatcher.dispatchEvent { flipToPassword() }
    }

    fun onBackButtonClick() {
        eventsDispatcher.dispatchEvent { flipToLogin() }
    }

   fun  onResetPasswordClick(){
       eventsDispatcher.dispatchEvent { onResetPassword() }
   }
    fun  onRegisterClick(){
        eventsDispatcher.dispatchEvent { onRegister() }
    }
    interface EventsListener {
        fun onResetPassword()
        fun onRegister()
        fun routeToMainScreen()
        fun routeToLoginScreen()
        fun flipToPassword()
        fun flipToLogin()
    }
}
