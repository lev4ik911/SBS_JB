package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.model.request.RegisterData
import by.iba.sbs.library.repository.AuthRepository
import by.iba.sbs.library.service.SystemInformation
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

enum class ValidationErrors(var index: Int) {
    LOGIN_IS_EMPTY(1),
    INVALID_EMAIL(2),
    PASSWORD_IS_TOO_SMALL(3),
    PASSWORD_HAS_INCORRECT_SYMBOLS(4),
    PASSWORD_MISMATCH(5)
}

class RegisterViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>,
    systemInfo: SystemInformation
) : ViewModelExt(settings),
    EventsDispatcherOwner<RegisterViewModel.EventsListener> {

    val appVersion: String = systemInfo.getAppVersion()

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val authRepository by lazy { AuthRepository(localStorage) }

    private val sbsDb = createDb()
    private val usersQueries = sbsDb.usersEntityQueries

    val isRegisterEnabled = MutableLiveData(true)

    val login = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val passwordConfirm = MutableLiveData("")
    var errorList = mutableListOf<ValidationErrors>()

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onRegisterButtonClick() {
        validation()
        if (errorList.isEmpty()) {
            loading.value = true
            viewModelScope.launch {
                try {
                    val response =
                        authRepository.register(
                            RegisterData(
                                login.value,
                                email.value,
                                password.value
                            )
                        )
                    if (response.isSuccess && response.isNotEmpty) {
                        response.data?.let {
                            usersQueries.addUser(it.id, it.name, it.email)
                            localStorage.userId = it.id
                            eventsDispatcher.dispatchEvent { routeToLoginScreen() }
                        }
                    } else {
                        eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage(response.error.toString(), MessageType.CONFUSING)
                            )
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
        else {
            eventsDispatcher.dispatchEvent { showErrors(errorList) }
        }

    }

    fun onRouteTologinScreenClick(){
        eventsDispatcher.dispatchEvent {routeToLoginScreen()}
    }

    fun validation() {
        errorList.clear()
        if (login.value.isEmpty()) {
            errorList.add(ValidationErrors.LOGIN_IS_EMPTY)
        }
        if (!email.value.contains("@")) {
            errorList.add(ValidationErrors.INVALID_EMAIL)
        }
        if (password.value.length < 5) {
            errorList.add(ValidationErrors.PASSWORD_IS_TOO_SMALL)
        }
        if (password.value.contains(" ")) {
            errorList.add(ValidationErrors.PASSWORD_HAS_INCORRECT_SYMBOLS)
        }
        if (!passwordConfirm.value.equals(password.value)){
            errorList.add(ValidationErrors.PASSWORD_MISMATCH)
        }
    }


    interface EventsListener {
        fun routeToLoginScreen()
        fun showToast(msg: ToastMessage)
        fun showErrors(errorList: List<ValidationErrors>)
    }
}
