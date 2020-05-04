package by.iba.sbs.ui.profile

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Author
import by.iba.sbs.library.model.Instruction

class ProfileViewModel : BaseViewModel(), EventsDispatcherOwner<ProfileViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val email = MutableLiveData("email@email.com")
    val fullName = MutableLiveData("John Doe")
    val rating = MutableLiveData("547")
    val isFavorite = MutableLiveData(true)
    val isMyProfile = MutableLiveData(true)
    val instructions = MutableLiveData<List<Instruction>>().apply {
        val mData = ArrayList<Instruction>()
        mData.add(Instruction(1, "Как стать счастливым", "Dobry"))
        mData.add(Instruction(2, "Отпадный шашлычок", "Dobry"))
        value = mData
    }
    val subscribers = MutableLiveData<List<Author>>().apply {
        val mData = ArrayList<Author>()
        mData.add(Author("Petey Cruiser", 12, 123, 547))
        mData.add(Author("Mario Speedwagon", 33, 21, 123))
        value = mData
    }
    fun onActionButtonClick() {

    }
    fun onLogoutButtonClick() {

    }
    interface EventsListener {

    }

}
