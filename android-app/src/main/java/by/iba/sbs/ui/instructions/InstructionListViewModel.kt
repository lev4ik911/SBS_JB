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
            mData.add(Instruction(7, "Как сдать СМК на отлично!", "Dobry", isFavorite = true))
            mData.add(Instruction(1, "Как попасть на проект, подготовка к интервью", "Author 2"))
            mData.add(Instruction(3, "Как стать счастливым", "Dobry", isFavorite = true))
            mData.add(Instruction(2, "Отпадный шашлычок", "Dobry", isFavorite = true))
            mData.add(Instruction(4, "Что делать, если вы заразились", "Доктор"))
            mData.add(Instruction(5, "Как поставить на учет автомобиль", "Dobry"))
            mData.add(Instruction(6, "Как оформить командировку", "Dobry"))

            this.value = mData
        }
    }

    interface EventsListener {

    }
}