package by.iba.sbs.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.FavoritesFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.DashboardViewModelShared
import by.iba.sbs.tools.Tools
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : MvvmEventsFragment<FavoritesFragmentBinding, DashboardViewModelShared, DashboardViewModelShared.EventsListener>(),
    DashboardViewModelShared.EventsListener {
    override val layoutId: Int = R.layout.favorites_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    override val viewModelClass: Class<DashboardViewModelShared> =
        DashboardViewModelShared::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        DashboardViewModelShared(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)))
    }
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
                        || item.descr.contains(text, true)
            }
            if (settings.accessToken.isNotEmpty()) {
                emptyViewId = R.layout.new_item
                onEmptyViewItemClick = {
                    val intent = Intent(activity, GuidelineActivity::class.java)
                    intent.putExtra("instructionId", 0)
                    startActivity(intent)
                }
            }
        }
        (activity as MainActivity).apply {
            toolbar_main.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.chevron_left)
            toolbar_main.setNavigationOnClickListener {
                onBackPressed()
            }
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
                    viewModel.recommended.addObserver {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    }
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
                    viewModel.popular.addObserver {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    }
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
                    viewModel.favorite.addObserver {
                        favoritesAdapter.addItems(it)
                        binding.lSwipeRefresh.isRefreshing = false
                    }
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
                    viewModel.favorite.addObserver {
                        favoritesAdapter.addItems(it)
                    }
                }
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
        menu.findItem(R.id.action_new).isVisible = viewModel.localStorage.accessToken.isNotEmpty()
        menu.findItem(R.id.action_cancel_search).isVisible = false
        menu.findItem(R.id.action_search).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> {
                val intent = Intent(activity, GuidelineActivity::class.java)
                intent.putExtra("instructionId", 0)
                startActivity(intent)
            }
        }
        return true
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onStart() {
        super.onStart()
        val forceRefresh = Date().day != Date(settings.lastUpdate).day
        when (activeCategory) {
            GuidelineCategory.RECOMMENDED.ordinal -> viewModel.loadRecommended(forceRefresh)
            GuidelineCategory.POPULAR.ordinal -> viewModel.loadPopular(forceRefresh)
            GuidelineCategory.FAVORITE.ordinal -> viewModel.loadFavorites(forceRefresh)
            else -> viewModel.loadFavorites(forceRefresh)
        }
    }


    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModel::class.java.name, msg)
    }

    override fun onViewFavoritesAction() {
        //TODO("Not nessesary in implementation")
    }

    override fun onViewRecommendedAction() {
        //TODO("Not nessesary in implementation")
    }

    override fun onViewPopularAction() {
        //TODO("Not nessesary in implementation")
    }

    override fun onFavoritesLoaded(forceRefresh: Boolean) {
        //TODO("Not nessesary in implementation")
    }

}
