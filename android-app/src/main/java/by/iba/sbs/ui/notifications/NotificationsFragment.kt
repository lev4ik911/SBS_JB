package by.iba.sbs.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.NotificationsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class NotificationsFragment :
    BaseEventsFragment<NotificationsFragmentBinding, NotificationsViewModel, NotificationsViewModel.EventsListener>(),
    NotificationsViewModel.EventsListener {

    override val layoutId: Int = R.layout.notifications_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: NotificationsViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.rv_notifications).also {
            it.adapter = _cardAdapter
            _cardAdapter.itemTouchHelper.attachToRecyclerView(it)
        }
        _cardAdapter.addItems(getData())
        _cardAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), LENGTH_LONG).show()
        }
        _cardAdapter.onEmptyViewItemClick = {
            Toast.makeText(context, "Add instruction click", LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType")
    private val _cardAdapter =
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_title).text = item.title
                view.findViewById<TextView>(R.id.tv_info).text = item.author
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.title == newItem.title
            }).also {
            it.emptyViewId =  R.layout.new_item
            it.dragLayoutId = R.id.iv_drag
        }

    class ExampleListModel(val title: String, val author: String) {

    }

    private fun getData(): ArrayList<ExampleListModel> {
        val mData = ArrayList<ExampleListModel>()
        mData.add(ExampleListModel("Как сдать СМК на отлично!", "Dobry"))
        mData.add(ExampleListModel("Как попасть на проект, подготовка к интервью", "Author 2"))
        mData.add(ExampleListModel("Как стать счастливым", "Dobry"))
        mData.add(ExampleListModel("Отпадный шашлычок", "Dobry"))
        mData.add(ExampleListModel("Что делать, если вы заразились", "Доктор"))
        mData.add(ExampleListModel("Как поставить на учет автомобиль", "Dobry"))
        mData.add(ExampleListModel("Как оформить командировку", "Dobry"))

        return mData
    }
}