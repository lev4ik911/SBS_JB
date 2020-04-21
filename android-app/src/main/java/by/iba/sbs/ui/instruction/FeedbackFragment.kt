package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.FeedbackFragmentBinding
import by.iba.sbs.library.model.Feedback
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedbackFragment : BaseFragment<FeedbackFragmentBinding, InstructionViewModel>() {
    override val layoutId: Int = R.layout.feedback_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.rv_feedback).also {
            it.adapter = feedbackAdapter
        }
        viewModel.feedback.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            feedbackAdapter.addItems(it)
        })
        feedbackAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
    }


    @SuppressLint("ResourceType")
    private val feedbackAdapter =
        EmptyViewAdapter<Feedback>(
            R.layout.instruction_feedback_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_author).text = item.author
                view.findViewById<TextView>(R.id.tv_feedback).text = item.description
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }).also {
        }
}
