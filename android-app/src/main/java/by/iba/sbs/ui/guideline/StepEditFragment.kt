package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepEditFragmentBinding
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory


class StepEditFragment : MvvmFragment<StepEditFragmentBinding, GuidelineViewModel>() {

    override val layoutId: Int = R.layout.step_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    private var stepWeight = 0
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        requireActivity().let {
            ViewModelProviders.of(it).get(GuidelineViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepWeight = arguments?.getInt("stepWeight") ?: 0
        if (stepWeight > 0)
            binding.step = viewModel.steps.value!!.find { step -> step.weight == stepWeight }
        else {
            if (viewModel.steps.value.isNullOrEmpty())
                viewModel.steps.value = listOf()
            binding.step = Step(weight = viewModel.steps.value!!.size.plus(1))
        }
    }

}
