package by.iba.sbs.library.viewmodel


import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Category
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.repository.UsersRepository

import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class DashboardViewModelShared(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<DashboardViewModelShared.EventsListener> {

    private val showRecommended = MutableLiveData(true).apply {
        value = localStorage.showRecommended || localStorage.accessToken.isEmpty()
    }
    private val showFavorites = MutableLiveData(true).apply {
        value = localStorage.showFavorites && localStorage.accessToken.isNotEmpty()
    }
    var isShowRecommended: LiveData<Boolean> = showRecommended.readOnly()
    var isShowFavorites: LiveData<Boolean> = showFavorites.readOnly()

    fun update() {
        showRecommended.value = localStorage.showRecommended || localStorage.accessToken.isEmpty()
        showFavorites.value = localStorage.showFavorites && localStorage.accessToken.isNotEmpty()
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val usersRepository by lazy { UsersRepository(localStorage) }

    val categories = MutableLiveData<List<Category>>(mutableListOf()).apply {
        val mData = ArrayList<Category>()
        mData.add(Category("Кулинария", "", true, "#f08c8c"))
        mData.add(Category("Медицина", "", false, "#660066"))
        mData.add(Category("Ремонт авто", "", false, "#5ac18e"))
        //mData.add(Category("Выживание", "", true, "#060054"))
        mData.add(Category("IBA info", "", true, "#246801"))
        //mData.add(Category("Документы", "", true, "#246801"))
        mData.add(Category("СМК", "", true, "#003366"))
        //mData.add(Category("Строительство", "", true, "#246801"))
        //mData.add(Category("Экстренная помощь", "", true, "#246801"))
        mData.add(Category("IBA docs", "", true, "#407294"))
        value = mData
    }
    val recommended = MutableLiveData<List<Guideline>>(mutableListOf()).apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("1", "Loading guidelines...", "Dobry"))
        value = mData
    }
    val favorite = MutableLiveData<List<Guideline>>(mutableListOf()).apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("1", "Loading guidelines...", "Loading...", "Dobry"))
        value = mData
    }
    val popular = MutableLiveData<List<Guideline>>(mutableListOf()).apply {

    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadFavorites(forceRefresh: Boolean, itemsCount: Int = -1) {
        loading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isNotEmpty) {
                        if (it.error != null) {
                            offlineMode.value = true
                        }
                        else if (forceRefresh) {
                            offlineMode.value = false
                        }
                        val guidelines = it.data!!
                        favorite.postValue(
                            if (itemsCount == -1) {
                                if (localStorage.userId.isNotEmpty())
                                    guidelines
                                        .filter { item -> item.isFavorite }
                                        .sortedBy { item -> item.id }
                                        .toList()
                                else
                                    guidelines
                                        .sortedBy { item -> item.id }
                                        .toList()
                            }
                            else
                                guidelines
                                    .filter { item -> item.isFavorite }
                                    .take(itemsCount)
                                    .sortedBy { item -> item.id }
                                    .toList()
                        )
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadRecommended(forceRefresh: Boolean, itemsCount: Int = -1) {
        loading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isNotEmpty) {
                        if (it.error != null) {
                            offlineMode.value = true
                        }
                        else if (forceRefresh) {
                            offlineMode.value = false
                        }
                        val guidelines = it.data!!
                        recommended.postValue(
                            if (itemsCount == -1)
                                guidelines
                                    .sortedBy { item -> item.id }
                                    .toList()
                            else
                                guidelines
                                    .take(itemsCount)
                                    .sortedBy { item -> item.id }
                                    .toList()
                        )
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadPopular(forceRefresh: Boolean, itemsCount: Int = -1) {
        loading.value = true
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isNotEmpty) {
                        if (it.error != null) {
                            offlineMode.value = true
                        }
                        else if (forceRefresh) {
                            offlineMode.value = false
                        }
                        val guidelines = it.data!!
                        popular.postValue(
                            if (itemsCount == -1)
                                guidelines
                                    .sortedBy { item -> item.id }
                                    .toList()
                            else
                                guidelines
                                    .take(itemsCount)
                                    .sortedBy { item -> item.id }
                                    .toList()
                        )
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadUserFavorites(forceRefresh: Boolean) {
        val userId = localStorage.userId
        if (userId.isEmpty() || !forceRefresh) {
            eventsDispatcher.dispatchEvent { onFavoritesLoaded(forceRefresh) }
            return
        }
        loading.value = true
        viewModelScope.launch {
            try {
                repository.clearFavorites()
                val result = usersRepository.getUserFavorites(userId)
                if (result.error != null)
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage(
                                result.error.toString(),
                                MessageType.ERROR
                            )
                        )
                    }
                loading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onFavoritesLoaded(forceRefresh) }
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

    fun onViewFavoritesClick() {
        eventsDispatcher.dispatchEvent { onViewFavoritesAction() }
    }

    fun onViewRecommendedClick() {
        eventsDispatcher.dispatchEvent { onViewRecommendedAction() }
    }

    fun onViewPopularClick() {
        eventsDispatcher.dispatchEvent { onViewPopularAction() }
    }

    fun onViewCategoriesClick() {

    }

    interface EventsListener {
        fun onViewFavoritesAction()
        fun onViewRecommendedAction()
        fun onViewPopularAction()
        fun showToast(msg: ToastMessage)
        fun onFavoritesLoaded(forceRefresh:Boolean)
    }
}