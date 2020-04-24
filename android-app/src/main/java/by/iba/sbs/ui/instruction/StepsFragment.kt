package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepsFragmentBinding
import by.iba.sbs.library.model.Step
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
            stepsAdapter.addItems(it)
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        BaseAdapter<Step>(
            R.layout.instruction_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info).text = item.description
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }
        ).also {
        }
}
