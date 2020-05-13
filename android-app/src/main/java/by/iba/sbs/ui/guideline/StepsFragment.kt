package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionStepListItemBinding
import by.iba.sbs.databinding.StepsFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class StepsFragment : BaseFragment<StepsFragmentBinding, GuidelineViewModel>() {
    override val layoutId: Int = R.layout.steps_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: GuidelineViewModel by sharedViewModel()

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
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            stepsAdapter.addItems(it)
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
    }


}
