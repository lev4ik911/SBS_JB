package by.iba.sbs.ui.guidelines

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.AndroidSettings

class GuidelineListViewModel(context: Context) : BaseViewModel(),
    EventsDispatcherOwner<GuidelineListViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val instructions = MutableLiveData<List<Guideline>>()
    private val localStorage: LocalSettings by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalSettings(settings)
    }

    //val repository = GuidelineRepository(localStorage)
    fun loadInstructions() {
        instructions.apply {
            val mData = ArrayList<Guideline>()
            mData.add(Guideline("7", "Как сдать СМК на отлично!", "Dobry", isFavorite = true))
            mData.add(Guideline("1", "Как попасть на проект, подготовка к интервью", "Author 2"))
            mData.add(Guideline("3", "Как стать счастливым", "Dobry", isFavorite = true))
            mData.add(Guideline("2", "Отпадный шашлычок", "Dobry", isFavorite = true))
            mData.add(Guideline("4", "Что делать, если вы заразились", "Доктор"))
            mData.add(Guideline("5", "Как поставить на учет автомобиль", "Dobry"))
            mData.add(Guideline("6", "Как оформить командировку", "Dobry"))

            this.value = mData
        }
    }

    interface EventsListener {

    }
}