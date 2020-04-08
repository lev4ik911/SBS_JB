package by.iba.sbs.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.TextView
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
            it.layoutManager = LinearLayoutManager(context)
        }
        _cardAdapter.addItems(getData())
    }

    private val _cardAdapter =
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_list_item,
            R.layout.new_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_title).text = item.title
                view.findViewById<TextView>(R.id.tv_info).text = item.author
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.title == newItem.title
            })

    class ExampleListModel(val title: String, val author: String) {

    }

    private fun getData(): ArrayList<ExampleListModel> {
        val mData = ArrayList<ExampleListModel>()
        mData.add(ExampleListModel("Как сдать СМК на отлично!", "Dobry"))
        mData.add(ExampleListModel("Как попасть на проект, подготовка к интервью", "Author 2"))
        mData.add(ExampleListModel("Как стать счастливым", "Dobry"))
        return mData
    }
}