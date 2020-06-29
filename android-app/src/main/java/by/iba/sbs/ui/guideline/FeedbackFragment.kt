package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.adapters.BaseBindingAdapter
import by.iba.sbs.databinding.FeedbackFragmentBinding
import by.iba.sbs.databinding.InstructionFeedbackListItemBinding
import by.iba.sbs.library.model.Feedback
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory

class FeedbackFragment : MvvmFragment<FeedbackFragmentBinding, GuidelineViewModel>() {
    override val layoutId: Int = R.layout.feedback_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        requireActivity().let {
            ViewModelProvider(it).get(GuidelineViewModel::class.java)
        }
    }

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
        viewModel.feedback.addObserver {
            feedbackAdapter.addItems(it)
        }
    }
}
