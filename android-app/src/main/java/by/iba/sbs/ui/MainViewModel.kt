package by.iba.sbs.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.viewmodel.ViewModelExt
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner

enum class ActiveTabEnum(var index: Int) {
    ID_HOME(1),
    ID_DASHBOARD(3),
    ID_NOTIFICATION(4),
    ID_SETTINGS(5)
}

class MainViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings), EventsDispatcherOwner<MainViewModel.EventsListener> {
    val activeTab: MutableLiveData<Int> = MutableLiveData(ActiveTabEnum.ID_HOME.index)
    fun onOpenGuidelineClick(view: View, guideline: Guideline) {
        eventsDispatcher.dispatchEvent { onOpenGuidelineAction(view, guideline) }
    }

    interface EventsListener {
        fun onOpenGuidelineAction(view: View, guideline: Guideline)
    }
}