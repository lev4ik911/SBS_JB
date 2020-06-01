package by.iba.sbs.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Guideline

enum class ActiveTabEnum(var index: Int) {
    ID_HOME(1),
    ID_DASHBOARD(3),
    ID_NOTIFICATION(4),
    ID_SETTINGS(5)
}

class MainViewModel : BaseViewModel(), EventsDispatcherOwner<MainViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<MainViewModel.EventsListener> =
        eventsDispatcherOnMain()
    val activeTab: MutableLiveData<Int> = MutableLiveData(ActiveTabEnum.ID_HOME.index)
    fun onOpenGuidelineClick(view: View, guideline: Guideline) {
        eventsDispatcher.dispatchEvent { onOpenGuidelineAction(view, guideline) }
    }

    interface EventsListener {
        fun onOpenGuidelineAction(view: View, guideline: Guideline)
    }
}