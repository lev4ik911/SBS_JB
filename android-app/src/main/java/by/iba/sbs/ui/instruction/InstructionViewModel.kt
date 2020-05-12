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
    val ratingUp = MutableLiveData(0)
    val ratingDown = MutableLiveData(0)

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
    var oldSteps = listOf<Step>()

    fun loadInstruction(instructionId: Int) {
        if (instructionId != 0) {
            val mData = ArrayList<Step>()
            if (instructionId.rem(2) == 0) {
                name.value = "Отпадный шашлычок!"
                description.value = "Отпадный шашлычок (desc)!"
                mData.add(
                    Step(
                        1,
                        name = "Свиную шею нарезать одинаковыми кусочками, не мелкими."
                    )
                )
                mData.add(
                    Step(
                        2,
                        name = " Лук нарезать тонкими кольцами и помять руками (или измельчить лук с помощью кухонной техники)."
                    )
                )
                mData.add(
                    Step(
                        3,
                        name = "Уложить на дно емкости слой мяса, сверху посолить, поперчить."
                    )
                )
                mData.add(
                    Step(
                        4,
                        name = " Хорошо перемешать мясо с луком и остальными ингредиентами маринада."
                    )
                )
                mData.add(
                    Step(
                        5,
                        name = "Оставить свинину в маринаде на несколько часов в холодильнике."
                    )
                )
                mData.add(
                    Step(
                        6,
                        name = " Затем нанизывать кусочки маринованного мяса на шампуры и жарить шашлык из свинины."
                    )
                )
            } else {
                name.value = "Как спастись от коронавируса?"
                description.value = "Врачи назвали основные способы защиты от смертельной инфекции"
                mData.add(
                    Step(
                        1,
                        name = "Старайтесь не выходить из дома без необходимости",
                        description = "Вирус распространяется в общественных местах — старайтесь их избегать. Домашний режим особенно важно соблюдать людям старше 65 лет и тем, кто страдает хроническими заболеваниями. Молодым стоит воздержаться от личного общения с родителями, бабушками и дедушками и пожилыми людьми вообще. Старайтесь поддерживать контакты по телефону или через интернет — это поможет уберечь пожилых людей от опасности заражения."
                    )
                )
                mData.add(
                    Step(
                        2,
                        name = "Соблюдайте дистанцию в общественных местах",
                        description = "Кашляя или чихая, человек с респираторной инфекцией, такой как COVID-19, распространяет вокруг себя мельчайшие капли, содержащие вирус. Если вы находитесь слишком близко, то можете заразиться вирусом при вдыхании воздуха. Держитесь от людей на расстоянии как минимум один метр, особенно если у кого-то из них кашель, насморк или повышенная температура."
                    )
                )
                mData.add(
                    Step(
                        3,
                        name = "Регулярно мойте руки",
                        description = "Если на поверхности рук есть вирус, то обработка спиртосодержащим средством или мытье рук с мылом убьет его."
                    )
                )
                mData.add(
                    Step(
                        4,
                        name = "По возможности не трогайте руками глаза, нос и рот",
                        description = "Руки касаются многих поверхностей, на которых может присутствовать вирус. Прикасаясь к глазам, носу или рту, можно перенести вирус с кожи рук в организм."
                    )
                )
                mData.add(
                    Step(
                        5,
                        name = "Соблюдайте правила респираторной гигиены",
                        description = "При кашле и чиханье прикрывайте рот и нос салфеткой или сгибом локтя; сразу выбрасывайте салфетку в контейнер для мусора с крышкой, обрабатывайте руки спиртосодержащим антисептиком или мойте их водой с мылом."
                    )
                )
                mData.add(
                    Step(
                        6,
                        name = "При повышении температуры, кашле и затруднении дыхания обращайтесь к врачам",
                        description = "Повышение температуры, кашель и затруднение дыхания могут быть вызваны респираторной инфекцией или другим серьезным заболеванием. Симптомы поражения органов дыхания в сочетании с повышением температуры могут иметь самые разные причины, среди которых, в зависимости от поездок и контактов пациента, может быть и коронавирус."
                    )
                )
            }
            steps.postValue(mData)
        }
    }

    fun onActionButtonClick() {
        if (isMyInstruction.value!!) {
            if (!steps.value.isNullOrEmpty())
                oldSteps = steps.value?.map { it.copy() }!!
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(2) }
        } else isFavorite.value = !isFavorite.value!!
    }

    fun onRatingUpButtonClick() {
        ratingUp.value = ratingUp.value?.plus(1)
    }

    fun onRatingDownButtonClick() {
        ratingDown.value = ratingDown.value?.plus(1)
    }

    fun onOpenProfileClick() {
        //get profileId from instruction
        eventsDispatcher.dispatchEvent { onOpenProfile(1) }//TODO
    }

    fun onEditStepClick(step: Step) {
        eventsDispatcher.dispatchEvent { onEditStep(step.stepId) }
    }

    fun onEditImageClick(step: Step) {
        eventsDispatcher.dispatchEvent { onEditImage(step.stepId) }
    }

    fun onSaveAction() {
        eventsDispatcher.dispatchEvent { onAfterSaveAction() }
    }

    fun onRemoveInstructionClick() {

    }

    fun onBackBtnClick() {
        steps.postValue(oldSteps)
    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: Int)
        fun onOpenProfile(profileId: Int)
        fun onEditStep(stepId: Int)
        fun onEditImage(stepId: Int)
        fun onAfterSaveAction()
    }

}