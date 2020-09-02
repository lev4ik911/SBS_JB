package by.iba.sbs.ui.guideline

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.InputType
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import by.iba.sbs.BR
import by.iba.sbs.BuildConfig
import by.iba.sbs.R
import by.iba.sbs.adapters.BaseBindingAdapter
import by.iba.sbs.databinding.InstructionActivityBinding
import by.iba.sbs.databinding.StepPreviewItemBinding
import by.iba.sbs.databinding.StepPreviewLayoutBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.model.request.RatingCreate
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import by.iba.sbs.tools.DownloadManager
import by.iba.sbs.tools.Tools
import by.iba.sbs.tools.copyTo
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.russhwolf.settings.AndroidSettings
import com.yalantis.ucrop.UCrop
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import java.io.File
import java.util.*


class GuidelineActivity :
    MvvmEventsActivity<InstructionActivityBinding, GuidelineViewModel, GuidelineViewModel.EventsListener>(),
    GuidelineViewModel.EventsListener {
    override val layoutId: Int = R.layout.instruction_activity
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        GuidelineViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)),
            eventsDispatcherOnMain()
        )
    }

    override val viewModelVariableId: Int = BR.viewmodel
    lateinit var mPopupWindow: PopupWindow
    var bindingPopup: StepPreviewLayoutBinding? = null

    private val PICK_IMAGE_GALLERY_REQUEST_CODE = 609
    private val CAMERA_ACTION_PICK_REQUEST_CODE = 610
    private val WRITE_STORAGE_PERMISSION = 611
    private val ACCESS_TO_CAMERA_PERMISSION = 612
    private var usingCamera = false
    private var selectedAction = 0
    private var absolutePhotoPath = ""
    private lateinit var _editStep: Step
    internal lateinit var instructionId: String
    private lateinit var step: Step
    private var isStepEditing = false


    private enum class ImageActions(val key: Int, val stringId: Int) {
        EditCurrent(0, R.string.select_variant_edit_current),
        TakePhoto(1, R.string.select_variant_take_photo),
        FromGallery(2, R.string.select_variant_from_gallery)
    }

    @ImplicitReflectionSerializer
    @UnstableDefault
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instructionId = intent?.getStringExtra("instructionId") ?: ""
        val forceRefresh = Date().day != Date(viewModel.localStorage.lastUpdate).day
        viewModel.loadGuideline(instructionId, forceRefresh)
        findNavController(R.id.fragment_navigation_instruction).navigate(
            if (instructionId == "") R.id.navigation_instruction_edit else R.id.navigation_instruction_view,
            bundleOf("instructionId" to instructionId)
        )
    }

    private fun checkPermissions() {
        var hasAccessToCamera = false
        val hasAccessToWrite: Boolean
        //check permission on camera
        if (usingCamera) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasAccessToCamera = false
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    ACCESS_TO_CAMERA_PERMISSION
                )
            } else hasAccessToCamera = true
        }

        //check permissions on write
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            hasAccessToWrite = false
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_STORAGE_PERMISSION
            )
        } else hasAccessToWrite = true

        if ((usingCamera && hasAccessToCamera && hasAccessToWrite) ||
            (!usingCamera && hasAccessToWrite)
        ) {
            openDialogSelectImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        var hasAccessToCamera = false
        var hasAccessToWrite = false
        when (requestCode) {
            ACCESS_TO_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        hasAccessToCamera = true
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        hasAccessToWrite = true
                } else {
                    hasAccessToCamera = false
                    hasAccessToWrite = false
                }
            }
            WRITE_STORAGE_PERMISSION -> {
                hasAccessToWrite =
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
        if ((usingCamera && hasAccessToCamera && hasAccessToWrite) ||
            (!usingCamera && hasAccessToWrite)
        ) {
            openDialogSelectImage()
        } else {
            if (usingCamera && !hasAccessToCamera) {
                val toast = Toast.makeText(
                    this,
                    resources.getString(R.string.camera_access_required), Toast.LENGTH_SHORT
                )
                toast.show()
            }
            if (!hasAccessToWrite) {
                val toast = Toast.makeText(
                    this,
                    resources.getString(R.string.storage_access_required), Toast.LENGTH_SHORT
                )
                toast.show()
            }

        }
    }

    private fun openDialogSelectImage() {
        when (selectedAction) {
            ImageActions.EditCurrent.key -> {
                try {
                    val path =
                        if (isStepEditing) _editStep.imagePath else viewModel.guideline.value.imagePath
                    if (path.isNotEmpty()) {
                        val sourcePath = Uri.fromFile(File(path))
                        val destinationUri = createImageFileInInternalStorage()
                        UCrop.of(sourcePath, destinationUri)
                            .withAspectRatio(16f, 9f)
                            .start(this@GuidelineActivity)
                    }

                } catch (ex: Exception) {
                    val toast = Toast.makeText(
                        this,
                        resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                    )
                    toast.show()
                    //TODO("Add to logs")
                }
            }
            ImageActions.TakePhoto.key -> {
                try {
                    val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val photoUri = createTempImageFileInDCIM()
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE)
                } catch (ex: Exception) {
                    val toast = Toast.makeText(
                        this,
                        resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                    )
                    toast.show()
                    //TODO("Add to logs")
                }
            }
            ImageActions.FromGallery.key -> {
                val pictureIntent = Intent(Intent.ACTION_GET_CONTENT)
                pictureIntent.type = "image/*"
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE)
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                startActivityForResult(
                    Intent.createChooser(pictureIntent, "Select Picture"),
                    PICK_IMAGE_GALLERY_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    val resultUri: Uri? = UCrop.getOutput(data!!)
                    if (resultUri != null && resultUri!!.path.isNotEmpty()) {
                        if (isStepEditing) {
                            _editStep.imagePath = resultUri!!.path
                            _editStep.updateImageDateTime = ""
                            viewModel.steps.value = viewModel.steps.value
                        } else {
                            val guideline = viewModel.guideline.value
                            guideline.imagePath = resultUri!!.path
                            guideline.updateImageDateTime = ""
                            viewModel.guideline.value = viewModel.guideline.value
                        }
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val toast = Toast.makeText(
                        this,
                        resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                    )
                    toast.show()
                    println(UCrop.getError(data!!))
                    //TODO("Add to logs")
                }
            }
            PICK_IMAGE_GALLERY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    //TODO("Add offer cut image by default" )
                    try {
                        val sourceUri = data.data as Uri
                        val destinationUri = createImageFileInInternalStorage()
                        UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16f, 9f)
                            .start(this)
                    } catch (ex: Exception) {
                        val toast = Toast.makeText(
                            this,
                            resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                        )
                        toast.show()
                        //TODO("Add to logs")
                    }
                }
            }
            CAMERA_ACTION_PICK_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    galleryAddPic()
                    //TODO("Add offer cut image by default" )
                    try {
                        val photoUri = Uri.parse(absolutePhotoPath)
                        val destinationUri = createImageFileInInternalStorage()
                        UCrop.of(photoUri, destinationUri)
                            .withAspectRatio(16f, 9f)
                            .start(this)
                    } catch (ex: Exception) {
                        val toast = Toast.makeText(
                            this,
                            resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                        )
                        toast.show()
                        //TODO("Add to logs")
                    }
                }
            }
        }
    }

    private fun createImageFileInInternalStorage(guidelineId: String="", stepId: String = "", remoteImageId: String=""): Uri {
         DownloadManager(applicationContext).apply {
             return this.createImageFileInInternalStorage(guidelineId, stepId, remoteImageId)
        }
    }

    // if we select "Take photo", photo also will be saved in DCIM
    private fun createTempImageFileInDCIM(): Uri {
        val imageFileName = "JPEG_${System.currentTimeMillis()}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "Camera"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val file = File.createTempFile(
            imageFileName, ".jpg", storageDir
        )
        absolutePhotoPath = "file:" + file.absoluteFile
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        else
            Uri.fromFile(file)
    }

    //update Gallery
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = Uri.parse(absolutePhotoPath)
            sendBroadcast(mediaScanIntent)
        }
    }

    override fun onCallInstructionEditor(instructionId: String) {
        val bundle = Bundle()
        bundle.putString("instructionId", instructionId)
        findNavController(R.id.fragment_navigation_instruction).navigate(
            R.id.navigation_instruction_edit,
            bundle
        )
    }

    override fun onOpenProfile(profileId: String) {
        findNavController(R.id.fragment_navigation_instruction).navigate(
            R.id.navigation_profile_fragment,
            bundleOf("userId" to profileId)
        )
    }

    override fun onEditStep(stepWeight: Int) {
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_step_edit, bundleOf("stepWeight" to stepWeight))
    }

    override fun onEditStepImage(editStep: Step) {
        isStepEditing = true
        _editStep = editStep
        val stepHasImage = _editStep.imagePath.isNotEmpty()
        openAlertDialog(stepHasImage)
    }

    override fun onEditGuidelineImage() {
        isStepEditing = false
        val guidelineHasImage = viewModel.guideline.value.imagePath.isNotEmpty()
        openAlertDialog(guidelineHasImage)
    }

    private fun openAlertDialog(itemHasImage: Boolean) {
        val builder = AlertDialog.Builder(this)
        val listOfResolvedActions = ImageActions.values().filter {
            (itemHasImage && it == ImageActions.EditCurrent) || (it != ImageActions.EditCurrent)
        }

        builder
            //.setTitle("Upload from")
            .setItems(listOfResolvedActions.map { resources.getString(it.stringId) }
                .toTypedArray()) { _, key ->
                selectedAction = key
                if (!itemHasImage)
                    selectedAction += 1

                when (selectedAction) {
                    ImageActions.EditCurrent.key, ImageActions.FromGallery.key -> {
                        usingCamera = false
                    }
                    ImageActions.TakePhoto.key -> {
                        usingCamera = true
                    }
                }
                checkPermissions()
            }
        //builder.setNegativeButton("Cancel", null);
        val dialog = builder.create()
        dialog.show()

    }

    override fun onPreviewStepAction(step: Step) {
        val contentView = layoutInflater.inflate(R.layout.step_preview_layout, null)
        bindingPopup = DataBindingUtil.bind(contentView)
        if (bindingPopup != null) {
            bindingPopup!!.viewmodel = viewModel
            mPopupWindow = PopupWindow(
                contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).also {
                it.isOutsideTouchable = false
                it.isFocusable = true
                it.elevation = 25.0f
                it.animationStyle = R.style.AnimationZoom
            }
            val stepsAdapter =
                BaseBindingAdapter<Step, StepPreviewItemBinding, GuidelineViewModel>(
                    R.layout.step_preview_item,
                    BR.step,
                    BR.viewmodel,
                    viewModel,
                    isItemsEquals = { oldItem, newItem ->
                        oldItem.weight == newItem.weight
                    }
                )
            bindingPopup!!.rvSteps.apply {
                this.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                adapter = stepsAdapter
                val snapHelperStart: SnapHelper = PagerSnapHelper()
                snapHelperStart.attachToRecyclerView(this)
            }
            viewModel.steps.addObserver {
                stepsAdapter.addItems(it)
                bindingPopup!!.rvSteps.scrollToPosition(step.weight - 1)
            }
            mPopupWindow.showAtLocation(binding.container, Gravity.CENTER, 0, 0)

            viewModel.updatedStepId.addObserver {
                stepsAdapter.itemsList.indexOfFirst { step -> step.stepId == it }.apply {
                    if (this != -1)
                        stepsAdapter.notifyItemChanged(this)
                }
            }

        }

    }

    override fun onClosePreviewStepAction() {
        mPopupWindow.dismiss()
    }

    override fun onPreviewStepNextAction(currentStep: Step) {
        bindingPopup?.rvSteps?.scrollToPosition(currentStep.weight)
    }

    override fun onPreviewStepPreviousAction(currentStep: Step) {
        bindingPopup?.rvSteps?.scrollToPosition(currentStep.weight - 2)
    }

    override fun onAfterSaveAction() {
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_instruction_view)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onRemoveInstruction() {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.title_delete_instruction_dialog))
            setMessage(
                resources.getString(
                    R.string.msg_delete_instruction_dialog,
                    viewModel.guideline.value.name
                )
            )
            setPositiveButton(
                resources.getString(R.string.btn_delete)
            ) { _: DialogInterface, _: Int ->
                viewModel.deleteInstruction(viewModel.guideline.value)
            }
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
            .create()
            .show()
    }

    override fun onAfterDeleteAction() {
        this.finish()
    }

    override fun onAfterSaveStepAction() {
        findNavController(R.id.fragment_navigation_instruction).navigate(
            R.id.navigation_instruction_edit
        )
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onRatingDownAction() {
        var result = ""
        MaterialDialog(this).show {
            title(R.string.title_dialog_feedback)
            input(
                hint = "Leave feedback",
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { _, text ->
                result = text.toString()
            }
            lifecycleOwner(this@GuidelineActivity)
            positiveButton(R.string.btn_send, click = {
                viewModel.createFeedback(RatingCreate(-1, result))
            })
            negativeButton(R.string.btn_cancel)
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onRatingUpAction() {
        var result = ""
        MaterialDialog(this).show {
            title(R.string.title_dialog_feedback)
            input(
                hint = "Leave feedback",
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { _, text ->
                result = text.toString()
            }
            lifecycleOwner(this@GuidelineActivity)
            positiveButton(R.string.btn_send, click = {
                viewModel.createFeedback(RatingCreate(1, result))
            })
            negativeButton(R.string.btn_cancel)
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onRemoveStep(step: Step) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.title_delete_step_dialog))
            setMessage(resources.getString(R.string.msg_delete_step_dialog, step.name))
            setPositiveButton(
                resources.getString(R.string.btn_delete),
                { dialogInterface: DialogInterface, i: Int ->
                    if (step.stepId.isNotEmpty()) {
                        viewModel.deleteStep(viewModel.guideline.value.id, step)
                    } else {
                        val stepArr = viewModel.steps.value.toMutableList()
                        stepArr.remove(step)
                        stepArr.forEachIndexed { index, step -> step.weight = index + 1 }
                        viewModel.steps.value = stepArr
                        onAfterSaveStepAction()
                    }
                })
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun showToast(msg: ToastMessage) {
        Tools.showToast(this, viewModelClass.name, msg)
    }

    override fun onAuthorizationRequired() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.title_dialog_authorization))
            setMessage(resources.getString(R.string.msg_authorization_dialog))
            setPositiveButton(
                resources.getString(R.string.btn_login)
            ) { _: DialogInterface, _: Int ->
                findNavController(R.id.fragment_navigation_instruction).navigate(R.id.navigation_login_fragment)
            }
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
        val dialog = builder.create()
        dialog.show()
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun loadImage(url: String,
                           guidelineId: String,
                           stepId: String,
                           remoteImageId: String,
                           source: Any) {
        DownloadManager(applicationContext).apply {
            this.downloadImage(url,
                guidelineId =  guidelineId,
                stepId = stepId,
                remoteImageId = remoteImageId,
                item = source,
                imageHandler =  imageHandler)
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun getGuidelineImageData() {
        File(viewModel.guideline.value.imagePath).apply {
            viewModel.uploadGuidelineImage(this.readBytes())
        }
    }
    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun getStepImageData(step: Step) {
        File(step.imagePath).apply {
            viewModel.uploadStepImage(step, this.readBytes())
        }
    }

    override fun deleteImagesOnDevice(guidelineId: String, stepId: String) {
        ContextWrapper(applicationContext).apply {
            val builder = StringBuilder()
            builder.append(this.getDir("imageDir", Context.MODE_PRIVATE).path )
            if (guidelineId.isNotEmpty()) {
                builder.append("/")
                    .append(guidelineId)
            }
            if (stepId.isNotEmpty()) {
                builder.append("/")
                    .append(stepId)
            }
            deleteFileFolderRecursive(File(builder.toString()))
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun transferTempImage(
        localPath: String,
        guidelineId: String,
        stepId: String,
        remoteImageId: String,
        source: Any
    ) {
        val file = File(localPath)
        val newLocalPath = createImageFileInInternalStorage(guidelineId, stepId, remoteImageId).path
        file.copyTo(File(newLocalPath))
        when (source) {
            is Guideline -> {
                source.imagePath = newLocalPath
                viewModel.saveGuidelinePreviewImageInLocalDB(source)
            }
            is Step -> {
                source.imagePath = newLocalPath
                viewModel.saveStepPreviewImageInLocalDB(source)
            }
            else -> {}
        }
        deleteFileFolderRecursive(file)
    }

    private fun deleteFileFolderRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles())
                deleteFileFolderRecursive(child)
        fileOrDirectory.delete()
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val imageHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.obj) {
                is Guideline -> {
                    val guideline = msg.obj as Guideline
                    viewModel.guideline.value = guideline
                    viewModel.saveGuidelinePreviewImageInLocalDB(guideline)
                }
                is Step -> {
                    val step = msg.obj as Step
                    viewModel.updatedStepId.value = step.stepId
                    viewModel.saveStepPreviewImageInLocalDB(step)
                }
                else -> {}
            }

            super.handleMessage(msg)
        }
    }
}
