package by.iba.sbs.ui.profile

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Instruction

class ProfileViewModel : BaseViewModel(), EventsDispatcherOwner<ProfileViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val email = MutableLiveData("email@email.com")
    val fullName = MutableLiveData("fullName")
    val rating = MutableLiveData("547")
    val isFavorite = MutableLiveData(true)
    val isMyProfile = MutableLiveData(true)
    val instructions = MutableLiveData<List<Instruction>>().apply {
        val mData = ArrayList<Instruction>()
        mData.add(Instruction("Как стать счастливым", "Dobry"))
        mData.add(Instruction("Отпадный шашлычок", "Dobry"))
        value = mData
    }

    fun onActionButtonClick() {

    }
    interface EventsListener {

    }

}
