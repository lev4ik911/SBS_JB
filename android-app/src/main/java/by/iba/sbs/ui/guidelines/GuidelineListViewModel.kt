package by.iba.sbs.ui.guidelines

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.MessageType
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.mvvmbase.model.ToastMessage
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.AndroidSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault


class GuidelineListViewModel(context: Context) : BaseViewModel(),
    EventsDispatcherOwner<GuidelineListViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val instructions = MutableLiveData<List<Guideline>>()
    private val localStorage: LocalSettings by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalSettings(settings)
    }

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    @ImplicitReflectionSerializer
    fun loadInstructions(forceRefresh: Boolean) {
        viewModelScope.launch {
            try {
                val guidelinesLiveData = repository.getAllGuidelines(forceRefresh)
                guidelinesLiveData.addObserver {
                    if (it.isSuccess && it.isNotEmpty) {
                        val employees = it.data!!
                        instructions.postValue(employees.sortedBy { item -> item.id }
                            .toList())
                    } else if (it.error != null) notificationsQueue.value =
                        ToastMessage(it.error!!.toString(), MessageType.ERROR)
                }
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
//
//        instructions.apply {
//            val mData = ArrayList<Guideline>()
//            mData.add(Guideline("7", "Как сдать СМК на отлично!", "Dobry", isFavorite = true))
//            mData.add(Guideline("1", "Как попасть на проект, подготовка к интервью", "Author 2"))
//            mData.add(Guideline("3", "Как стать счастливым", "Dobry", isFavorite = true))
//            mData.add(Guideline("2", "Отпадный шашлычок", "Dobry", isFavorite = true))
//            mData.add(Guideline("4", "Что делать, если вы заразились", "Доктор"))
//            mData.add(Guideline("5", "Как поставить на учет автомобиль", "Dobry"))
//            mData.add(Guideline("6", "Как оформить командировку", "Dobry"))
//
//            this.value = mData
//        }
    }

    interface EventsListener {

    }
}