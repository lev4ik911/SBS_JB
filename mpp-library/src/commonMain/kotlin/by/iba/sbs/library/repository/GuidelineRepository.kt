package by.iba.sbs.library.repository

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.data.remote.*
import by.iba.sbs.library.model.Feedback
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.model.request.*
import by.iba.sbs.library.model.response.*
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.applicationDispatcher
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.time.getCurrentMilliSeconds
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IGuidelineRepository {
    suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>>
    suspend fun getGuideline(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<Guideline>>

    suspend fun getAllSteps(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Step>>>

    suspend fun getAllFeedbacks(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Feedback>>>

    suspend fun insertGuideline(data: Guideline): Response<GuidelineView>
    suspend fun updateGuideline(data: Guideline): Response<GuidelineView>
    suspend fun deleteGuideline(guidelineId: String): Response<String?>
    suspend fun insertSteps(guidelineId: String, data: List<Step>): Response<List<StepView>>
    suspend fun updateSteps(guidelineId: String, data: List<Step>): Response<List<StepView>>
    suspend fun deleteStep(guidelineId: String, stepId: String): Response<String?>
    suspend fun getStepByIdFromLocalDB(guidelineId: String, stepId: String): Step
    suspend fun insertRating(
        guidelineId: String,
        data: RatingCreate,
        ratingSummary: RatingSummary
    ): Response<RatingView>

    suspend fun updateRating(guidelineId: String, data: Feedback): Response<RatingView>
    suspend fun deleteRating(guidelineId: String, feedbackId: String): Response<String?>
    suspend fun getSuggestions(searchText: String): List<String>
    suspend fun getFilteredGuidelines(searchText: String): List<Guideline>
    @UnstableDefault
    suspend fun addGuidelineToFavorite(guidelineId: String, favorited: Int): Response<FavoriteView>
    @UnstableDefault
    suspend fun removeGuidelineFromFavorite(guidelineId: String, favorited: Int): Response<String?>
    @UnstableDefault
    suspend fun clearFavorites()

    @UnstableDefault
    suspend fun uploadGuidelineImage(guideline: Guideline, data: ByteArray): Response<FileView>
    @UnstableDefault
    suspend fun checkPreviewImageForGuideline(guideline: Guideline): String?
    @UnstableDefault
    suspend fun saveGuidelineImageInLocalDB(guideline: Guideline)
    @UnstableDefault
    suspend fun checkPreviewImageForStep(guidelineId: String, step: Step): String?
    @UnstableDefault
    suspend fun saveStepImageInLocalDB(guidelineId: String, step: Step)
    @UnstableDefault
    suspend fun uploadStepImage(
        guidelineId: String,
        step: Step,
        data: ByteArray
    ): Response<FileView>

    @UnstableDefault
    suspend fun getPopularGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>>
}

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(val settings: LocalSettings) :
    IGuidelineRepository {

    @UnstableDefault
    private val guidelines by lazy { Guidelines(settings) }

    @UnstableDefault
    private val steps by lazy { Steps(settings) }

    @UnstableDefault
    private val favorites by lazy { Favorites(settings) }

    @UnstableDefault
    private val feedback by lazy { Feedbacks(settings) }
    private val sbsDb = createDb()
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries
    private val imagesQueries = sbsDb.imagesEntityQueries
    private val ratingSummaryQueries = sbsDb.ratingSummaryQueries
    private val feedbackQueries = sbsDb.feedbackEntityQueries
    private val suggestionsQueries = sbsDb.suggestionsEntityQueries
    private val favoritesQueries = sbsDb.favoritesEntityQueries


    @UnstableDefault
    override suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {

        val favorites = favoritesQueries.selectAllGuidelinesIdFromFavorites().executeAsList()
        val imagesCache = imagesQueries.selectImagesForGuidelines().executeAsList()
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(
                        it.id,
                        it.name,
                        it.descr,
                        it.favourited.toLong(),
                        it.authorId,
                        it.author,
                        it.remoteImageId,
                        it.updateImageDateTime
                    )
                    it.rating.apply {
                        ratingSummaryQueries.insertRating(
                            it.id,
                            positive.toLong(),
                            negative.toLong(),
                            overall.toLong()
                        )
                    }
                }
            }

            override fun shouldFetch(data: List<Guideline>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Guideline> = coroutineScope {
                val ratingSummaryCache = ratingSummaryQueries.selectAllRatings().executeAsList()
                return@coroutineScope guidelinesQueries.selectAllGuidelines().executeAsList().map {
                    val rating = ratingSummaryCache.firstOrNull { rating -> rating.id == it.id }
                    if (rating != null) {
                        Guideline(
                            it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            authorId = it.authorId,
                            author = it.author,
                            isFavorite = favorites.any { fav -> fav == it.id },
                            rating = RatingSummary(
                                rating.positive!!.toInt(),
                                rating.negative!!.toInt(),
                                rating.overall!!.toInt()
                            ),
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    } else {
                        Guideline(it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            it.authorId,
                            it.author,
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath .orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    }
                }
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = guidelines.getAllGuidelines()
                    if (result.isSuccess) {
                        clearCache()
                        result.data!!.map { item ->
                            Guideline(
                                item.id,
                                item.name,
                                item.description ?: "",
                                item.favourited,
                                rating = item.rating,
                                authorId = item.activity.createdBy.id,
                                author = item.activity.createdBy.name,
                                isFavorite = favorites.any { fav -> fav == item.id },
                                imagePath = imagesCache.firstOrNull{im->im.guidelineId == item.id}?.localImagePath.orEmpty(),
                                remoteImageId = item.preview?.id ?: "",
                                updateImageDateTime = item.preview?.activity?.updatedAt ?: ""
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        loadFromDb()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun getPopularGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {

        val favorites = favoritesQueries.selectAllGuidelinesIdFromFavorites().executeAsList()
        val imagesCache = imagesQueries.selectImagesForGuidelines().executeAsList()
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(
                        it.id,
                        it.name,
                        it.descr,
                        it.favourited.toLong(),
                        it.authorId,
                        it.author,
                        it.remoteImageId,
                        it.updateImageDateTime
                    )
                    it.rating.apply {
                        ratingSummaryQueries.insertRating(
                            it.id,
                            positive.toLong(),
                            negative.toLong(),
                            overall.toLong()
                        )
                    }
                }
            }

            override fun shouldFetch(data: List<Guideline>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Guideline> = coroutineScope {
                val ratingSummaryCache = ratingSummaryQueries.selectAllRatings().executeAsList()
                return@coroutineScope guidelinesQueries.selectPopularGuidelines().executeAsList().map {
                    val rating = ratingSummaryCache.firstOrNull { rating -> rating.id == it.id }
                    if (rating != null) {
                        Guideline(
                            it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            authorId = it.authorId,
                            author = it.author,
                            isFavorite = favorites.any { fav -> fav == it.id },
                            rating = RatingSummary(
                                rating.positive!!.toInt(),
                                rating.negative!!.toInt(),
                                rating.overall!!.toInt()
                            ),
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    } else {
                        Guideline(it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            it.authorId,
                            it.author,
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath .orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    }
                }
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = guidelines.getPopularGuidelines()
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Guideline(
                                item.id,
                                item.name,
                                item.description ?: "",
                                item.favourited,
                                rating = item.rating,
                                authorId = item.activity.createdBy.id,
                                author = item.activity.createdBy.name,
                                isFavorite = favorites.any { fav -> fav == item.id },
                                imagePath = imagesCache.firstOrNull{im->im.guidelineId == item.id}?.localImagePath.orEmpty(),
                                remoteImageId = item.preview?.id ?: "",
                                updateImageDateTime = item.preview?.activity?.updatedAt ?: ""
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        loadFromDb()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun getGuideline(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<Guideline>> {
        val favorite = favoritesQueries.selectFavoriteIdByGuidelineId(guidelineId).executeAsOneOrNull()
        val imagesCashe = imagesQueries.selectImageForGuideline(guidelineId).executeAsOneOrNull()
        return object : NetworkBoundResource<Guideline, Guideline>() {
            override fun processResponse(response: Guideline): Guideline = response

            override suspend fun saveCallResults(data: Guideline) = coroutineScope {
                data.rating.apply {
                    ratingSummaryQueries.insertRating(
                        data.id,
                        positive.toLong(),
                        negative.toLong(),
                        overall.toLong()
                    )
                }
                guidelinesQueries.insertGuideline(
                    data.id,
                    data.name,
                    data.descr,
                    data.favourited.toLong(),
                    data.authorId,
                    data.author,
                    data.remoteImageId,
                    data.updateImageDateTime
                )

            }

            override fun shouldFetch(data: Guideline?): Boolean =
                data == null || forceRefresh

            override suspend fun loadFromDb(): Guideline? = coroutineScope {
                val item = guidelinesQueries.selectGuidelineById(guidelineId).executeAsOneOrNull()
                val ratingSummary =
                    ratingSummaryQueries.selectRatingByGuidelineId(guidelineId).executeAsOneOrNull()
                if (item != null) {
                    if (ratingSummary != null) {
                        Guideline(
                            item.id,
                            item.name,
                            item.description,
                            item.favourited!!.toInt(),
                            authorId = item.authorId,
                            author = item.author,
                            isFavorite = favorite != null,
                            rating = RatingSummary(
                                ratingSummary.positive!!.toInt(),
                                ratingSummary.negative!!.toInt(),
                                ratingSummary.overall!!.toInt()
                            ),

                            imagePath = imagesCashe?.localImagePath.orEmpty(),
                            remoteImageId = item.remoteImageId,
                            updateImageDateTime = item.updateImageDateTime
                        )
                    } else {
                        Guideline(
                            item.id,
                            item.name,
                            item.description,
                            item.favourited!!.toInt(),
                            authorId = item.authorId,
                            author = item.author,
                            imagePath = imagesCashe?.localImagePath.orEmpty(),
                            remoteImageId = item.remoteImageId,
                            updateImageDateTime = item.updateImageDateTime
                        )
                    }
                } else return@coroutineScope null

            }

            override fun createCallAsync(): Deferred<Guideline> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = guidelines.getGuideline(guidelineId)
                    if (result.isSuccess) {
                        val item = result.data!!
                        Guideline(
                            item.id,
                            item.name,
                            item.description!!,
                            item.favourited,
                            authorId = item.activity.createdBy.id,
                            author = item.activity.createdBy.name,
                            isFavorite = favorite != null,
                            rating = RatingSummary(
                                item.rating.positive,
                                item.rating.negative,
                                item.rating.overall
                            ),
                            imagePath = imagesCashe?.localImagePath.orEmpty(),
                            remoteImageId = item.preview?.id.orEmpty(),
                            updateImageDateTime = item.preview?.activity?.updatedAt.orEmpty()
                        )
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        loadFromDb()?: Guideline()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    private fun clearCache() {
        ratingSummaryQueries.deleteRatings()
        guidelinesQueries.deleteAllGuidelines()
        settings.lastUpdate = getCurrentMilliSeconds()
    }

    @UnstableDefault
    override suspend fun getAllSteps(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Step>>> {
        val imagesCache = imagesQueries.selectAllImagesForGuideline(guidelineId).executeAsList()
        return object : NetworkBoundResource<List<Step>, List<Step>>() {
            override fun processResponse(response: List<Step>): List<Step> = response

            override suspend fun saveCallResults(data: List<Step>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertStep(
                        it.stepId,
                        guidelineId,
                        it.name,
                        it.descr,
                        it.weight.toLong(),
                        it.remoteImageId,
                        it.updateImageDateTime
                    )
                }
            }

            override fun shouldFetch(data: List<Step>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Step>? = coroutineScope {
                return@coroutineScope guidelinesQueries.selectAllSteps(guidelineId).executeAsList()
                    .map {
                        Step(
                            it.id,
                            it.name,
                            it.description,
                            it.weight!!.toInt(),
                            imagePath = imagesCache.firstOrNull{im->im.stepId == it.id}?.localImagePath.orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    }
            }

            override fun createCallAsync(): Deferred<List<Step>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = steps.getAllSteps(guidelineId)
                    if (result.isSuccess) {
                        guidelinesQueries.deleteAllStepsByGuidelineId(guidelineId)
                        result.data!!.map { item ->
                            Step(
                                item.id,
                                item.name,
                                item.description,
                                item.weight,
                                imagePath = imagesCache.firstOrNull{im->im.stepId == item.id}?.localImagePath.orEmpty(),
                                remoteImageId = item.preview?.id.orEmpty(),
                                updateImageDateTime = item.preview?.activity?.updatedAt.orEmpty()
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        loadFromDb() ?: listOf()

                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun insertGuideline(data: Guideline): Response<GuidelineView> =
        coroutineScope {
            val result = guidelines.postGuideline(GuidelineCreate(data.name, data.descr))
            if (result.isSuccess) {
                val item = result.data!!
                guidelinesQueries.insertGuideline(
                    item.id,
                    item.name,
                    item.description ?: "",
                    item.favourited.toLong(),
                    item.activity.createdBy.id,
                    item.activity.createdBy.name,
                    remoteImageId = item.preview?.id.orEmpty(),
                    updateImageDateTime = item.preview?.activity?.updatedAt.orEmpty()
                )
                ratingSummaryQueries.insertRating(
                    item.id,
                    item.rating.positive.toLong(),
                    item.rating.negative.toLong(),
                    item.rating.overall.toLong()
                )
            } else {
                if (result.status == Response.Status.ERROR)
                    error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun updateGuideline(data: Guideline): Response<GuidelineView> =
        coroutineScope {
            val result =
                guidelines.putGuideline(data.id, GuidelineEdit(data.name, data.descr))
            if (result.isSuccess) {
                val item = result.data!!
                guidelinesQueries.insertGuideline(
                    item.id,
                    item.name,
                    item.description ?: "",
                    item.favourited.toLong(),
                    item.activity.createdBy.id,
                    item.activity.createdBy.name,
                    remoteImageId = item.preview?.id.orEmpty(),
                    updateImageDateTime = item.preview?.activity?.updatedAt.orEmpty()
                )
            }
//            else {
//                if (result.status != Response.Status.SUCCESS)
//                    error(result.error!!)
//            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun deleteGuideline(guidelineId: String): Response<String?> = coroutineScope {
        val result = guidelines.deleteGuideline(guidelineId)
        if (result.isSuccess) {
            feedbackQueries.deleteAllFeedbacksByGuidelineId(guidelineId)
            ratingSummaryQueries.deleteRatingByGuidelineId(guidelineId)
            guidelinesQueries.deleteAllStepsByGuidelineId(guidelineId)
            guidelinesQueries.deleteGuidelineById(guidelineId)
            imagesQueries.deleteGuidelineImages(guidelineId)
        }
//        else {
//            if (result.status == Response.Status.ERROR) error(result.error!!)
//        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun insertSteps(
        guidelineId: String,
        data: List<Step>
    ): Response<List<StepView>> = coroutineScope {
        val result = steps.postSteps(guidelineId, data.map {
            StepCreate(it.name, it.descr, it.weight)
        })
        if (result.isSuccess) {
            val items = result.data!!
            items.forEach {
                guidelinesQueries.insertStep(
                    it.id,
                    guidelineId,
                    it.name,
                    it.description,
                    it.weight.toLong(),
                    remoteImageId = it.preview?.id.orEmpty(),
                    updateImageDateTime = it.preview?.activity?.updatedAt.orEmpty()
                )
            }

        }
//        else {
//            if (result.status == Response.Status.ERROR) error(result.error!!)
//        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun updateSteps(
        guidelineId: String,
        data: List<Step>
    ): Response<List<StepView>> = coroutineScope {
        val stepMap = HashMap<String, StepEdit>()
        data.forEach {
            stepMap[it.stepId] =
                StepEdit(name = it.name, description = it.descr, weight = it.weight)
        }
        val result = steps.putSteps(guidelineId, stepMap)
        if (result.isSuccess) {
            val items = result.data!!
            items.forEach {
                guidelinesQueries.insertStep(
                    it.id,
                    guidelineId,
                    it.name,
                    it.description,
                    it.weight.toLong(),
                    remoteImageId = it.preview?.id.orEmpty(),
                    updateImageDateTime = it.preview?.activity?.updatedAt.orEmpty()
                )
            }
        }
        //else {
//            if (result.status == Response.Status.ERROR) error(result.error!!)
//        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun deleteStep(guidelineId: String, stepId: String): Response<String?> =
        coroutineScope {
            val result = steps.deleteStep(guidelineId, stepId)
            if (result.isSuccess) {
                guidelinesQueries.deleteStepById(stepId)
                imagesQueries.deleteStepImages(guidelineId, stepId)
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    override suspend fun getStepByIdFromLocalDB(guidelineId: String, stepId: String): Step =
        coroutineScope {
            val result = Step()
            guidelinesQueries.selectStepById(guidelineId, stepId).executeAsOneOrNull().apply {
                if (this != null) {
                    result.stepId = this.id
                    result.name = this.name
                    result.descr = this.description
                    result.weight = this.weight!!.toInt()
                    result.imagePath = imagesQueries.selectImageForStep(this.id).executeAsOneOrNull()?.localImagePath ?: ""
                    result.remoteImageId = this.remoteImageId
                    result.updateImageDateTime = this.updateImageDateTime
                }
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun getAllFeedbacks(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Feedback>>> {
        return object : NetworkBoundResource<List<Feedback>, List<Feedback>>() {
            override fun processResponse(response: List<Feedback>): List<Feedback> = response

            override suspend fun saveCallResults(data: List<Feedback>) = coroutineScope {
                data.forEach {
                    feedbackQueries.insertFeedback(
                        it.id,
                        guidelineId,
                        it.rating.toLong(),
                        it.comment ?: "",
                        it.author,
                        it.authorId
                    )
                }
            }

            override fun shouldFetch(data: List<Feedback>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Feedback>? = coroutineScope {
                return@coroutineScope feedbackQueries.selectAllFeedbacks(guidelineId)
                    .executeAsList()
                    .map {
                        Feedback(it.id, it.rating.toInt(), it.comment, it.author, it.authorId)
                    }
            }

            override fun createCallAsync(): Deferred<List<Feedback>> {
                return GlobalScope.async(applicationDispatcher) {
                    val result = feedback.getAllFeedbacks(guidelineId)
                    if (result.isSuccess) {
                        feedbackQueries.deleteAllFeedbacksByGuidelineId(guidelineId)
                        result.data!!.map { item ->
                            Feedback(
                                item.id,
                                item.rating,
                                item.comment,
                                item.activity.createdBy.name,
                                item.activity.createdBy.id
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        loadFromDb()?: listOf()
                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun insertRating(
        guidelineId: String,
        data: RatingCreate,
        ratingSummary: RatingSummary
    ): Response<RatingView> =
        coroutineScope {
            val result =
                feedback.postFeedback(guidelineId, data)
            if (result.isSuccess) {
                val item = result.data!!
                feedbackQueries.insertFeedback(
                    item.id,
                    guidelineId,
                    item.rating.toLong(),
                    item.comment ?: "",
                    item.activity.createdBy.name,
                    item.activity.createdBy.id
                )
                ratingSummaryQueries.insertRating(
                    guidelineId,
                    ratingSummary.positive.toLong(),
                    ratingSummary.negative.toLong(),
                    ratingSummary.overall.toLong()
                )
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun updateRating(guidelineId: String, data: Feedback): Response<RatingView> =
        coroutineScope {
            val result = feedback.putFeedback(
                guidelineId,
                data.id,
                RatingEdit(data.rating, data.comment)
            )
            if (result.isSuccess) {
                val item = result.data!!
                feedbackQueries.insertFeedback(
                    item.id,
                    guidelineId,
                    item.rating.toLong(),
                    item.comment ?: "",
                    item.activity.createdBy.name,
                    item.activity.createdBy.id
                )
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun deleteRating(guidelineId: String, feedbackId: String): Response<String?> =
        coroutineScope {
            val result = feedback.deleteFeedback(guidelineId, feedbackId)
            if (result.isSuccess) {
                feedbackQueries.deleteFeedbackById(feedbackId)
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    override suspend fun getSuggestions(searchText: String): List<String> =
        coroutineScope {
            return@coroutineScope suggestionsQueries.selectSuggestionsByText(searchText)
                .executeAsList()
        }

    override suspend fun getFilteredGuidelines(searchText: String): List<Guideline> =
        coroutineScope {
            val ratingSummaryCache = ratingSummaryQueries.selectAllRatings().executeAsList()
            val favorites = favoritesQueries.selectAllGuidelinesIdFromFavorites().executeAsList()
            val imagesCache = imagesQueries.selectImagesForGuidelines().executeAsList()
            return@coroutineScope suggestionsQueries.searchGuidelinesByText(searchText)
                .executeAsList().map {
                    val rating = ratingSummaryCache.firstOrNull { rating -> rating.id == it.id }
                    if (rating != null) {
                        Guideline(
                            it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            author = it.author,
                            authorId = it.authorId,
                            isFavorite = favorites.any { fav -> fav == it.id },
                            rating = RatingSummary(
                                rating.positive!!.toInt(),
                                rating.negative!!.toInt(),
                                rating.overall!!.toInt()
                            ),
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    } else {
                        Guideline(it.id,
                            it.name,
                            it.description,
                            it.favourited!!.toInt(),
                            it.author,
                            it.authorId,
                            imagePath = imagesCache.firstOrNull{im->im.guidelineId == it.id}?.localImagePath.orEmpty(),
                            remoteImageId = it.remoteImageId,
                            updateImageDateTime = it.updateImageDateTime
                        )
                    }
                }

        }

    @UnstableDefault
    override suspend fun addGuidelineToFavorite(
        guidelineId: String,
        favorited: Int
    ): Response<FavoriteView> =
        coroutineScope {
            val result = favorites.postFavorite(guidelineId)
            if (result.isSuccess) {
                val item = result.data!!
                favoritesQueries.addGuidelineToFavorites(item.id, item.entity.type, item.entity.id)
                guidelinesQueries.updateGuidelineFavourited(favorited.plus(1).toLong(), guidelineId)
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun removeGuidelineFromFavorite(
        guidelineId: String,
        favorited: Int
    ): Response<String?> =
        coroutineScope {
            var result: Response<String?>
            favoritesQueries.selectFavoriteIdByGuidelineId(guidelineId).executeAsOne().apply {
                result = favorites.deleteFavorite(guidelineId, this)
                if (result.isSuccess) {
                    favoritesQueries.removeGuidelineFromFavoritesById(this)
                    guidelinesQueries.updateGuidelineFavourited(favorited.minus(1).toLong(), guidelineId)
                } else {
                    if (result.status == Response.Status.ERROR) error(result.error!!)
                }
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun clearFavorites() =
        coroutineScope {
            return@coroutineScope favoritesQueries.clearFavorites()
        }

    @UnstableDefault
    override suspend fun checkPreviewImageForGuideline(guideline: Guideline): String? =
        coroutineScope {
            var url:String? = null
            if (!guideline.isEmptyPreview) {
                imagesQueries.selectImageForGuideline(guideline.id).executeAsOneOrNull().apply {
                    if (this == null
                        || !this.updateImageDateTime.equals(guideline.updateImageDateTime)
                        || guideline.isImageNotDownloaded) {
                        url = guidelines.generateURLForGuidelineImage(
                            guideline.id,
                            guideline.remoteImageId
                        )
                    }
                }
            }
            return@coroutineScope url
        }

    @UnstableDefault
    override suspend fun saveGuidelineImageInLocalDB(guideline: Guideline) =
        coroutineScope {
                imagesQueries.selectImageForGuideline(guideline.id).executeAsOneOrNull().apply {
                    if (this != null)
                        imagesQueries.updateImageById(guideline.imagePath,
                                                        guideline.remoteImageId,
                                                        guideline.updateImageDateTime,
                                                        this.id)
                    else
                        imagesQueries.insertGuidelineImage(guideline.id,
                                                        guideline.imagePath,
                                                        guideline.remoteImageId,
                                                        guideline.updateImageDateTime)
                }
                return@coroutineScope
            }

    @UnstableDefault
    override suspend fun checkPreviewImageForStep(guidelineId: String, step: Step): String? =
        coroutineScope {
            var url:String? = null
            if (!step.isEmptyPreview) {
                imagesQueries.selectImageForStep(step.stepId).executeAsOneOrNull().apply {
                    if (this == null
                        || !this.updateImageDateTime.equals(step.updateImageDateTime)
                        || step.isImageNotDownloaded) {
                        url = guidelines.generateURLForStepImage(
                            guidelineId,
                            step.stepId,
                            step.remoteImageId
                        )
                    }
                }
            }
            return@coroutineScope url
        }

    @UnstableDefault
    override suspend fun saveStepImageInLocalDB(guidelineId: String, step: Step) =
        coroutineScope {
            imagesQueries.selectImageForStep(step.stepId).executeAsOneOrNull().apply {
                if (this != null)
                    imagesQueries.updateImageById(step.imagePath,
                        step.remoteImageId,
                        step.updateImageDateTime,
                        this.id)
                else
                    imagesQueries.insertStepImage(guidelineId,
                        step.stepId,
                        step.imagePath,
                        step.remoteImageId,
                        step.updateImageDateTime)
            }
            return@coroutineScope
        }

    @UnstableDefault
    override suspend fun uploadGuidelineImage(guideline: Guideline, data: ByteArray): Response<FileView> =
        coroutineScope {
            lateinit var result: Response<FileView>;
            if (guideline.isEmptyPreview) {
                result = guidelines.postGuidelineImage(guideline.id, data)
            }
            else if (guideline.isImageNotUploaded){
                result = guidelines.putGuidelineImage(guideline.id, guideline.remoteImageId, data)
            }
            if (result.isSuccess && result.isNotEmpty) {
                result.data!!.apply {
                    guideline.remoteImageId = this.id
                    guideline.updateImageDateTime = this.activity.updatedAt
                    saveGuidelineImageInLocalDB(guideline)
                }
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun uploadStepImage(guidelineId: String, step:Step, data: ByteArray): Response<FileView> =
        coroutineScope {
            lateinit var result: Response<FileView>;
            if (step.isEmptyPreview) {
                result = guidelines.postStepImage(guidelineId, step.stepId, data)
            }
            else if (step.isImageNotUploaded){
                result = guidelines.putStepImage(guidelineId, step.stepId, step.remoteImageId, data)
            }
            if (result.isSuccess && result.isNotEmpty) {
                result.data!!.apply {
                    step.remoteImageId = this.id
                    step.updateImageDateTime = this.activity.updatedAt
                    saveStepImageInLocalDB(guidelineId, step)
                }
            }
            return@coroutineScope result
        }

}