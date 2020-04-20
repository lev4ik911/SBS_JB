package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.ui.login.LoginActivity
import com.yalantis.ucrop.UCrop
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionEditFragment : BaseEventsFragment<InstructionEditFragmentBinding, InstructionEditViewModel, InstructionEditViewModel.EventsListener>(),
    InstructionEditViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionEditViewModel by viewModel()
    var instructionId = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
          if(instructionId ==0)
              activity?.finish()
            else
              findNavController().navigate(R.id.navigation_instruction_view)
        }
        instructionId = arguments?.getInt("instructionId") ?: 0
        viewModel.loadInstruction(instructionId)
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_description)?.title =
            viewModel.name.value
        view.findViewById<RecyclerView>(R.id.rv_steps).apply {
            this.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(this)
        }
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
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_edit_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info)?.text = item.info
                view.findViewById<ImageView>(R.id.iv_camera)?.setOnClickListener {
                    (activity as InstructionActivity).callImageSelector()
                }
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.info == newItem.info
            }).also {
            it.emptyViewId = R.layout.new_step
            it.dragLayoutId = R.id.iv_drag
        }

    override fun onAfterSaveAction() {
       activity?.findNavController(R.id.fragment_navigation_instruction)?.navigate(R.id.navigation_instruction_view)
    }
}
