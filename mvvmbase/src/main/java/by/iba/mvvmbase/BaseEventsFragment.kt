package by.iba.mvvmbase

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner

abstract class BaseEventsFragment<DB : ViewDataBinding, VM, Listener : Any> :
    BaseFragment<DB, VM>() where VM : BaseViewModel, VM : EventsDispatcherOwner<Listener> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        viewModel.eventsDispatcher.bind(this, this as Listener)
    }
}