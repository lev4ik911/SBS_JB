package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepEditFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class StepEditFragment : BaseFragment<StepEditFragmentBinding, GuidelineViewModel>() {

    override val layoutId: Int = R.layout.step_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: GuidelineViewModel by sharedViewModel()
    private var stepId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepId = arguments?.getString("stepId") ?: ""
        if (stepId.isNotEmpty())
            binding.step = viewModel.steps.value!!.find { step-> step.stepId == stepId}
        else
            binding.step = Step(weight = viewModel.steps.value!!.size.plus(1))
    }

}
