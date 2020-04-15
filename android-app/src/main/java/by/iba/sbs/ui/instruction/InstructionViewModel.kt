package by.iba.sbs.ui.instruction

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain


class InstructionViewModel : BaseViewModel(),
    EventsDispatcherOwner<InstructionViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val name = MutableLiveData("Отпадный шашлычок!")
    val description = MutableLiveData("Отпадный шашлычок!")
    val rating = MutableLiveData(100500)
    interface EventsListener {

    }

}