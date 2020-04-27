package by.iba.sbs.ui.instruction

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionActivityBinding
import com.yalantis.ucrop.UCrop
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionActivity : BaseEventsActivity<InstructionActivityBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {
    override val layoutId: Int = R.layout.instruction_activity
    override val viewModel: InstructionViewModel by viewModel()
    override val viewModelVariableId: Int =  by.iba.sbs.BR.viewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instructionId = intent?.getIntExtra("instructionId", 0) ?: 0
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)

        findNavController(R.id.fragment_navigation_instruction).navigate(
            if (instructionId == 0) R.id.navigation_instruction_edit else R.id.navigation_instruction_view,
            bundle
        )
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

    override fun onCallInstructionEditor(instructionId: Int) {
        val bundle = Bundle()
        bundle.putInt("instructionId", instructionId)
        findNavController(R.id.fragment_navigation_instruction).navigate(R.id.navigation_instruction_edit,bundle)
    }

    fun onAfterSaveAction() {
        findNavController(R.id.fragment_navigation_instruction)
            .navigate(R.id.navigation_instruction_view)
    }
}
