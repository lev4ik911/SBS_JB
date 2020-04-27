package by.iba.sbs.ui.notifications

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionListFragmentBinding
import by.iba.sbs.ui.instruction.InstructionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class InstructionListFragment :
    BaseEventsFragment<InstructionListFragmentBinding, InstructionListViewModel, InstructionListViewModel.EventsListener>(),
    InstructionListViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_list_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionListViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)
        }
        instructionsAdapter.addItems(getData())
        instructionsAdapter.onItemClick = { pos, itemView, item ->
            val transitionSharedNameImgView = this.getString(R.string.transition_name_img_view)
            val transitionSharedNameTxtView = this.getString(R.string.transition_name_txt_view)
            var imageViewPair: Pair<View, String>
            val textViewPair: Pair<View, String>
             itemView.findViewById<ImageView>(R.id.iv_preview).apply {
                 this.transitionName = transitionSharedNameImgView
                 imageViewPair = Pair.create(this, transitionSharedNameImgView)
             }
             itemView.findViewById<TextView>(R.id.tv_title).apply {
                 this.transitionName = transitionSharedNameTxtView
                 textViewPair = Pair.create(this, transitionSharedNameTxtView)
             }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity as Activity,
                imageViewPair,
                textViewPair)
            val intent = Intent(activity, InstructionActivity::class.java)
            intent.putExtra("instructionId", 12)
            startActivity(intent, options.toBundle())
        }
        instructionsAdapter.onEmptyViewItemClick = {
            val intent = Intent(activity, InstructionActivity::class.java)
            intent.putExtra("instructionId", 0)
           // findNavController().navigate(R.id.navigation_instruction_edit, bundle)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceType")
    private val instructionsAdapter =
        EmptyViewAdapter<ExampleListModel>(
            R.layout.instruction_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_title).text = item.title
                view.findViewById<TextView>(R.id.tv_info).text = item.author
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.title == newItem.title
            }).also {
            it.emptyViewId = R.layout.new_item
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