package by.iba.sbs.ui.instruction

import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.sbs.library.model.Feedback
import by.iba.sbs.library.model.Step
import java.util.*


class InstructionViewModel : BaseViewModel(),
    EventsDispatcherOwner<InstructionViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()
    val name = MutableLiveData("Отпадный шашлычок!")
    val description = MutableLiveData("Отпадный шашлычок desc!")
    val rating = MutableLiveData(100500)
    val isFavorite = MutableLiveData(true)
    val isMyInstruction = MutableLiveData(true)
    val feedback = MutableLiveData<List<Feedback>>().apply {
        val mData = ArrayList<Feedback>()
        mData.add(Feedback("Charlize Theron", "Something I really appreciate about you is your aptitude for problem solving in a proactive way."))
        mData.add(Feedback("Matt Damon", "I really think you have a superpower around making new hires feel welcome."))
        this.postValue(mData)
    }

    val steps = MutableLiveData<List<Step>>()

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
        if (isMyInstruction.value!!)
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(2) }
        else isFavorite.value = !isFavorite.value!!
    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: Int)
    }

}