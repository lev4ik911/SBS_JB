package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class InstructionFragment :
    BaseEventsFragment<InstructionFragmentBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
        activity?.finish()
        }
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_description)?.apply {
        title = viewModel.name.value
        }
        view.findViewById<RecyclerView>(R.id.rv_steps).also {
            it.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(it)
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
        initActionButton()
        viewModel.isFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            initActionButton()
        })
    }

    private fun initActionButton() {
        view?.findViewById<ImageView>(R.id.f_action_button).apply {
            when {
                viewModel.isInstructionOwner.value!! -> {
                    this?.setImageResource(R.drawable.file_document_edit_outline)
                    this?.setColorFilter(resources.getColor(R.color.colorAccent))
                }
                viewModel.isFavorite.value!! -> {
                    this?.setImageResource(R.drawable.heart)
                    this?.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
                else -> {
                    this?.setImageResource(R.drawable.heart_outline)
                    this?.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info).text = item.info
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.info == newItem.info
            }).also {
        }

    override fun onCallInstructionEditor(instructionId: Int) {
        (activity as InstructionActivity).callInstructionEditor(instructionId)
    }

}
