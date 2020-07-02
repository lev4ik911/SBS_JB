package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.adapters.BaseBindingAdapter
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.databinding.InstructionEditStepListItemBinding
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import by.iba.sbs.tools.Tools
import com.google.android.material.appbar.AppBarLayout
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlin.math.abs


class GuidelineEditFragment :
    MvvmFragment<InstructionEditFragmentBinding, GuidelineViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    private val percentageToShowTitleAtToolbar = 0.7f
    private val percentageToHideTitleDetails = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var instructionId = ""
    override val layoutId: Int = R.layout.instruction_edit_fragment
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
        binding.appBar.addOnOffsetChangedListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (instructionId == "")
                activity?.finish()
            else {
                viewModel.onBackBtnClick()
                findNavController().navigate(R.id.navigation_instruction_view)
            }
        }
        instructionId = arguments?.getString("instructionId") ?: ""

        val stepsAdapter =
            BaseBindingAdapter<Step, InstructionEditStepListItemBinding, GuidelineViewModel>(
                R.layout.instruction_edit_step_list_item,
                BR.step,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.descr == newItem.descr
                }).also {
                it.emptyViewId = R.layout.new_step
                it.dragLayoutId = R.id.iv_drag
            }
        binding.rvSteps.apply {
            this.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(this)
        }

        viewModel.guideline.addObserver {
            binding.toolbarDescription.title = it.name
            binding.htabHeader.invalidate()
        }
        viewModel.steps.addObserver {
            stepsAdapter.addItems(it)
        }
        stepsAdapter.onItemClick = { pos, itemView, item ->
            (activity as GuidelineActivity).onEditStep(item.weight)
            // Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
        stepsAdapter.onEmptyViewItemClick = {
            (activity as GuidelineActivity).onEditStep(0)
        }
        stepsAdapter.onItemMoved = { old, new ->
            val steps = stepsAdapter.itemsList
            steps.forEachIndexed { index, step -> step.weight = index + 1 }
            viewModel.steps.postValue(steps)
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appBar.totalScrollRange
        val percentage = abs(p1).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {

            if (!mIsTheTitleVisible) {

                Tools.startAlphaAnimation(
                    binding.tvTitle,
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleVisible = true
                binding.tvTitle.text = binding.teName.text
                binding.btnToolbarAction.visibility = View.INVISIBLE
            }
        } else {
            if (mIsTheTitleVisible) {
                Tools.startAlphaAnimation(
                    binding.tvTitle,
                    mAlphaAnimationsDuration,
                    View.INVISIBLE
                )
                mIsTheTitleVisible = false
                binding.tvTitle.text = ""
                binding.btnToolbarAction.visibility = View.VISIBLE
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= percentageToHideTitleDetails) {
            if (mIsTheTitleContainerVisible) {
                binding.fActionButton.visibility = View.INVISIBLE
                Tools.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                Tools.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration, View.INVISIBLE
                )
                binding.fActionButton.visibility = View.VISIBLE
                mIsTheTitleContainerVisible = true
            }
        }
    }
}
