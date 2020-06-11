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
    private var stepWeight = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepWeight = arguments?.getInt("stepWeight") ?: 0
        if (stepWeight>0)
            binding.step = viewModel.steps.value!!.find { step-> step.weight == stepWeight}
        else {
            if (viewModel.steps.value.isNullOrEmpty())
                viewModel.steps.value = listOf()
            binding.step = Step(weight = viewModel.steps.value!!.size.plus(1))
        }
    }

}
