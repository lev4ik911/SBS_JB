package by.iba.sbs.ui.instructions

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Instruction

class InstructionListViewModel : BaseViewModel(),
    EventsDispatcherOwner<InstructionListViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val instructions = MutableLiveData<List<Instruction>>()

    fun loadInstructions(){
        instructions.apply {
            val mData = ArrayList<Instruction>()
            mData.add(Instruction("Как сдать СМК на отлично!", "Dobry"))
            mData.add(Instruction("Как попасть на проект, подготовка к интервью", "Author 2"))
            mData.add(Instruction("Как стать счастливым", "Dobry"))
            mData.add(Instruction("Отпадный шашлычок", "Dobry"))
            mData.add(Instruction("Что делать, если вы заразились", "Доктор"))
            mData.add(Instruction("Как поставить на учет автомобиль", "Dobry"))
            mData.add(Instruction("Как оформить командировку", "Dobry"))

            this.value = mData
        }
    }

    interface EventsListener {

    }
}