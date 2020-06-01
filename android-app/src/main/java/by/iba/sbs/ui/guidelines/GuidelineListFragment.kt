package by.iba.sbs.ui.guidelines

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionListFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.ui.MainViewModel
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@UnstableDefault
@ImplicitReflectionSerializer
class GuidelineListFragment :
    BaseEventsFragment<InstructionListFragmentBinding, GuidelineListViewModel, GuidelineListViewModel.EventsListener>(),
    GuidelineListViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_list_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: GuidelineListViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val instructionsAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                }).also {
                it.emptyViewId = R.layout.new_item
            }

        binding.rvInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)
            it.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.layout_animation_right_to_left
            )
        }
        binding.lSwipeRefresh.setOnRefreshListener {
            viewModel.loadInstructions(true)
        }

        viewModel.instructions.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            instructionsAdapter.addItems(it)
            binding.rvInstructions.scheduleLayoutAnimation()
        })
    }
    override fun onStart() {
        super.onStart()
        viewModel.loadInstructions(false)
    }
}