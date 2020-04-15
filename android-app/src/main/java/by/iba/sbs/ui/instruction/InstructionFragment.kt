package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class InstructionFragment : BaseEventsFragment<InstructionFragmentBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_description)?.title = viewModel.name.value
        view.findViewById<RecyclerView>(R.id.rv_steps).also {
            it.adapter = _cardAdapter
            _cardAdapter.itemTouchHelper.attachToRecyclerView(it)
        }
        _cardAdapter.addItems(getData())
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
            R.layout.instruction_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info).text = item.info
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.info == newItem.info
            }).also {
            it.emptyViewId = R.layout.new_step
            it.dragLayoutId = R.id.iv_drag
        }

    class ExampleListModel( val info: String) {

    }

    private fun getData(): ArrayList<ExampleListModel> {
        val mData = ArrayList<ExampleListModel>()
        mData.add(ExampleListModel("Свиную шею нарезать одинаковыми кусочками, не мелкими."))
        mData.add(ExampleListModel(" Лук нарезать тонкими кольцами и помять руками (или измельчить лук с помощью кухонной техники)."))
        mData.add(ExampleListModel("Уложить на дно емкости слой мяса, сверху посолить, поперчить."))
        mData.add(ExampleListModel(" Хорошо перемешать мясо с луком и остальными ингредиентами маринада."))
        mData.add(ExampleListModel("Оставить свинину в маринаде на несколько часов в холодильнике."))
        mData.add(ExampleListModel(" Затем нанизывать кусочки маринованного мяса на шампуры и жарить шашлык из свинины."))

        return mData
    }
}
