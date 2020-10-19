package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.data.local.createDb
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
    private val sbsDb = createDb()
    private val usersQueries = sbsDb.usersEntityQueries

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val usersRepository by lazy { UsersRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val guidelineRepository by lazy { GuidelineRepository(localStorage) }


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

    @UnstableDefault
    @ImplicitReflectionSerializer
    val user = MutableLiveData(User()).apply {
        addObserver {
            if (it.id.isNotEmpty()) {
                isMyProfile.value = it.id == localStorage.userId
                isFavorite.value = !isMyProfile.value
                email.value = it.email
                fullName.value = it.name
                loadUserGuidelines(false)
            }
        }
    }
    val rating = MutableLiveData("547")

    val guidelines = MutableLiveData<List<Guideline>>(mutableListOf())

    val subscribers = MutableLiveData<List<Author>>(mutableListOf()).apply {
        val mData = ArrayList<Author>()
        mData.add(Author("Petey Cruiser", 12, 123, 547))
        mData.add(Author("Mario Speedwagon", 33, 21, 123))
        mData.add(Author("Michael A. Shaffer", 23, 17, 99))
        value = mData
    }

    val updatedGuidelineId = MutableLiveData("")

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadUser(userId: String, forceRefresh: Boolean) {
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
                usersRepository.getUser(
                    mUserId,
                    forceRefresh
                )
                    .addObserver {
                        loading.postValue(it.status == Response.Status.LOADING)
                        if (it.isSuccess && it.isNotEmpty) {
                            user.postValue(it.data!!)
                        } else if (it.error != null) eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage("LoadUser: ${it.error}", MessageType.ERROR)
                            )
                        }
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
    fun updateUser() {
        loading.value = true
        user.value = User(user.value.id, fullName.value, user.value.email)
        viewModelScope.launch {
            try {
                val response = usersRepository.updateUser(user.value)
                if (response.isSuccess && response.isNotEmpty) {
                    response.data?.let {
                        user.postValue(User(it.id, it.name, it.email))
                        usersQueries.addUser(it.id, it.name, it.email)
                        //    eventsDispatcher.dispatchEvent { routeToProfileScreen() }
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadUserGuidelines(forceRefresh: Boolean) {
        loading.value = true
        viewModelScope.launch {
            try {
                usersRepository.getUserGuidelines(user.value.id, forceRefresh)
                    .addObserver {
                        loading.postValue(it.status == Response.Status.LOADING)
                        if (it.isSuccess && it.isNotEmpty) {
                            guidelines.value = it.data!!.sortedBy { item -> item.id }.toList()
                            checkPreviewImage(guidelines.value)
                        } else if (it.error != null)
                            eventsDispatcher.dispatchEvent {
                                showToast(
                                    ToastMessage(
                                        it.error.toString(),
                                        MessageType.ERROR
                                    )
                                )
                            }
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

    fun onOpenGuidelineClick(guideline: Guideline) {
        eventsDispatcher.dispatchEvent { onOpenGuidelineAction(guideline) }
    }

    fun onActionButtonClick() {
        eventsDispatcher.dispatchEvent { onActionButtonAction() }
    }

    fun onLogoutButtonClick() {
        eventsDispatcher.dispatchEvent { onLogoutAction() }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onSaveProfileButtonClick() {
        updateUser()
        //   eventsDispatcher.dispatchEvent { onSaveProfileAction() }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun logout() {
        localStorage.accessToken = ""
        localStorage.userId = ""
        viewModelScope.launch {
            try {
                guidelineRepository.clearFavorites()
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
        eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }

    fun onClearHistoryClick() {
        eventsDispatcher.dispatchEvent { requireAccept() }
    }

    fun onAcceptClearHistory() {
        localStorage.searchHistoryJson = ""
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun checkPreviewImage(source: List<Guideline>) {
        viewModelScope.launch {
            try {
                source.forEach {
                    if (!it.isEmptyPreview) {
                        guidelineRepository.checkPreviewImageForGuideline(it)?.let { url ->
                            eventsDispatcher.dispatchEvent { loadImage(url, it) }
                        }
                    } else {
                        //TODO("Add check removed images")
                    }
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun saveGuidelinePreviewImageInLocalDB(guideline: Guideline) {
        viewModelScope.launch {
            try {
                guidelines.value.find { g -> g.id == guideline.id }?.imagePath = guideline.imagePath
                guidelineRepository.saveGuidelineImageInLocalDB(guideline)
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

    interface EventsListener {
        fun onActionButtonAction()
        fun onLogoutAction()
        fun onSaveProfileAction()
        fun routeToLoginScreen()
        fun routeToProfileScreen()
        fun showToast(msg: ToastMessage)
        fun requireAccept()
        fun onOpenGuidelineAction(guideline: Guideline)
        fun loadImage(url: String, guideline: Guideline)
    }

}
