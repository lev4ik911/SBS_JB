package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionEditFragment :
    BaseEventsFragment<InstructionEditFragmentBinding, InstructionEditViewModel, InstructionEditViewModel.EventsListener>() {

    override val layoutId: Int = R.layout.instruction_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionEditViewModel by viewModel()
    var instructionId = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (instructionId == 0)
                activity?.finish()
            else
                findNavController().navigate(R.id.navigation_instruction_view)
        }
        instructionId = arguments?.getInt("instructionId") ?: 0

        viewModel.loadInstruction(instructionId)

        binding.rvSteps.apply {
            this.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(this)
        }
        viewModel.name.observe(viewLifecycleOwner, Observer {
            binding.toolbarDescription.title = it
        })
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            stepsAdapter.addItems(it)
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
        stepsAdapter.onEmptyViewItemClick = {
            // startActivity(Intent(activity, InstructionActivity::class.java))
        }
    }

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        EmptyViewAdapter<Step>(
            R.layout.instruction_edit_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info)?.text = item.description
                view.findViewById<ImageView>(R.id.iv_camera)?.setOnClickListener {
                    (activity as InstructionActivity).callImageSelector()
                }
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }).also {
            it.emptyViewId = R.layout.new_step
            it.dragLayoutId = R.id.iv_drag
        }
}
