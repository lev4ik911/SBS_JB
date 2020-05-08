package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.binding.StepBinding
import by.iba.sbs.databinding.InstructionStepListItemBinding
import by.iba.sbs.databinding.StepsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class StepsFragment : BaseFragment<StepsFragmentBinding, InstructionViewModel>() {
    override val layoutId: Int = R.layout.steps_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSteps.apply {
            adapter = stepsAdapter
        }
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            stepsAdapter.addItems(it.map { step -> StepBinding(step) })
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        BaseBindingAdapter<StepBinding, InstructionStepListItemBinding>(
            R.layout.instruction_step_list_item,
            BR.step,
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }
        )
}
