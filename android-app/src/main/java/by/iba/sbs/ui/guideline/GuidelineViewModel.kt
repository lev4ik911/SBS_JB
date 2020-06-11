package by.iba.sbs.ui.guideline

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.iba.mvvmbase.BaseViewModel
import by.iba.mvvmbase.MessageType
import by.iba.mvvmbase.dispatcher.EventsDispatcher
import by.iba.mvvmbase.dispatcher.EventsDispatcherOwner
import by.iba.mvvmbase.dispatcher.eventsDispatcherOnMain
import by.iba.mvvmbase.model.ToastMessage
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Feedback
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.model.request.RatingCreate
import by.iba.sbs.library.repository.GuidelineRepository
import by.iba.sbs.library.service.LocalSettings
import com.russhwolf.settings.AndroidSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import java.util.*


class GuidelineViewModel(context: Context) : BaseViewModel(),
    EventsDispatcherOwner<GuidelineViewModel.EventsListener> {
    override val eventsDispatcher: EventsDispatcher<EventsListener> = eventsDispatcherOnMain()

    private val localStorage: LocalSettings by lazy {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val settings = AndroidSettings(sharedPrefs)
        LocalSettings(settings)
    }

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }
    val steps = MutableLiveData<List<Step>>()
    var oldSteps = listOf<Step>()
    val updatedStepId = MutableLiveData("")

    val ratingUp = MutableLiveData(0)
    val ratingDown = MutableLiveData(0)
    val guideline = MutableLiveData(Guideline()).apply {
        observeForever {
            ratingUp.value = it.rating.positive
            ratingDown.value = it.rating.negative
        }
    }
    var oldGuideline = Guideline()
    val isFavorite = MutableLiveData(true)
    val isMyInstruction = MutableLiveData(true)
    val feedback = MutableLiveData<List<Feedback>>().apply {
//        val mData = ArrayList<Feedback>()
//        mData.add(
//            Feedback(
//                "Charlize Theron",
//                "Something I really appreciate about you is your aptitude for problem solving in a proactive way."
//            )
//        )
//        mData.add(
//            Feedback(
//                "Matt Damon",
//                "I really think you have a superpower around making new hires feel welcome."
//            )
//        )
//        this.postValue(mData)
    }


    @UnstableDefault
    @OptIn(ImplicitReflectionSerializer::class)
    fun loadInstruction(instructionId: String, forceRefresh: Boolean) {
        if (instructionId != "") {
            isLoading.value = true
            val mData = ArrayList<Step>()
            viewModelScope.launch {
                try {
                    val guidelinesLiveData = repository.getGuideline(instructionId, forceRefresh)
                    guidelinesLiveData.addObserver {
                        if (it.isSuccess && it.isNotEmpty) {
                            guideline.value = it.data!!
                        } else if (it.error != null) notificationsQueue.value =
                            ToastMessage(it.error!!.toString(), MessageType.ERROR)
                    }
                } catch (e: Exception) {
                    notificationsQueue.value =
                        ToastMessage(e.toString(), MessageType.ERROR)
                }
            }
            viewModelScope.launch {
                try {
                    val stepsLiveData = repository.getAllSteps(instructionId, forceRefresh)
                    stepsLiveData.addObserver {
                        if (it.isSuccess && it.isNotEmpty) {
                            if (forceRefresh) {
                                //check image info on actual
                                val tempListOfSteps = it.data!!.toMutableList()
                                tempListOfSteps.forEach {
                                    it.updateImageTimeSpan = 1 //TODO (delete fake data!!!)
                                    if (it.updateImageTimeSpan != 0) {
                                        viewModelScope.launch {
                                            val stepFromLocalDB =
                                                repository.getStepByIdFromLocalDB(
                                                    instructionId,
                                                    it.stepId
                                                )
                                            if (stepFromLocalDB.updateImageTimeSpan != it.updateImageTimeSpan) {
                                                eventsDispatcher.dispatchEvent {
                                                    onLoadImageFromAPI(
                                                        it
                                                    )
                                                }
                                            }
                                            it.imagePath = stepFromLocalDB.imagePath
                                            it.updateImageTimeSpan =
                                                stepFromLocalDB.updateImageTimeSpan
                                        }
                                    }
                                }
                                steps.value = tempListOfSteps
                            } else
                                steps.value = it.data!!
                        } else if (it.error != null) notificationsQueue.value =
                            ToastMessage(it.error!!.toString(), MessageType.ERROR)
                        isLoading.postValue(it.status == Response.Status.LOADING)
                    }
                } catch (e: Exception) {
                    notificationsQueue.value =
                        ToastMessage(e.toString(), MessageType.ERROR)
                }
            }
            viewModelScope.launch {
                try {
                    val feedbacksLiveData = repository.getAllFeedbacks(instructionId, forceRefresh)
                    feedbacksLiveData.addObserver {
                        if (it.isSuccess && it.isNotEmpty) {
                            feedback.value = it.data!!
                        } else if (it.error != null) notificationsQueue.value =
                            ToastMessage(it.error!!.toString(), MessageType.ERROR)
                    }
                } catch (e: Exception) {
                    notificationsQueue.value =
                        ToastMessage(e.toString(), MessageType.ERROR)
                }
            }
//            if (instructionId.rem(2) == 0) {
//                name.value = "Отпадный шашлычок!"
//                description.value = "Отпадный шашлычок (desc)!"
//                mData.add(
//                    Step(
//                        1,
//                        name = "Свиную шею нарезать одинаковыми кусочками, не мелкими."
//                    )
//                )
//                mData.add(
//                    Step(
//                        2,
//                        name = " Лук нарезать тонкими кольцами и помять руками (или измельчить лук с помощью кухонной техники)."
//                    )
//                )
//                mData.add(
//                    Step(
//                        3,
//                        name = "Уложить на дно емкости слой мяса, сверху посолить, поперчить."
//                    )
//                )
//                mData.add(
//                    Step(
//                        4,
//                        name = " Хорошо перемешать мясо с луком и остальными ингредиентами маринада."
//                    )
//                )
//                mData.add(
//                    Step(
//                        5,
//                        name = "Оставить свинину в маринаде на несколько часов в холодильнике."
//                    )
//                )
//                mData.add(
//                    Step(
//                        6,
//                        name = " Затем нанизывать кусочки маринованного мяса на шампуры и жарить шашлык из свинины."
//                    )
//                )
//            } else {
//                name.value = "Как спастись от коронавируса?"
//                description.value = "Врачи назвали основные способы защиты от смертельной инфекции"
//                mData.add(
//                    Step(
//                        1,
//                        name = "Старайтесь не выходить из дома без необходимости",
//                        description = "Вирус распространяется в общественных местах — старайтесь их избегать. Домашний режим особенно важно соблюдать людям старше 65 лет и тем, кто страдает хроническими заболеваниями. Молодым стоит воздержаться от личного общения с родителями, бабушками и дедушками и пожилыми людьми вообще. Старайтесь поддерживать контакты по телефону или через интернет — это поможет уберечь пожилых людей от опасности заражения."
//                    )
//                )
//                mData.add(
//                    Step(
//                        2,
//                        name = "Соблюдайте дистанцию в общественных местах",
//                        description = "Кашляя или чихая, человек с респираторной инфекцией, такой как COVID-19, распространяет вокруг себя мельчайшие капли, содержащие вирус. Если вы находитесь слишком близко, то можете заразиться вирусом при вдыхании воздуха. Держитесь от людей на расстоянии как минимум один метр, особенно если у кого-то из них кашель, насморк или повышенная температура."
//                    )
//                )
//                mData.add(
//                    Step(
//                        3,
//                        name = "Регулярно мойте руки",
//                        description = "Если на поверхности рук есть вирус, то обработка спиртосодержащим средством или мытье рук с мылом убьет его."
//                    )
//                )
//                mData.add(
//                    Step(
//                        4,
//                        name = "По возможности не трогайте руками глаза, нос и рот",
//                        description = "Руки касаются многих поверхностей, на которых может присутствовать вирус. Прикасаясь к глазам, носу или рту, можно перенести вирус с кожи рук в организм."
//                    )
//                )
//                mData.add(
//                    Step(
//                        5,
//                        name = "Соблюдайте правила респираторной гигиены",
//                        description = "При кашле и чиханье прикрывайте рот и нос салфеткой или сгибом локтя; сразу выбрасывайте салфетку в контейнер для мусора с крышкой, обрабатывайте руки спиртосодержащим антисептиком или мойте их водой с мылом."
//                    )
//                )
//                mData.add(
//                    Step(
//                        6,
//                        name = "При повышении температуры, кашле и затруднении дыхания обращайтесь к врачам",
//                        description = "Повышение температуры, кашель и затруднение дыхания могут быть вызваны респираторной инфекцией или другим серьезным заболеванием. Симптомы поражения органов дыхания в сочетании с повышением температуры могут иметь самые разные причины, среди которых, в зависимости от поездок и контактов пациента, может быть и коронавирус."
//                    )
//                )
//            }
//            steps.postValue(mData)
        }
    }

    fun onActionButtonClick() {
        if (isMyInstruction.value!!) {
            if (!steps.value.isNullOrEmpty())
                oldSteps = steps.value?.map { it.copy() }!!
            oldGuideline = guideline.value!!.copy()
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(guideline.value!!.id) }
        } else isFavorite.value = !isFavorite.value!!
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onRatingUpButtonClick() {
        eventsDispatcher.dispatchEvent {
            onRatingUpAction()
        }
    }


    fun onRatingDownButtonClick() {
        eventsDispatcher.dispatchEvent {
            onRatingDownAction()
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun createFeedback(feedback: RatingCreate) {
        viewModelScope.launch {
            repository.insertRating(guideline.value!!.id, feedback)
        }
        if (feedback.rating > 0)
            ratingUp.value = ratingUp.value?.plus(1)
        else
            ratingDown.value = ratingDown.value?.plus(1)
    }

    fun onOpenProfileClick() {
        //get profileId from instruction
        eventsDispatcher.dispatchEvent { onOpenProfile(1) }//TODO
    }


    fun onPreviewStepClick(view: View, step: Step) {
        eventsDispatcher.dispatchEvent { onPreviewStepAction(view, step) }
    }

    fun onPreviewStepCloseClick() {
        eventsDispatcher.dispatchEvent { onClosePreviewStepAction() }
    }

    fun onPreviewStepNextClick(currentStep: Step) {
        eventsDispatcher.dispatchEvent { onPreviewStepNextAction(currentStep) }
    }

    fun onPreviewStepPreviousClick(currentStep: Step) {
        eventsDispatcher.dispatchEvent { onPreviewStepPreviousAction(currentStep) }
    }

    fun onEditStepClick(step: Step) {
        eventsDispatcher.dispatchEvent { onEditStep(step.weight) }
    }

    fun onEditStepImageClick(step: Step) {
        eventsDispatcher.dispatchEvent { onEditStepImage(step) }
    }

    fun onEditGuidelineImageClick() {
        eventsDispatcher.dispatchEvent { onEditGuidelineImage() }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onSaveAction() {
        isLoading.value = true
        if (guideline.value!!.id.isEmpty())
            insertInstruction(guideline.value!!)
        else
            updateInsruction(guideline.value!!)
        eventsDispatcher.dispatchEvent { onAfterSaveAction() }
    }

    fun onSaveStepAction(step: Step) {
        if (step.stepId.isEmpty() && !steps.value!!.contains(step)){
            val stepArr = steps.value?.toMutableList()
            stepArr?.add(
                Step(
                    name = step.name,
                    description = step.description,
                    weight = step.weight
                )
            )
            steps.value = stepArr
        }
        eventsDispatcher.dispatchEvent { onAfterSaveStepAction() }
    }

    fun onRemoveInstructionClick() {
        eventsDispatcher.dispatchEvent { onRemoveInstruction() }
    }

    fun onRemoveStepClick(step: Step) {
        eventsDispatcher.dispatchEvent { onRemoveStep(step) }
    }

    fun onBackBtnClick() {
        steps.value = oldSteps
        guideline.value = oldGuideline
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun insertInstruction(newGuideline: Guideline) {
        viewModelScope.launch {
            try {
                val result = repository.insertGuideline(newGuideline)
                if (result.isSuccess && result.isNotEmpty) {
                    notificationsQueue.value =
                        ToastMessage("Successful saved", MessageType.SUCCESS)
                    //TODO(Add to total res)
                    val resultGuideline = result.data!!
                    guideline.value = Guideline(
                        id = resultGuideline.id,
                        name = resultGuideline.name,
                        description = resultGuideline.description ?: ""
                    )
                    saveSteps()
                } else if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun updateInsruction(guideline: Guideline) {
        viewModelScope.launch {
            try {
                val result = repository.updateGuideline(guideline)
                if (result.isSuccess && result.isNotEmpty) {
                    notificationsQueue.value =
                        ToastMessage("Successful saved", MessageType.SUCCESS)
                    //TODO(Add to total res)
                    saveSteps()
                } else if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun deleteInstruction(guideline: Guideline) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteGuideline(guideline.id)
                if (result.isSuccess) {
                    notificationsQueue.value =
                        ToastMessage("Successful delete", MessageType.SUCCESS)
                    //TODO(Add to total res)
                } else if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
                isLoading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterDeleteAction() }
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun saveSteps(){
        //insert / update steps
        var isNeedSaving = false
        steps.value?.filter{step-> step.stepId.isEmpty()}.apply {
            if (!this.isNullOrEmpty()) {
                isNeedSaving = true
                insertSteps(guideline.value!!.id, this)
            }
        }
        steps.value?.filter{step-> !step.stepId.isEmpty() && !oldSteps.contains(step)}.apply {
            if (!this.isNullOrEmpty()) {
                isNeedSaving = true
                updateSteps(guideline.value!!.id, this)
            }
        }
        if (!isNeedSaving)
            isLoading.value = false
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun insertSteps(guidelineId: String, newSteps: List<Step>) {
        viewModelScope.launch {
            try {
                val result = repository.insertSteps(guidelineId, newSteps)
                if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
                isLoading.value = false
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun updateSteps(guidelineId: String, updatedSteps: List<Step>) {
        viewModelScope.launch {
            try {
                val result = repository.updateSteps(guidelineId, updatedSteps)
                if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
                isLoading.value = false
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun deleteStep(guidelineId: String, step: Step) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteStep(guidelineId, step.stepId)
                if (result.isSuccess) {
                    notificationsQueue.value =
                        ToastMessage("Successful delete", MessageType.SUCCESS)
                    //TODO(Add to total res)
                    val stepArr = steps.value?.toMutableList()
                    stepArr?.remove(step)
                    stepArr?.forEachIndexed { index, step -> step.weight = index + 1 }
                    steps.value = stepArr
                } else if (result.error != null) notificationsQueue.value =
                    ToastMessage(result.error!!.toString(), MessageType.ERROR)
                isLoading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterSaveStepAction() }
            } catch (e: Exception) {
                notificationsQueue.value =
                    ToastMessage(e.toString(), MessageType.ERROR)
            }
        }
    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: String)
        fun onOpenProfile(profileId: Int)
        fun onEditStep(stepWeight: Int)
        fun onEditStepImage(step: Step)
        fun onEditGuidelineImage()
        fun onPreviewStepAction(view: View, step: Step)
        fun onClosePreviewStepAction()
        fun onPreviewStepNextAction(currentStep: Step)
        fun onPreviewStepPreviousAction(currentStep: Step)
        fun onAfterSaveAction()
        fun onAfterDeleteAction()
        fun onRemoveInstruction()
        fun onAfterSaveStepAction()
        fun onRatingDownAction()
        fun onRatingUpAction()
        fun onRemoveStep(step: Step)
        fun onLoadImageFromAPI(step: Step)
    }
}