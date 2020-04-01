package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain

class RegisterViewModel : BaseViewModel(), EventsDispatcherOwner<RegisterViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<RegisterViewModel.EventsListener> = eventsDispatcherOnMain()
    interface EventsListener {
    }

}
