package by.iba.mvvmbase.dispatcher

interface EventsDispatcherOwner<T : Any> {
    val eventsDispatcher: EventsDispatcher<T>
}