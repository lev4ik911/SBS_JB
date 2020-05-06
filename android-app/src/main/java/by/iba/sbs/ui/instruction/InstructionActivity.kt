package by.iba.sbs.ui.instruction

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.sbs.BuildConfig
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionActivityBinding
import by.iba.sbs.ui.profile.ProfileActivity
import com.yalantis.ucrop.UCrop
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*


class InstructionActivity :
    BaseEventsActivity<InstructionActivityBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {
    override val layoutId: Int = R.layout.instruction_activity
    override val viewModel: InstructionViewModel by viewModel()
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel


    private val PICK_IMAGE_GALLERY_REQUEST_CODE = 609
    private val CAMERA_ACTION_PICK_REQUEST_CODE = 610
    private val WRITE_STORAGE_PERMISSION = 611
    private val ACCESS_TO_CAMERA_PERMISSION = 612
    private var usingCamera = false
    private var selectedAction = 0
    private var absolutePhotoPath = ""
    private var stepId: Int = 0


    private enum class ImageActions(val key: Int, val stringId: Int) {
        EditCurrent(0, R.string.select_variant_edit_current),
        TakePhoto(1, R.string.select_variant_take_photo),
        FromGallery(2, R.string.select_variant_from_gallery)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instructionId = intent?.getIntExtra("instructionId", 0) ?: 0
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)

        viewModel.loadInstruction(instructionId)

        findNavController(R.id.fragment_navigation_instruction).navigate(
            if (instructionId == 0) R.id.navigation_instruction_edit else R.id.navigation_instruction_view,
            bundle
        )
    }

    fun callImageSelector(_stepId: Int) {
        stepId = _stepId
        val stepHasImage = viewModel.steps.value!!.any { step -> step.stepId == stepId && step.imagePath.isNotEmpty()}
        val builder = AlertDialog.Builder(this)
        val listOfResolvedActions = ImageActions.values().filter {
            (stepHasImage && it == ImageActions.EditCurrent) || (it != ImageActions.EditCurrent)
        }

        builder
            //.setTitle("Upload from")
            .setItems(listOfResolvedActions.map {resources.getString(it.stringId) }.toTypedArray()) { _, key ->
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
                     viewModel.steps.value?.find { step -> step.stepId == stepId && step.imagePath.isNotEmpty() }.apply {

                        if (!(this?.imagePath).isNullOrEmpty()) {
                            val sourcePath = Uri.fromFile(File(this?.imagePath))
                            val destinationUri = createTempImageFileInInternalStorage()
                            UCrop.of(sourcePath, destinationUri)
                                .withAspectRatio(1f, 1f)
                                .start(this@InstructionActivity)
                        }
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
                    val _steps = viewModel.steps.value
                    _steps?.find { step -> step.stepId == stepId }
                        .apply {
                            if (!(resultUri?.path).isNullOrEmpty())
                                this?.imagePath = resultUri?.path!!
                        }
                    viewModel.steps.value = _steps
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

    override fun onCallInstructionEditor(instructionId: Int) {
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)
        findNavController(R.id.fragment_navigation_instruction).navigate(R.id.navigation_instruction_edit, bundle)
    }

    override fun onOpenProfile(profileId: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("profileId", profileId)
        // findNavController().navigate(R.id.navigation_instruction_edit, bundle)
        startActivity(intent)
    }

    override fun onAfterSaveAction() {
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_instruction_view)
    }
}
