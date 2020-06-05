package by.iba.sbs.ui.guideline

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.sbs.BuildConfig
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionActivityBinding
import by.iba.sbs.library.model.Step
import by.iba.sbs.ui.profile.ProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yalantis.ucrop.UCrop
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*


class GuidelineActivity :
    BaseEventsActivity<InstructionActivityBinding, GuidelineViewModel, GuidelineViewModel.EventsListener>(),
    GuidelineViewModel.EventsListener {
    override val layoutId: Int = R.layout.instruction_activity
    override val viewModel: GuidelineViewModel by viewModel()
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel


    private val PICK_IMAGE_GALLERY_REQUEST_CODE = 609
    private val CAMERA_ACTION_PICK_REQUEST_CODE = 610
    private val WRITE_STORAGE_PERMISSION = 611
    private val ACCESS_TO_CAMERA_PERMISSION = 612
    private var usingCamera = false
    private var selectedAction = 0
    private var absolutePhotoPath = ""
    private lateinit var step: Step


    private enum class ImageActions(val key: Int, val stringId: Int) {
        EditCurrent(0, R.string.select_variant_edit_current),
        TakePhoto(1, R.string.select_variant_take_photo),
        FromGallery(2, R.string.select_variant_from_gallery)
    }

    @UnstableDefault
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instructionId = intent?.getStringExtra("instructionId") ?: ""
        val bundle = Bundle()
        bundle.putString("instructionId", instructionId)

        viewModel.loadInstruction(instructionId, true)

        findNavController(R.id.fragment_navigation_instruction).navigate(
            if (instructionId == "") R.id.navigation_instruction_edit else R.id.navigation_instruction_view,
            bundle
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
                        if (!step.imagePath.isEmpty()) {
                            val sourcePath = Uri.fromFile(File(step.imagePath))
                            val destinationUri = createTempImageFileInInternalStorage()
                            UCrop.of(sourcePath, destinationUri)
                                .withAspectRatio(1f, 1f)
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
                    if (!(resultUri?.path).isNullOrEmpty())
                        step.imagePath = resultUri?.path!!
                    viewModel.steps.value = viewModel.steps.value

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
                        val sourceUri = data.getData() as Uri
                        val destinationUri = createTempImageFileInInternalStorage()
                        UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1f, 1f)
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
                        val destinationUri = createTempImageFileInInternalStorage()
                        UCrop.of(photoUri, destinationUri)
                            .withAspectRatio(1f, 1f)
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

    private fun createTempImageFileInInternalStorage(): Uri {
        val imageFileName = UUID.randomUUID().toString()
        val cw = ContextWrapper(applicationContext)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)

        if (!directory.exists())
            directory.mkdirs()

        val file = File.createTempFile(
            imageFileName, ".jpg", directory
        )
        return Uri.fromFile(file)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        else
            return Uri.fromFile(file)
    }

    //update Gallery
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = Uri.parse(absolutePhotoPath)
            sendBroadcast(mediaScanIntent)
        }
    }

    fun onToolbarClick(view: View) {
        onBackPressed()
    }

    override fun onCallInstructionEditor(instructionId: String) {
        val bundle = Bundle()
        bundle.putString("instructionId", instructionId)
        findNavController(R.id.fragment_navigation_instruction).navigate(R.id.navigation_instruction_edit, bundle)
    }

    override fun onOpenProfile(profileId: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("profileId", profileId)
        // findNavController().navigate(R.id.navigation_instruction_edit, bundle)
        startActivity(intent)
    }

    override fun onEditStep(stepId: String) {
        val bundle = bundleOf("stepId" to stepId)
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_step_edit, bundle)
    }

    override fun onEditImage(editStep: Step) {
        step = editStep
        val stepHasImage = step.imagePath.isNotEmpty()
        val builder = AlertDialog.Builder(this)
        val listOfResolvedActions = ImageActions.values().filter {
            (stepHasImage && it == ImageActions.EditCurrent) || (it != ImageActions.EditCurrent)
        }

        builder
            //.setTitle("Upload from")
            .setItems(listOfResolvedActions.map { resources.getString(it.stringId) }
                .toTypedArray()) { _, key ->
                selectedAction = key
                if (!stepHasImage)
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

    override fun onAfterSaveAction() {
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_instruction_view)
    }
    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onRemoveInstruction() {
        val builder = AlertDialog.Builder(this).apply{
            setTitle(resources.getString(R.string.title_delete_instruction_dialog))
            setMessage(resources.getString(R.string.msg_delete_instruction_dialog, viewModel.guideline.value!!.name))
            setPositiveButton(resources.getString(R.string.btn_delete), { dialogInterface: DialogInterface, i: Int ->
                viewModel.deleteInstruction(viewModel.guideline.value!!)
            })
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
        val dialog = builder.create()
        dialog.show()
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
    override fun onRemoveStep(step: Step) {
        val builder = AlertDialog.Builder(this).apply{
            setTitle(resources.getString(R.string.title_delete_step_dialog))
            setMessage(resources.getString(R.string.msg_delete_step_dialog, step.name))
            setPositiveButton(resources.getString(R.string.btn_delete), { dialogInterface: DialogInterface, i: Int ->
                viewModel.deleteStep(viewModel.guideline.value!!.id, step)
            })
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onLoadImageFromAPI(step: Step) {
        Glide.with(this)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load("https://avatars.mds.yandex.net/get-pdb/1911901/744af759-4eca-4050-8582-b8211e14a1e2/s1200")
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    //TODO("add handler")
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    val destFile = File(createTempImageFileInInternalStorage().path)
                    val fos = FileOutputStream(destFile)
                    resource?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    step.imagePath = destFile.path
                    Message().apply {
                        this.obj = step.stepId
                        imageHandler.sendMessage(this)
                    }
                    return false
                }
            })
            .submit()

    }

    private val imageHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            viewModel.updatedStepId.value = msg.obj as String
            super.handleMessage(msg)
        }
    }
}
