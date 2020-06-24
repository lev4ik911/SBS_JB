package by.iba.sbs.library.viewmodel


import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.*
import by.iba.sbs.library.model.request.RatingCreate
import by.iba.sbs.library.model.response.RatingSummary
import by.iba.sbs.library.repository.GuidelineRepository
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault


class GuidelineViewModel(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<GuidelineViewModel.EventsListener> {

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    val ratingUp = MutableLiveData(0)
    val ratingDown = MutableLiveData(0)
    val isFavorite = MutableLiveData(true)
    val isMyInstruction = MutableLiveData(true)

    val guideline = MutableLiveData(Guideline()).apply {
        addObserver {
            ratingUp.value = it.rating.positive
            ratingDown.value = it.rating.negative
        }
    }

    val steps = MutableLiveData<List<Step>>(mutableListOf())

    var oldGuideline = Guideline()
    var oldSteps = listOf<Step>()

    val updatedStepId = MutableLiveData("")
    val starsCount = MutableLiveData("").apply {
        //  value = Html.fromHtml("&#9733; ").toString() + "12345"
    }
    val feedback = MutableLiveData<MutableList<Feedback>>(mutableListOf()).apply {
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


    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    fun loadGuideline(instructionId: String, forceRefresh: Boolean) {
        if (instructionId != "") {
            loading.value = true
            viewModelScope.launch {
                try {
                    val guidelinesLiveData = repository.getGuideline(instructionId, forceRefresh)
                    guidelinesLiveData.addObserver {
                        if (it.isSuccess && it.isNotEmpty) {
                            guideline.value = it.data!!
                        } else if (it.error != null)
                            eventsDispatcher.dispatchEvent {
                                showToast(
                                    ToastMessage(it.error.message.toString(), MessageType.ERROR)
                                )
                            }
                    }
                } catch (e: Exception) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage(e.message.toString(), MessageType.ERROR)
                        )
                    }
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
                                tempListOfSteps.forEach { step ->
                                    step.updateImageTimeSpan = 1 //TODO (delete fake data!!!)
                                    if (step.updateImageTimeSpan != 0) {
                                        viewModelScope.launch {
                                            val stepFromLocalDB =
                                                repository.getStepByIdFromLocalDB(
                                                    instructionId,
                                                    step.stepId
                                                )
                                            if (stepFromLocalDB.updateImageTimeSpan != step.updateImageTimeSpan) {
                                                eventsDispatcher.dispatchEvent {
                                                    onLoadImageFromAPI(
                                                        step
                                                    )
                                                }
                                            }
                                            step.imagePath = stepFromLocalDB.imagePath
                                            step.updateImageTimeSpan =
                                                stepFromLocalDB.updateImageTimeSpan
                                        }
                                    }
                                }
                                steps.value = tempListOfSteps
                            } else
                                steps.value = it.data!!
                        } else if (it.error != null) eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage(it.error.message.toString(), MessageType.ERROR)
                            )
                        }
                        loading.postValue(it.status == Response.Status.LOADING)
                    }
                } catch (e: Exception) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage(e.message.toString(), MessageType.ERROR)
                        )
                    }
                }
            }
            viewModelScope.launch {
                try {
                    val feedbackLiveData = repository.getAllFeedbacks(instructionId, forceRefresh)
                    feedbackLiveData.addObserver {
                        if (it.isSuccess && it.isNotEmpty) {
                            feedback.value = it.data!!.toMutableList()
                        } else if (it.error != null) eventsDispatcher.dispatchEvent {
                            showToast(
                                ToastMessage(it.error.message.toString(), MessageType.ERROR)
                            )
                        }
                    }
                } catch (e: Exception) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage(e.message.toString(), MessageType.ERROR)
                        )
                    }
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
        if (isMyInstruction.value) {
            if (!steps.value.isNullOrEmpty())
                oldSteps = steps.value.map { it.copy() }
            oldGuideline = guideline.value.copy()
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(guideline.value.id) }
        } else isFavorite.value = !isFavorite.value
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
    fun createFeedback(newFeedback: RatingCreate) {
        if (newFeedback.rating > 0)
            ratingUp.value = ratingUp.value.plus(1)
        else
            ratingDown.value = ratingDown.value.plus(1)

        val ratingSummary = RatingSummary(
            positive = ratingUp.value,
            negative = ratingDown.value,
            overall = ratingUp.value - ratingDown.value
        )
        viewModelScope.launch {
            val response = repository.insertRating(guideline.value.id, newFeedback, ratingSummary)
            if (response.isSuccess && response.isNotEmpty) {
                response.data?.let {
                    val feedbackList = feedback.value
                    feedbackList.add(Feedback(it.id, it.rating, it.comment))
                    feedback.value = feedbackList
                }
            }
        }
    }

    fun onOpenProfileClick() {
        //get profileId from instruction
        eventsDispatcher.dispatchEvent { onOpenProfile(1) }//TODO
    }


    fun onPreviewStepClick(step: Step) {
        eventsDispatcher.dispatchEvent { onPreviewStepAction(step) }
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
        loading.value = true
        if (guideline.value.id.isEmpty())
            insertGuideline(guideline.value)
        else
            updateGuideline(guideline.value)
        eventsDispatcher.dispatchEvent { onAfterSaveAction() }
    }

    fun onSaveStepAction(step: Step) {
        if (step.stepId.isEmpty() && !steps.value.contains(step)) {
            val stepArr = steps.value.toMutableList()
            stepArr.add(
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

    fun onRemoveGuidelineClick() {
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
    fun insertGuideline(newGuideline: Guideline) {
        viewModelScope.launch {
            try {
                val result = repository.insertGuideline(newGuideline)
                if (result.isSuccess && result.isNotEmpty) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage("Successful insert", MessageType.SUCCESS)
                        )
                    }
                    //TODO(Add to total res)
                    val resultGuideline = result.data!!
                    guideline.value = Guideline(
                        id = resultGuideline.id,
                        name = resultGuideline.name,
                        description = resultGuideline.description ?: ""
                    )
                    saveSteps()
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun updateGuideline(guideline: Guideline) {
        viewModelScope.launch {
            try {
                val result = repository.updateGuideline(guideline)
                if (result.isSuccess && result.isNotEmpty) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage("Successful update", MessageType.SUCCESS)
                        )
                    }
                    //TODO(Add to total res)
                    saveSteps()
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun deleteInstruction(guideline: Guideline) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteGuideline(guideline.id)
                if (result.isSuccess) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage("Successful delete", MessageType.SUCCESS)
                        )
                    }
                    //TODO(Add to total res)
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterDeleteAction() }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun saveSteps() {
        //insert / update steps
        var isNeedSaving = false
        steps.value.filter { step -> step.stepId.isEmpty() }.apply {
            if (!this.isNullOrEmpty()) {
                isNeedSaving = true
                insertSteps(guideline.value.id, this)
            }
        }
        steps.value.filter { step -> !step.stepId.isEmpty() && !oldSteps.contains(step) }.apply {
            if (!this.isNullOrEmpty()) {
                isNeedSaving = true
                updateSteps(guideline.value.id, this)
            }
        }
        if (!isNeedSaving)
            loading.value = false
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun insertSteps(guidelineId: String, newSteps: List<Step>) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.insertSteps(guidelineId, newSteps)
                if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
                loading.value = false
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun updateSteps(guidelineId: String, updatedSteps: List<Step>) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.updateSteps(guidelineId, updatedSteps)
                if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
                loading.value = false
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun deleteStep(guidelineId: String, step: Step) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteStep(guidelineId, step.stepId)
                if (result.isSuccess) {
                    eventsDispatcher.dispatchEvent {
                        showToast(
                            ToastMessage("Successful delete", MessageType.SUCCESS)
                        )
                    }
                    //TODO(Add to total res)
                    val stepArr = steps.value.toMutableList()
                    stepArr.remove(step)
                    stepArr.forEachIndexed { index, step -> step.weight = index + 1 }
                    steps.value = stepArr
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterSaveStepAction() }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.toString(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: String)
        fun onOpenProfile(profileId: Int)
        fun onEditStep(stepWeight: Int)
        fun onEditStepImage(editStep: Step)
        fun onEditGuidelineImage()
        fun onPreviewStepAction(step: Step)
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
        fun showToast(msg: ToastMessage)
    }
}