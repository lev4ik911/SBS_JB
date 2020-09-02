package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
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
    private var selectedStep = Step()
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        requireActivity().let {
            ViewModelProvider(it).get(GuidelineViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarDescription.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        val stepWeight = arguments?.getInt("stepWeight") ?: 0
        if (stepWeight > 0)
            selectedStep = viewModel.steps.value.find { step -> step.weight == stepWeight }!!
        else {
            if (viewModel.steps.value.isNullOrEmpty())
                viewModel.steps.value = listOf()
            selectedStep = Step(weight = viewModel.steps.value.size.plus(1))
        }
        binding.step = selectedStep

        viewModel.steps.addObserver {
            binding.step = selectedStep
        }
    }

}
