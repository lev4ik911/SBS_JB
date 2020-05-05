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
    val name = MutableLiveData("")
    val description = MutableLiveData("")
    val rating = MutableLiveData(0)
    val isFavorite = MutableLiveData(true)
    val isMyInstruction = MutableLiveData(true)
    val feedback = MutableLiveData<List<Feedback>>().apply {
        val mData = ArrayList<Feedback>()
        mData.add(
            Feedback(
                "Charlize Theron",
                "Something I really appreciate about you is your aptitude for problem solving in a proactive way."
            )
        )
        mData.add(
            Feedback(
                "Matt Damon",
                "I really think you have a superpower around making new hires feel welcome."
            )
        )
        this.postValue(mData)
    }

    val steps = MutableLiveData<List<Step>>()

    fun loadInstruction(instructionId: Int) {
        if (instructionId != 0) {
            val mData = ArrayList<Step>()
            if (instructionId.rem(2) == 0) {
                name.value = "Отпадный шашлычок!"
                description.value = "Отпадный шашлычок (desc)!"
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
            } else {
                name.value = "Как спастись от коронавируса?"
                description.value = "Врачи назвали основные способы защиты от смертельной инфекции"
                mData.add(
                    Step(
                        1,
                        description = "Старайтесь не выходить из дома без необходимости"
                        //description = "Вирус распространяется в общественных местах — старайтесь их избегать. Домашний режим особенно важно соблюдать людям старше 65 лет и тем, кто страдает хроническими заболеваниями. Молодым стоит воздержаться от личного общения с родителями, бабушками и дедушками и пожилыми людьми вообще. Старайтесь поддерживать контакты по телефону или через интернет — это поможет уберечь пожилых людей от опасности заражения."
                    )
                )
                mData.add(
                    Step(
                        2,
                        description = "Соблюдайте дистанцию в общественных местах"
                    )
                )
                mData.add(
                    Step(
                        3,
                        description = "Регулярно мойте руки"
                    )
                )
                mData.add(
                    Step(
                        4,
                        description = "По возможности не трогайте руками глаза, нос и рот"
                    )
                )
                mData.add(
                    Step(
                        5,
                        description = "Соблюдайте правила респираторной гигиены"
                    )
                )
                mData.add(
                    Step(
                        6,
                        description = "При повышении температуры, кашле и затруднении дыхания обращайтесь к врачам"
                    )
                )
            }
            steps.postValue(mData)
        }
    }

    fun onActionButtonClick() {
        if (isMyInstruction.value!!)
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(2) }
        else isFavorite.value = !isFavorite.value!!
    }

    fun onRatingUpButtonClick() {
        rating.value = rating.value?.plus(1)
    }

    fun onRatingDownButtonClick() {
        rating.value = rating.value?.minus(1)
    }
    fun onOpenProfileClick() {
        //get profileId from instruction
        eventsDispatcher.dispatchEvent { onOpenProfile(1) }//TODO
    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: Int)
        fun onOpenProfile(profileId: Int)
    }

}