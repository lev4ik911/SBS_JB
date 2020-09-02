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
            isMyInstruction.value = localStorage.userId == it.authorId
            isFavorite.value = it.isFavorite
        }
    }

    val steps = MutableLiveData<List<Step>>(mutableListOf())

    var oldGuideline = Guideline()
    var oldSteps = listOf<Step>()

    val updatedStepId = MutableLiveData("")
    val starsCount = MutableLiveData("").apply {
        //  value = Html.fromHtml("&#9733; ").toString() + "12345"
    }
    val feedback = MutableLiveData<MutableList<Feedback>>(mutableListOf())

    @OptIn(UnstableDefault::class)
    @ImplicitReflectionSerializer
    fun loadGuideline(instructionId: String, forceRefresh: Boolean) {
        if (instructionId != "") {
            loading.value = true
            viewModelScope.launch {
                try {
                    val guidelinesLiveData = repository.getGuideline(instructionId, forceRefresh)
                    guidelinesLiveData.addObserver {
                        if (it.isNotEmpty) {
                            guideline.value = it.data!!
                            if (it.error != null) {
                                offlineMode.value = true
                            }
                            else if (it.isSuccess) {
                                checkPreviewImageForGuideline(guideline.value)
                                if (forceRefresh)
                                    offlineMode.value = false
                            }
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
                        if (it.isNotEmpty) {
                            steps.value = it.data!!
                            if (it.error != null) {
                                offlineMode.value = true
                            }
                            else if (it.isSuccess) {
                                checkPreviewImageForSteps(steps.value)
                                if (forceRefresh)
                                    offlineMode.value = false
                            }

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
                        if (it.isNotEmpty) {
                            if (it.error != null) {
                                offlineMode.value = true
                            }
                            else if (forceRefresh && it.isSuccess) {
                                offlineMode.value = false
                            }
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
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onActionButtonClick() {
        if (isMyInstruction.value) {
            if (!steps.value.isNullOrEmpty())
                oldSteps = steps.value.map { it.copy() }
            oldGuideline = guideline.value.copy()
            eventsDispatcher.dispatchEvent { onCallInstructionEditor(guideline.value.id) }
        } else {
            if (localStorage.userId.isNotEmpty()) {
                val currentGuideline = guideline.value
                if(!localStorage.userId.equals(currentGuideline.authorId)) {
                    isFavorite.value = !isFavorite.value
                    if (isFavorite.value)
                        addGuidelineToFavorite(currentGuideline.id)
                    else
                        removeGuidelineFromFavorite(currentGuideline.id)
                }
                else{
                    //TODO(Add message "it is user gideline")
                }

            } else {
                eventsDispatcher.dispatchEvent { onAuthorizationRequired() }
            }

        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun onRatingUpButtonClick() {
        eventsDispatcher.dispatchEvent {
            if (localStorage.userId.isNotEmpty()) {
                onRatingUpAction()
            } else {
                onAuthorizationRequired()
            }
        }
    }


    fun onRatingDownButtonClick() {
        eventsDispatcher.dispatchEvent {
            if (localStorage.userId.isNotEmpty()) {
                onRatingDownAction()
            } else {
                onAuthorizationRequired()
            }
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

    fun onEditFeedbackClick(feedback: Feedback) {

    }

    fun onOpenProfileClick() {
        //get profileId from instruction
        eventsDispatcher.dispatchEvent { onOpenProfile(guideline.value.authorId) }//TODO
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
                    descr = step.descr,
                    weight = step.weight,
                    imagePath = step.imagePath
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

    fun returnOldGuideline(){
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
                    val resultGuideline = result.data!!
                    guideline.value = Guideline(
                        id = resultGuideline.id,
                        name = resultGuideline.name,
                        descr = resultGuideline.description.orEmpty(),
                        author = resultGuideline.activity.createdBy.name,
                        authorId = resultGuideline.activity.createdBy.id,
                        imagePath = newGuideline.imagePath
                    )
                    if (newGuideline.imagePath.isNotEmpty()) {
                        eventsDispatcher.dispatchEvent { getGuidelineImageData() }
                    }
                    saveSteps()

                    //TODO(Add to total res)
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    returnOldGuideline()
                    showToast(
                        ToastMessage(result.error.message.toString(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
            } catch (e: Exception) {
                returnOldGuideline()
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
        loading.value = true
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
                    if (guideline.isImageNotUploaded) {
                        eventsDispatcher.dispatchEvent { getGuidelineImageData() }
                    }
                    saveSteps()
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    returnOldGuideline()
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
            } catch (e: Exception) {
                returnOldGuideline()
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
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
                    eventsDispatcher.dispatchEvent { deleteImagesOnDevice(guideline.id) }
                    //TODO(Add to total res)
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterDeleteAction() }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
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
                if (result.isSuccess && result.isNotEmpty) {
                    val savedSteps = result.data!!
                    newSteps.forEach {
                        savedSteps.firstOrNull{st -> st.weight == it.weight}?.let { ns ->
                            it.stepId = ns.id
                        }
                    }
                    checkSavedStepsForAvailabilityImage(newSteps)
                }
                else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.value = false
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
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
                if (result.isSuccess && result.isNotEmpty) {
                    checkSavedStepsForAvailabilityImage(updatedSteps)
                }
                if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.value = false
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
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
                    eventsDispatcher.dispatchEvent { deleteImagesOnDevice(guidelineId, step.stepId) }
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
                eventsDispatcher.dispatchEvent { onAfterSaveStepAction() }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun addGuidelineToFavorite(guidelineId: String) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.addGuidelineToFavorite(guidelineId)
                if (result.isSuccess && result.isNotEmpty) {
                    guideline.value.isFavorite = true
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun removeGuidelineFromFavorite(guidelineId: String) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.removeGuidelineFromFavorite(guidelineId)
                if (result.isSuccess && result.isNotEmpty) {
                    guideline.value.isFavorite = false
                } else if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
                    )
                }
                loading.postValue(result.status == Response.Status.LOADING)
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(e.message.orEmpty(), MessageType.ERROR)
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private fun checkPreviewImageForSteps(source: List<Step>) {
        viewModelScope.launch {
            try {
                source.forEach {
                    if (!it.isEmptyPreview) {
                        repository.checkPreviewImageForStep(guideline.value.id, it)?.let { url ->
                            eventsDispatcher.dispatchEvent {loadImage(url, guideline.value.id, it.stepId, it.remoteImageId, it)}
                        }
                    }
                    else {
                        //TODO("Add check removed images")
                    }
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private fun checkPreviewImageForGuideline(source: Guideline) {
        viewModelScope.launch {
            try {
                if (!source.isEmptyPreview) {
                    repository.checkPreviewImageForGuideline(source)?.let { url ->
                        eventsDispatcher.dispatchEvent { loadImage(url, source.id, remoteImageId = source.remoteImageId, source = source) }
                    }
                }
                else {
                    //TODO("Add check removed images")
                }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun saveGuidelinePreviewImageInLocalDB(data: Guideline){
        viewModelScope.launch {
            try {
                repository.saveGuidelineImageInLocalDB(data)
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun saveStepPreviewImageInLocalDB(data: Step){
        viewModelScope.launch {
            try {
                repository.saveStepImageInLocalDB(guideline.value.id, data)
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun uploadGuidelineImage(data: ByteArray) {
        viewModelScope.launch {
            try {
                val result = repository.uploadGuidelineImage(guideline.value, data)
                if (result.isSuccess && result.isNotEmpty) {
                    val guideline = guideline.value
                    eventsDispatcher.dispatchEvent {transferTempImage(guideline.imagePath,
                                                                        guideline.id,
                                                                        remoteImageId = guideline.remoteImageId,
                                                                        source = guideline)}
                }
                if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
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
    fun uploadStepImage(step: Step, data: ByteArray) {
        viewModelScope.launch {
            try {
                val result = repository.uploadStepImage(guideline.value.id, step, data)
                if (result.isSuccess && result.isNotEmpty) {
                    eventsDispatcher.dispatchEvent {transferTempImage(step.imagePath,
                        guidelineId = guideline.value.id,
                        stepId = step.stepId,
                        remoteImageId = step.remoteImageId,
                        source = step)}
                }
                if (result.error != null) eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(result.error.message.orEmpty(), MessageType.ERROR)
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
    private fun checkSavedStepsForAvailabilityImage(steps: List<Step>) {
        steps.forEach {
            if (it.isImageNotUploaded && it.stepId.isNotEmpty()) {
                eventsDispatcher.dispatchEvent { getStepImageData(it) }
            }
        }
    }

    override fun onCleared() {

    }

    interface EventsListener {
        fun onCallInstructionEditor(instructionId: String)
        fun onOpenProfile(profileId: String)
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
        fun showToast(msg: ToastMessage)
        fun onAuthorizationRequired()
        fun loadImage(url: String, guidelineId: String, stepId: String = "", remoteImageId: String, source: Any)
        fun getGuidelineImageData()
        fun getStepImageData(step: Step)
        fun deleteImagesOnDevice(guidelineId :String, stepId :String = "")
        fun transferTempImage(localPath: String, guidelineId: String, stepId: String = "", remoteImageId: String, source: Any)
    }
}