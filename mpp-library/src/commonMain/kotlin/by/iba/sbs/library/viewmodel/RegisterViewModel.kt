package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.repository.AuthRepository
import by.iba.sbs.library.repository.UsersRepository
import by.iba.sbs.library.service.SystemInformation
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

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

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val usersRepository by lazy { UsersRepository(localStorage) }

    private val sbsDb = createDb()
    private val usersQueries = sbsDb.usersEntityQueries

    val isLoginEnabled = MutableLiveData(true)

    val login = MutableLiveData("")
    val password = MutableLiveData("")
    val passwordConfirm = MutableLiveData("")

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onRegisterButtonClick() {
//        loading.value = true
//        viewModelScope.launch {
//            try {
//                val response = authRepository.login(LoginData(login.value, password.value))
//                if (response.isSuccess && response.isNotEmpty) {
//                    response.data?.let {
//                        localStorage.accessToken = it.accessToken
//                        it.user.apply {
//                            usersQueries.addUser(id, name, email)
//                            localStorage.userId = id
//                            eventsDispatcher.dispatchEvent { routeToLoginScreen() }
//                        }
//                    }
//                }
//                loading.postValue(false)
//
//            } catch (e: Exception) {
//                eventsDispatcher.dispatchEvent {
//                    showToast(
//                        ToastMessage(e.message.toString(), MessageType.ERROR)
//                    )
//                }
//            }
//        }
    }

    interface EventsListener {
        fun routeToLoginScreen()
        fun showToast(msg: ToastMessage)
    }
}
