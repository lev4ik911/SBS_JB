package by.iba.sbs.ui.guidelines

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionListFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
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
    var lastSearchText: String = ""
    private lateinit var instructionsAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        instructionsAdapter =
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
        instructionsAdapter.apply {
            filterCriteria = { item, text ->
                item.name.contains(text, true)
                        || item.description.contains(text, true)
            }
            onEmptyViewItemClick = {
                val intent = Intent(activity, GuidelineActivity::class.java)
                intent.putExtra("instructionId", 0)
                startActivity(intent)
            }
        }
        binding.rvInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)
            it.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.layout_animation_left_to_right
            )
        }
//        binding.lSwipeRefresh.setOnRefreshListener {
//            viewModel.loadInstructions(true)
//        }

        viewModel.instructions.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            instructionsAdapter.addItems(it)
            binding.rvInstructions.scheduleLayoutAnimation()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_action_menu, menu)
        val mSearchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        mSearchView.queryHint = "Search"
        mSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                lastSearchText = newText
                instructionsAdapter.filter.filter(newText)
                return true
            }
        })
//        mSearchView.setOnCloseListener {
//            when (activeCategory) {
//                GuidelineCategory.RECOMMENDED.ordinal ->  toolbar_main.title = resources.getString(R.string.title_recommended)
//                GuidelineCategory.FAVORITE.ordinal ->toolbar_main.title = resources.getString(R.string.title_favorites)
//                GuidelineCategory.POPULAR.ordinal -> toolbar_main.title = resources.getString(R.string.title_popular)
//            }
//            return@setOnCloseListener true
//        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadInstructions(false)
    }
}