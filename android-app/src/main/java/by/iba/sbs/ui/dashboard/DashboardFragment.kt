package by.iba.sbs.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
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
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.guideline.GuidelineActivity
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

enum class GuidelineCategory {
    FAVORITE,
    RECOMMENDED,
    POPULAR,
    DEFAULT
}
class DashboardFragment :
    BaseEventsFragment<DashboardFragmentBinding, DashboardViewModel, DashboardViewModel.EventsListener>(),
    DashboardViewModel.EventsListener {
    override val layoutId: Int = R.layout.dashboard_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()
    var lastSearchText: String = ""

    @UnstableDefault
    @ImplicitReflectionSerializer
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
                .also {
                    it.onItemClick = { pos, itemView, item ->
                        onOpenGuidelineAction(itemView!!, item)
                    }
                }
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
                .also {
                    it.onItemClick = { pos, itemView, item ->
                        onOpenGuidelineAction(itemView!!, item)
                    }
                }
        binding.rvFavorite.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.favorite.observe(viewLifecycleOwner, Observer {
            favoritesAdapter.addItems(it)
        })
        viewModel.loadFavorites(true, 3)

        val popularAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, DashboardViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
                .also {
                    it.onItemClick = { pos, itemView, item ->
                        onOpenGuidelineAction(itemView!!, item)
                    }
                }
        binding.rvPopular.apply {
            adapter = popularAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.popular.observe(viewLifecycleOwner, Observer {
            popularAdapter.addItems(it)
        })
        viewModel.loadPopular(true, 3)
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).toolbar_main.apply {
            navigationIcon = null
            title = resources.getString(R.string.title_home)
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
                //   mCardAdapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onViewFavoritesAction() {
        val bundle = bundleOf("Category" to GuidelineCategory.FAVORITE.ordinal)
        findNavController().navigate(R.id.navigation_favorites, bundle)
    }

    override fun onViewRecommendedAction() {
        val bundle = bundleOf("Category" to GuidelineCategory.RECOMMENDED.ordinal)
        findNavController().navigate(R.id.navigation_favorites, bundle)
    }

    override fun onViewPopularAction() {
        val bundle = bundleOf("Category" to GuidelineCategory.POPULAR.ordinal)
        findNavController().navigate(R.id.navigation_favorites, bundle)
    }

    override fun onOpenGuidelineAction(view: View, guideline: Guideline) {
        val transitionSharedNameImgView = this.getString(R.string.transition_name_img_view)
        val transitionSharedNameTxtView = this.getString(R.string.transition_name_txt_view)
        var imageViewPair: Pair<View, String>
        val textViewPair: Pair<View, String>
        view.findViewById<ImageView>(R.id.iv_preview).apply {
            this?.transitionName = transitionSharedNameImgView
            imageViewPair = Pair.create(this, transitionSharedNameImgView)
        }
        view.findViewById<TextView>(R.id.tv_title).apply {
            this?.transitionName = transitionSharedNameTxtView
            textViewPair = Pair.create(this, transitionSharedNameTxtView)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity as Activity,
            imageViewPair,
            textViewPair
        )
        val intent = Intent(activity, GuidelineActivity::class.java)
        intent.putExtra("instructionId", guideline.id)
        startActivity(intent, options.toBundle())

    }
}