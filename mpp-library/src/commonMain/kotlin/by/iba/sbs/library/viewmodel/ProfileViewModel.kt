package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.*
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.repository.UsersRepository
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class ProfileViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<ProfileViewModel.EventsListener> {

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val usersRepository by lazy { UsersRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }
    val showRecommended = MutableLiveData(true).apply {
        value = localStorage.showRecommended
        addObserver {
            localStorage.showRecommended = it
        }
    }
    val showFavorites = MutableLiveData(true).apply {
        value = localStorage.showFavorites
        addObserver {
            localStorage.showFavorites = it
        }
    }
    var searchHistoryCount = MutableLiveData(5).apply {
        value = localStorage.searchHistoryCount
        addObserver {
            localStorage.searchHistoryCount = it
        }
    }

    val email = MutableLiveData("email@email.com")
    val fullName = MutableLiveData("John Doe")
    val isFavorite = MutableLiveData(true)
    val isMyProfile = MutableLiveData(true)
    val user = MutableLiveData(User()).apply {
        addObserver {
            isMyProfile.value = it.id == localStorage.userId
            isFavorite.value = !isMyProfile.value
        }
    }
    val rating = MutableLiveData("547")

    val guidelines = MutableLiveData<List<Guideline>>(mutableListOf())

    val subscribers = MutableLiveData<List<Author>>(mutableListOf()).apply {
        val mData = ArrayList<Author>()
        mData.add(Author("Petey Cruiser", 12, 123, 547))
        mData.add(Author("Mario Speedwagon", 33, 21, 123))
        value = mData
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadUser(userId: String) {
        loading.value = true
        val mUserId = if (userId.isEmpty())
            if (localStorage.userId.isEmpty()) {
                eventsDispatcher.dispatchEvent { routeToLoginScreen() }
                return
            } else
                localStorage.userId
        else userId
        viewModelScope.launch {
            try {
                val resultLiveData = usersRepository.getUser(
                    mUserId,
                    false
                )
                resultLiveData.addObserver {
                    if (it.isSuccess && it.isNotEmpty) {
                        user.postValue(it.data!!)
                    } else if (it.error != null) eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage("LoadUser: ${it.error}", MessageType.ERROR)
                        )
                    }
                    loading.postValue(it.status == Response.Status.LOADING)
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadUserGuidelines(forceRefresh: Boolean) {
        loading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isSuccess && it.isNotEmpty) {
                        var gl = it.data!!

                        guidelines.postValue(gl.sortedBy { item -> item.id }
                            .toList())

                    } else if (it.error != null)
                        eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage(
                                    it.error.toString(),
                                    MessageType.ERROR
                                )
                            )
                        }
                    loading.postValue(it.status == Response.Status.LOADING)
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun onActionButtonClick() {
        eventsDispatcher.dispatchEvent { onActionButtonAction() }
    }

    fun onLogoutButtonClick() {
        eventsDispatcher.dispatchEvent { onLogoutAction() }
    }

    fun logout() {
        localStorage.accessToken = ""
        localStorage.userId = ""
        eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }

    fun onClearHistoryClick() {
        eventsDispatcher.dispatchEvent { requireAccept() }
    }

    fun onAcceptClearHistory() {
        localStorage.searchHistoryJson = ""
    }

    interface EventsListener {
        fun onActionButtonAction()
        fun onLogoutAction()
        fun routeToLoginScreen()
        fun showToast(msg: ToastMessage)
        fun requireAccept()
    }

}
