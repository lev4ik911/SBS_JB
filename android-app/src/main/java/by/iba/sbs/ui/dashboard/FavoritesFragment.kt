package by.iba.sbs.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.FavoritesFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.MainViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : BaseFragment<FavoritesFragmentBinding, DashboardViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.favorites_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    var lastSearchText: String = ""
    var activeCategory: Int = 0
    lateinit var favoritesAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        favoritesAdapter =
            BaseBindingAdapter(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        favoritesAdapter.apply {
            filterCriteria = { item, text ->
                item.name.contains(text, true)
                        || item.description.contains(text, true)
            }
            emptyViewId = R.layout.new_item
        }
        (activity as MainActivity).apply {
            toolbar_main.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.chevron_left)
            toolbar_main.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        binding.lSwipeRefresh.setOnRefreshListener {
            when (activeCategory) {
                GuidelineCategory.RECOMMENDED.ordinal -> viewModel.loadRecommended(true)
                GuidelineCategory.POPULAR.ordinal -> viewModel.loadPopular(true)
                else -> viewModel.loadFavorites(true)
            }
        }
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
                favoritesAdapter.filter.filter(newText)
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

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            activeCategory = arguments?.getInt("Category") ?: 0
            when (activeCategory) {
                GuidelineCategory.RECOMMENDED.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_recommended)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        layoutAnimation = AnimationUtils.loadLayoutAnimation(
                            requireContext(),
                            R.anim.layout_animation_right_to_left
                        )
                    }
                    viewModel.recommended.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    })
                    viewModel.loadRecommended(false)
                }
                GuidelineCategory.POPULAR.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_popular)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        layoutAnimation = AnimationUtils.loadLayoutAnimation(
                            requireContext(),
                            R.anim.layout_animation_right_to_left
                        )
                    }
                    viewModel.popular.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    })
                    viewModel.loadPopular(false)
                }
                GuidelineCategory.FAVORITE.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_favorites)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        layoutAnimation = AnimationUtils.loadLayoutAnimation(
                            requireContext(),
                            R.anim.layout_animation_right_to_left
                        )
                    }
                    viewModel.favorite.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    })
                    viewModel.loadFavorites(false)
                }
                else -> {
                    toolbar_main.navigationIcon = null
                    toolbar_main.title = resources.getString(R.string.title_favorites)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        layoutAnimation = AnimationUtils.loadLayoutAnimation(
                            requireContext(),
                            R.anim.layout_animation_right_to_left
                        )
                    }
                    viewModel.favorite.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    })
                    viewModel.loadFavorites(false)
                }
            }
        }

    }

}
