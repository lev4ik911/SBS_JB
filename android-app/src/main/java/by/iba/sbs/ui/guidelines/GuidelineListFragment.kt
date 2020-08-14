package by.iba.sbs.ui.guidelines

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.adapters.SearchSuggestionAdapter
import by.iba.sbs.databinding.InstructionListFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.GuidelineListViewModelShared
import by.iba.sbs.tools.Tools
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

@UnstableDefault
@ImplicitReflectionSerializer
class GuidelineListFragment :
    MvvmEventsFragment<InstructionListFragmentBinding, GuidelineListViewModelShared, GuidelineListViewModelShared.EventsListener>(),
    GuidelineListViewModelShared.EventsListener {
    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)))
    }
    override val layoutId: Int = R.layout.instruction_list_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModelClass.name, msg)
    }

    override val viewModelClass: Class<GuidelineListViewModelShared> =
        GuidelineListViewModelShared::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        GuidelineListViewModelShared(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

    //override val viewModel: GuidelineListViewModelShared by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private lateinit var searchView: MaterialSearchView
    private lateinit var instructionsAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>
    private var isInitialization = true
    private lateinit var mSuggestionsListView: ListView
    private lateinit var mSearchLayout: View
    private lateinit var mTintView: View
    private lateinit var mEmptyBtn: ImageButton
    private var showSuggestions = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<Toolbar>(R.id.toolbar_main).apply {
            title = if (viewModel.searchedText.value.isNotEmpty()) {
                resources.getString(R.string.title_search_results, viewModel.searchedText.value)
            } else {
                resources.getString(R.string.title_search)
            }
        }
        viewModel.getSearchHistoryList()
        searchView = requireActivity().findViewById(R.id.search_view)
        mSearchLayout = requireActivity().findViewById(R.id.search_layout)
        mSuggestionsListView = mSearchLayout.findViewById(R.id.suggestion_list)
        mTintView = mSearchLayout.findViewById(R.id.transparent_view)
        mEmptyBtn = mSearchLayout.findViewById(R.id.action_empty_btn)

        var searchAdapter: SearchSuggestionAdapter? = null
        searchView.setHint(resources.getString(R.string.hint_search))

        viewModel.suggestions.addObserver {
            if (searchAdapter != null) {
                mSuggestionsListView.visibility = View.VISIBLE
                searchAdapter!!.setSuggestionIcon(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.baseline_search_24
                    )!!
                )
                searchAdapter!!.setItems(it)
            }
        }

        mEmptyBtn.setOnClickListener {
            searchView.setQuery("",false)
            mSuggestionsListView.visibility = View.VISIBLE
            viewModel.searchedText.value = ""
            viewModel.loadInstructions(false)
        }

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchedText.value = query
                viewModel.getFilteredGuidelines(query)
                //TODO(Add logic API call)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 3) {
                    if (showSuggestions){
                        viewModel.loadSuggestions(newText)
                    }
                    else
                        showSuggestions = true
                }
                if (newText.length < 3 && !isInitialization) {
                    searchAdapter!!.setSuggestionIcon(ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_history_24)!!)
                    searchAdapter!!.setItems(viewModel.searchHistoryList)
                }
                return true
            }
        })

        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                searchAdapter = SearchSuggestionAdapter(
                    requireActivity(),
                    viewModel.searchHistoryList,
                    ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_history_24)!!,
                    true
                )
                searchView.setAdapter(searchAdapter)
                mTintView.visibility = View.VISIBLE
                isInitialization = false
                viewModel.searchedText.value.apply {
                    if (this.isNotEmpty())
                        searchView.setQuery(this, false)
                    else
                        mSuggestionsListView.visibility = View.VISIBLE
                }
            }
            override fun onSearchViewClosed() {
                searchView.setAdapter(null)
                activity!!.findViewById<Toolbar>(R.id.toolbar_main).apply {
                    val searchText = viewModel.searchedText.value
                    if (searchText.isNotEmpty()) {
                        title = resources.getString(R.string.title_search_results, searchText)
                        viewModel.saveSearchHistoryList(searchText)
                    } else
                        title = resources.getString(R.string.title_search)
                }
            }
        })
        searchView.setOnItemClickListener { _, _, position, _ ->
            showSuggestions = false
            val query = searchAdapter!!.getItem(position)
            if (query != null) {
                searchView.setQuery(query, false)
                mSuggestionsListView.visibility = View.INVISIBLE
                viewModel.getFilteredGuidelines(query)
                viewModel.searchedText.value = query
            }
        }

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

            }
        instructionsAdapter.apply {
            if (settings.accessToken.isNotEmpty()) {
                emptyViewId = R.layout.new_item
                onEmptyViewItemClick = {
                    val intent = Intent(activity, GuidelineActivity::class.java)
                    intent.putExtra("instructionId", 0)
                    startActivity(intent)
                }
            }
        }
        binding.rvInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)

        }
//        binding.lSwipeRefresh.setOnRefreshListener {
//            viewModel.loadInstructions(true)
//        }

        viewModel.instructions.addObserver {
            instructionsAdapter.addItems(it)
            if(isAdded) {
                binding.rvInstructions.apply {
                    layoutAnimation = AnimationUtils.loadLayoutAnimation(
                        requireContext(),
                        R.anim.layout_animation_left_to_right
                    )
                    scheduleLayoutAnimation()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_action_menu, menu)
        menu.findItem(R.id.action_new).isVisible = false
        viewModel.searchedText.addObserver {
            menu.findItem(R.id.action_cancel_search).isVisible = it.isNotEmpty()
        }
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchView.setMenuItem(searchItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> {
                val intent = Intent(activity, GuidelineActivity::class.java)
                intent.putExtra("instructionId", 0)
                startActivity(intent)
            }
            R.id.action_cancel_search -> {
                viewModel.searchedText.value = ""
                viewModel.loadInstructions(false)
                requireActivity().findViewById<Toolbar>(R.id.toolbar_main).apply {
                        title = resources.getString(R.string.title_search)
                }
                mSuggestionsListView.visibility = View.VISIBLE
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        val forceRefresh = Date().day != Date(settings.lastUpdate).day
        viewModel.loadInstructions(forceRefresh)
    }
}