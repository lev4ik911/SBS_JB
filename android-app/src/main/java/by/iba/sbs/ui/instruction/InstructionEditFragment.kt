package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.ui.profile.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionEditFragment : BaseEventsFragment<InstructionEditFragmentBinding, InstructionEditViewModel, InstructionEditViewModel.EventsListener>(),
    InstructionEditViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionEditViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_description)?.title =
            viewModel.name.value
        view.findViewById<RecyclerView>(R.id.rv_steps).also {
            it.adapter = _cardAdapter
            _cardAdapter.itemTouchHelper.attachToRecyclerView(it)
        }
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            _cardAdapter.addItems(it)
        })
        _cardAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
        _cardAdapter.onEmptyViewItemClick = {
            // startActivity(Intent(activity, InstructionActivity::class.java))
        }

    }
    @SuppressLint("ResourceType")
    private val _cardAdapter =
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_edit_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info).text = item.info
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.info == newItem.info
            }).also {
            it.emptyViewId = R.layout.new_step
            it.dragLayoutId = R.id.iv_drag
        }
}
