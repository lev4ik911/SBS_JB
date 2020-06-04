package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.FeedbackFragmentBinding
import by.iba.sbs.databinding.InstructionFeedbackListItemBinding
import by.iba.sbs.library.model.Feedback
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedbackFragment : BaseFragment<FeedbackFragmentBinding, GuidelineViewModel>() {
    override val layoutId: Int = R.layout.feedback_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: GuidelineViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val feedbackAdapter =
            BaseBindingAdapter<Feedback, InstructionFeedbackListItemBinding, GuidelineViewModel>(
                R.layout.instruction_feedback_list_item,
                BR.feedback,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.comment == newItem.comment
                }
            )

        binding.rvFeedback.also {
            it.adapter = feedbackAdapter
        }
        viewModel.feedback.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            feedbackAdapter.addItems(it)
        })
    }
}
