package by.iba.sbs.ui.instruction

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import java.util.ArrayList


class InstructionEditViewModel : BaseViewModel(), EventsDispatcherOwner<InstructionEditViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val name = MutableLiveData("Отпадный шашлычок!")
    val description = MutableLiveData("Отпадный шашлычок!")
    val steps = MutableLiveData<List<ExampleListModel>>().apply {
        val mData = ArrayList<ExampleListModel>()
        mData.add(ExampleListModel("Свиную шею нарезать одинаковыми кусочками, не мелкими."))
        mData.add(ExampleListModel(" Лук нарезать тонкими кольцами и помять руками (или измельчить лук с помощью кухонной техники)."))
        mData.add(ExampleListModel("Уложить на дно емкости слой мяса, сверху посолить, поперчить."))
        mData.add(ExampleListModel(" Хорошо перемешать мясо с луком и остальными ингредиентами маринада."))
        mData.add(ExampleListModel("Оставить свинину в маринаде на несколько часов в холодильнике."))
        mData.add(ExampleListModel(" Затем нанизывать кусочки маринованного мяса на шампуры и жарить шашлык из свинины."))

        this.postValue( mData)
    }
    fun onActionButtonClick(){

    }
    interface EventsListener {

    }

}