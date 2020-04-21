package by.iba.sbs.ui.instruction

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Step
import java.util.*


class InstructionEditViewModel : BaseViewModel(),
    EventsDispatcherOwner<InstructionEditViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val name = MutableLiveData("")
    val description = MutableLiveData("")
    val steps = MutableLiveData<List<Step>>().apply {

    }

    fun loadInstruction(instructionId: Int) {
        if (instructionId != 0) {
            name.value = "Отпадный шашлычок!"
            description.value = "Отпадный шашлычок (desc)!"
            val mData = ArrayList<Step>()
            mData.add(
                Step(
                    1,
                    description = "Свиную шею нарезать одинаковыми кусочками, не мелкими."
                )
            )
            mData.add(
                Step(
                    1,
                    description = " Лук нарезать тонкими кольцами и помять руками (или измельчить лук с помощью кухонной техники)."
                )
            )
            mData.add(
                Step(
                    1,
                    description = "Уложить на дно емкости слой мяса, сверху посолить, поперчить."
                )
            )
            mData.add(
                Step(
                    1,
                    description = " Хорошо перемешать мясо с луком и остальными ингредиентами маринада."
                )
            )
            mData.add(
                Step(
                    1,
                    description = "Оставить свинину в маринаде на несколько часов в холодильнике."
                )
            )
            mData.add(
                Step(
                    1,
                    description = " Затем нанизывать кусочки маринованного мяса на шампуры и жарить шашлык из свинины."
                )
            )

            steps.postValue(mData)
        }
    }

    fun onActionButtonClick() {
        eventsDispatcher.dispatchEvent { onAfterSaveAction() }
    }

    interface EventsListener {
        fun onAfterSaveAction()
    }

}