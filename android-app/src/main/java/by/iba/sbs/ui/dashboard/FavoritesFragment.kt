package by.iba.sbs.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
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
import by.iba.sbs.ui.guideline.GuidelineActivity
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
    lateinit var favoritesAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>

    inner class FavoritesHandler() {
        fun onOpenGuidelineAction(view: View, guideline: Guideline) {
            val transitionSharedNameImgView =
                requireContext().getString(R.string.transition_name_img_view)
            val transitionSharedNameTxtView =
                requireContext().getString(R.string.transition_name_txt_view)
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
        favoritesAdapter.filterCriteria = { item, text ->
            item.name.contains(text, true)
                    || item.description.contains(text, true)

        }
        (activity as MainActivity).apply {
            toolbar_main.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.chevron_left)
            toolbar_main.setNavigationOnClickListener {
                onBackPressed()
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
    }
    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onStart() {
        super.onStart()


        (activity as MainActivity).apply {
            when (arguments?.getInt("Category")) {
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
                    })
                    viewModel.loadRecommended(false)
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
                    })
                    viewModel.loadFavorites(false)
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
                    })
                    viewModel.loadPopular(false)
                }
                else -> {

                }
            }
        }
    }

}
