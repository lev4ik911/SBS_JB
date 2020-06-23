package by.iba.sbs.library.viewmodel

import by.iba.sbs.library.model.Author
import by.iba.sbs.library.model.Guideline
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData

class ProfileViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<ProfileViewModel.EventsListener> {


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
    val email = MutableLiveData("email@email.com")
    val fullName = MutableLiveData("John Doe")
    val rating = MutableLiveData("547")
    val isFavorite = MutableLiveData(true)
    val isMyProfile = MutableLiveData(true)
    val instructions = MutableLiveData<List<Guideline>>(mutableListOf()).apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("1", "Как стать счастливым", "Dobry"))
        mData.add(Guideline("2", "Отпадный шашлычок", "Dobry"))
        value = mData
    }
    val subscribers = MutableLiveData<List<Author>>(mutableListOf()).apply {
        val mData = ArrayList<Author>()
        mData.add(Author("Petey Cruiser", 12, 123, 547))
        mData.add(Author("Mario Speedwagon", 33, 21, 123))
        value = mData
    }

    fun onActionButtonClick() {
        eventsDispatcher.dispatchEvent { onActionButtonAction() }
    }

    fun onLogoutButtonClick() {
        localStorage.accessToken = ""
        eventsDispatcher.dispatchEvent { routeToLoginScreen() }
    }

    interface EventsListener {
        fun onActionButtonAction()
        fun routeToLoginScreen()
    }

}
