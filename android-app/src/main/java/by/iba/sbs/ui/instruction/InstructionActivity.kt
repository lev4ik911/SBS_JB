package by.iba.sbs.ui.instruction

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import by.iba.sbs.R
import com.yalantis.ucrop.UCrop
import org.koin.androidx.fragment.android.setupKoinFragmentFactory


class InstructionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instruction_activity)
        val instructionId = intent?.getIntExtra("instructionId", 0) ?: 0
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)

        findNavController(R.id.fragment_navigation_instruction).navigate(
            if (instructionId == 0) R.id.navigation_instruction_edit else R.id.navigation_instruction_view,
            bundle
        )
    }

    fun callInstructionEditor(instructionId:Int) {
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)
        findNavController(R.id.fragment_navigation_instruction).navigate(R.id.navigation_instruction_edit,bundle)
    }

    fun callImageSelector() {
//        UCrop
//            .of(sourceUri, destinationUri)
//            .withAspectRatio(16F, 9F)
//         //  .withMaxResultSize(maxWidth, maxHeight)
//            .start(this)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri? = UCrop.getOutput(data!!)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }

    fun onToolbarClick(view: View) {
        onBackPressed()
    }
}
