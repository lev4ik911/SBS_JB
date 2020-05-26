package by.iba.sbs.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.CategoriesListItemBinding
import by.iba.sbs.databinding.DashboardFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.databinding.InstructionListItemHorizontalBinding
import by.iba.sbs.library.model.Category
import by.iba.sbs.library.model.Guideline
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DashboardFragment :
    BaseEventsFragment<DashboardFragmentBinding, DashboardViewModel, DashboardViewModel.EventsListener>(),
    DashboardViewModel.EventsListener {
    override val layoutId: Int = R.layout.dashboard_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()
    var lastSearchText: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val categoriesAdapter =
            BaseBindingAdapter<Category, CategoriesListItemBinding, DashboardViewModel>(
                R.layout.categories_list_item,
                BR.category,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        binding.rvCategory.apply {
            adapter = categoriesAdapter
            layoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
            //layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        viewModel.categories.observe(viewLifecycleOwner, Observer {
            categoriesAdapter.addItems(it)
        })
        val recommendedAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemHorizontalBinding, DashboardViewModel>(
                R.layout.instruction_list_item_horizontal,
                BR.instruction,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        binding.rvRecommended.apply {
            adapter = recommendedAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        viewModel.recommended.observe(viewLifecycleOwner, Observer {
            recommendedAdapter.addItems(it)
        })

        val favoritesAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, DashboardViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        binding.rvFavorite.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.favorite.observe(viewLifecycleOwner, Observer {
            favoritesAdapter.addItems(it)
        })

        val popularAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, DashboardViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        binding.rvPopular.apply {
            adapter = popularAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.popular.observe(viewLifecycleOwner, Observer {
            popularAdapter.addItems(it)
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
                //   mCardAdapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onViewFavoritesAction() {
        findNavController().navigate(R.id.navigation_favorites)
    }
}