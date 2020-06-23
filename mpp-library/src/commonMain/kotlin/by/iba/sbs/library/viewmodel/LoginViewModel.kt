package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.repository.AuthRepository
import by.iba.sbs.library.service.SystemInformation
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class LoginViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>,
    systemInfo: SystemInformation
) : ViewModelExt(settings),
    EventsDispatcherOwner<LoginViewModel.EventsListener> {


    val appVersion: String = systemInfo.getAppVersion()

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val repository by lazy { AuthRepository(localStorage) }
    val isLoginEnabled = MutableLiveData(true)

    val keepLogin = MutableLiveData(true).apply {
        value = localStorage.keepLogin
        addObserver {
            localStorage.keepLogin = it
        }
    }

    val login = MutableLiveData(localStorage.login)
        .also {
            it.addObserver { value ->
                isLoginEnabled.value = value.isNotBlank()
                localStorage.login = if (keepLogin.value) value else ""
            }
        }

    fun init() {
        if (login.value.isNotEmpty())
            eventsDispatcher.dispatchEvent { flipToPassword() }
    }

    val password = MutableLiveData("RooT~Pa55w0rd")

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onLoginButtonClick() {
        loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(LoginData(login.value, password.value))
                if (response.isSuccess && response.isNotEmpty) {
                    response.data?.let {
                        localStorage.accessToken = it.accessToken
                    }
                }
                loading.postValue(false)

            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    fun onNextButtonClick() {
        eventsDispatcher.dispatchEvent { flipToPassword() }
    }

    fun onBackButtonClick() {
        eventsDispatcher.dispatchEvent { flipToLogin() }
    }

    fun onResetPasswordClick() {
        eventsDispatcher.dispatchEvent { onResetPassword() }
    }

    fun onRegisterClick() {
        eventsDispatcher.dispatchEvent { onRegister() }
    }

    interface EventsListener {
        fun onResetPassword()
        fun onRegister()
        fun routeToMainScreen()
        fun routeToLoginScreen()
        fun flipToPassword()
        fun flipToLogin()
        fun showToast(msg: ToastMessage)
    }
}
