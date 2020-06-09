package by.iba.sbs.library.repository

import by.iba.sbs.library.data.local.createDb
import by.iba.sbs.library.data.remote.*
import by.iba.sbs.library.model.Feedback
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.model.request.*
import by.iba.sbs.library.model.response.GuidelineView
import by.iba.sbs.library.model.response.RatingSummary
import by.iba.sbs.library.model.response.RatingView
import by.iba.sbs.library.model.response.StepView
import by.iba.sbs.library.service.LocalSettings
import dev.icerock.moko.mvvm.livedata.LiveData
import kotlinx.coroutines.*
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
    suspend fun insertStep(guidelineId: String, data: Step): Response<StepView>
    suspend fun updateStep(guidelineId: String, data: Step): Response<StepView>
    suspend fun deleteStep(guidelineId: String, stepId: String): Response<String?>
    suspend fun getStepByIdFromLocalDB(guidelineId: String, stepId: String): Step
    suspend fun insertRating(guidelineId: String, data: RatingCreate): Response<RatingView>
    suspend fun updateRating(guidelineId: String, data: Feedback): Response<RatingView>
    suspend fun deleteRating(guidelineId: String, stepId: String): Response<String?>
}

@ImplicitReflectionSerializer
class GuidelineRepository @UnstableDefault constructor(settings: LocalSettings) :
    IGuidelineRepository {
    @UnstableDefault
    private val guidelines = Guidelines(settings)

    @UnstableDefault
    private val steps = Steps(settings)

    @UnstableDefault
    private val feedback = Feedback(settings)
    private val sbsDb = createDb()
    private val guidelinesQueries = sbsDb.guidelinesEntityQueries
    private val ratingSummaryQueries = sbsDb.ratingSummaryQueries
    private val feedbackQueries = sbsDb.feedbackEntityQueries

    @UnstableDefault
    override suspend fun getAllGuidelines(forceRefresh: Boolean): LiveData<Response<List<Guideline>>> {
        //   if (forceRefresh) clearCache()
        return object : NetworkBoundResource<List<Guideline>, List<Guideline>>() {
            override fun processResponse(response: List<Guideline>): List<Guideline> = response

            override suspend fun saveCallResults(data: List<Guideline>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertGuideline(it.id, it.name, it.description)
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
                            it.id, it.name, it.description,
                            rating = RatingSummary(
                                rating.positive!!.toInt(),
                                rating.negative!!.toInt(),
                                rating.overall!!.toInt()
                            )
                        )
                    } else {
                        Guideline(it.id, it.name, it.description)
                    }
                }
            }

            override fun createCallAsync(): Deferred<List<Guideline>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val ratingSummary = ratingSummaryQueries.selectAllRatings().executeAsList()

                    val result = guidelines.getAllGuidelines()
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            val rating =
                                ratingSummary.firstOrNull { rating -> rating.id == item.id }
                            if (rating != null) {
                                Guideline(
                                    item.id, item.name, item.description ?: "",
                                    rating = RatingSummary(
                                        rating.positive!!.toInt(),
                                        rating.negative!!.toInt(),
                                        rating.overall!!.toInt()
                                    )
                                )
                            } else {
                                Guideline(item.id, item.name, item.description ?: "")
                            }
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()

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
                guidelinesQueries.insertGuideline(data.id, data.name, data.description)

            }

            override fun shouldFetch(data: Guideline?): Boolean =
                data == null || forceRefresh

            override suspend fun loadFromDb(): Guideline = coroutineScope {
                val item = guidelinesQueries.selectGuidelineById(guidelineId).executeAsOne()
                val ratingSummary =
                    ratingSummaryQueries.selectRatingByGuidelineId(guidelineId).executeAsOne()
                return@coroutineScope Guideline(
                    item.id, item.name, item.description, rating = RatingSummary(
                        ratingSummary.positive!!.toInt(),
                        ratingSummary.negative!!.toInt(),
                        ratingSummary.overall!!.toInt()
                    )
                )
            }

            override fun createCallAsync(): Deferred<Guideline> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = guidelines.getGuideline(guidelineId)
                    if (result.isSuccess) {
                        val item = result.data!!
                        Guideline(
                            item.id,
                            item.name,
                            item.description!!,
                            rating = RatingSummary(
                                item.rating.positive,
                                item.rating.negative,
                                item.rating.overall
                            )
                        )
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        Guideline()

                    }
                }
            }

        }.build()
            .asLiveData()
    }

    private fun clearCache() {
        guidelinesQueries.deleteAllSteps()
        guidelinesQueries.deleteAllGuidelines()
    }

    @UnstableDefault
    override suspend fun getAllSteps(
        guidelineId: String,
        forceRefresh: Boolean
    ): LiveData<Response<List<Step>>> {
        return object : NetworkBoundResource<List<Step>, List<Step>>() {
            override fun processResponse(response: List<Step>): List<Step> = response

            override suspend fun saveCallResults(data: List<Step>) = coroutineScope {
                data.forEach {
                    guidelinesQueries.insertStep(
                        it.stepId,
                        guidelineId,
                        it.name,
                        it.description,
                        it.weight.toLong()
                    )
                }
            }

            override fun shouldFetch(data: List<Step>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Step> = coroutineScope {
                return@coroutineScope guidelinesQueries.selectAllSteps(guidelineId).executeAsList()
                    .map {
                        Step(it.id, it.name, it.description, it.weight!!.toInt(), it.imagePath, it.updateImageTimeSpan!!.toInt())
                    }
            }

            override fun createCallAsync(): Deferred<List<Step>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = steps.getAllSteps(guidelineId)
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Step(
                                item.id,
                                item.name,
                                item.description,
                                item.weight,
                                item.imagePath,
                                item.updateImageTimeSpan
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()

                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun insertGuideline(data: Guideline): Response<GuidelineView> =
        coroutineScope {
            val result = guidelines.postGuideline(GuidelineCreate(data.name, data.description))
            if (result.isSuccess) {
                val item = result.data!!
                guidelinesQueries.insertGuideline(item.id, item.name, item.description ?: "")
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun updateGuideline(data: Guideline): Response<GuidelineView> =
        coroutineScope {
            val result =
                guidelines.putGuideline(data.id, GuidelineEdit(data.name, data.description))
            if (result.isSuccess) {
                val item = result.data!!
                guidelinesQueries.insertGuideline(item.id, item.name, item.description ?: "")
            } else {
                if (result.status == Response.Status.ERROR) error(result.error!!)
            }
            return@coroutineScope result
        }

    @UnstableDefault
    override suspend fun deleteGuideline(guidelineId: String): Response<String?> = coroutineScope {
        val result = guidelines.deleteGuideline(guidelineId)
        if (result.isSuccess) {
            guidelinesQueries.deleteAllStepsByGuidelineId(guidelineId)
            guidelinesQueries.deleteGuidelineById(guidelineId)
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun insertStep(guidelineId: String, data: Step): Response<StepView> = coroutineScope {
        val result = steps.postStep(guidelineId, StepCreate(data.name, data.description, data.weight))
        if (result.isSuccess) {
            val item = result.data!!
            guidelinesQueries.insertStepWithImage(
                item.id,
                guidelineId,
                item.name,
                item.description,
                item.weight.toLong(),
                data.imagePath, // save local path in db
                item.updateImageTimeSpan.toLong()
            )
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun updateStep(guidelineId: String, data: Step): Response<StepView> = coroutineScope {
        val result = steps.putStep(
            guidelineId,
            data.stepId,
            StepEdit(name = data.name, description = data.description, weight = data.weight)
        )
        if (result.isSuccess) {
            val item = result.data!!
            guidelinesQueries.insertStepWithImage(
                item.id,
                guidelineId,
                item.name,
                item.description,
                item.weight.toLong(),
                data.imagePath, // save local path in db
                item.updateImageTimeSpan.toLong()
            )
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    @UnstableDefault
    override suspend fun deleteStep(guidelineId: String, stepId: String): Response<String?> = coroutineScope {
        val result = steps.deleteStep(guidelineId, stepId)
        if (result.isSuccess) {
            guidelinesQueries.deleteStepById(stepId)
        } else {
            if (result.status == Response.Status.ERROR) error(result.error!!)
        }
        return@coroutineScope result
    }

    override suspend fun getStepByIdFromLocalDB(guidelineId: String, stepId: String): Step =
        coroutineScope {
            val result = Step()
            guidelinesQueries.selectStepById(guidelineId, stepId).executeAsOne().apply {
                result.stepId = this.id
                result.name = this.name
                result.description = this.description
                result.weight = this.weight!!.toInt()
                result.imagePath = this.imagePath
                result.updateImageTimeSpan = this.updateImageTimeSpan!!.toInt()
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
                        it.comment ?: ""
                    )
                }
            }

            override fun shouldFetch(data: List<Feedback>?): Boolean =
                data == null || data.isEmpty() || forceRefresh

            override suspend fun loadFromDb(): List<Feedback> = coroutineScope {
                return@coroutineScope feedbackQueries.selectAllFeedbacks(guidelineId)
                    .executeAsList()
                    .map {
                        Feedback(it.id, it.rating.toInt(), it.comment)
                    }
            }

            override fun createCallAsync(): Deferred<List<Feedback>> {
                return GlobalScope.async(Dispatchers.Default) {
                    val result = feedback.getAllFeedbacks(guidelineId)
                    if (result.isSuccess) {
                        result.data!!.map { item ->
                            Feedback(
                                item.id,
                                item.rating,
                                item.comment
                            )
                        }
                    } else {
                        if (result.status == Response.Status.ERROR) error(result.error!!)
                        listOf()

                    }
                }
            }

        }.build()
            .asLiveData()
    }

    @UnstableDefault
    override suspend fun insertRating(
        guidelineId: String,
        data: RatingCreate
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
                    item.comment ?: ""
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
                    item.comment ?: ""
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

}