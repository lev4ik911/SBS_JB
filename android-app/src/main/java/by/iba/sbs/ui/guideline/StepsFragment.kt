package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.adapters.BaseBindingAdapter
import by.iba.sbs.databinding.InstructionStepListItemBinding
import by.iba.sbs.databinding.StepsFragmentBinding
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory


class StepsFragment : MvvmFragment<StepsFragmentBinding, GuidelineViewModel>() {
    override val layoutId: Int = R.layout.steps_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        requireActivity().let {
            ViewModelProviders.of(it).get(GuidelineViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stepsAdapter =
            BaseBindingAdapter<Step, InstructionStepListItemBinding, GuidelineViewModel>(
                R.layout.instruction_step_list_item,
                BR.step,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.description == newItem.description
                }
            )
        binding.rvSteps.apply {
            adapter = stepsAdapter
        }
        viewModel.steps.addObserver {
            stepsAdapter.addItems(it)
        }
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
        viewModel.updatedStepId.addObserver {
            stepsAdapter.itemsList.indexOfFirst { step -> step.stepId == it }.apply {
                stepsAdapter.notifyItemChanged(this)
            }
        }
    }

}
