package by.iba.sbs.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.CategoriesListItemBinding
import by.iba.sbs.databinding.DashboardFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.databinding.InstructionListItemHorizontalBinding
import by.iba.sbs.library.model.Category
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.DashboardViewModelShared
import by.iba.sbs.tools.DownloadManager
import by.iba.sbs.tools.Tools
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

enum class GuidelineCategory {
    DEFAULT,
    FAVORITE,
    RECOMMENDED,
    POPULAR
}

class DashboardFragment :
    MvvmEventsFragment<DashboardFragmentBinding, DashboardViewModelShared, DashboardViewModelShared.EventsListener>(),
    DashboardViewModelShared.EventsListener {
    override val layoutId: Int = R.layout.dashboard_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    override val viewModelClass: Class<DashboardViewModelShared> =
        DashboardViewModelShared::class.java
    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)))
    }

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        DashboardViewModelShared(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

    private val mainViewModel: MainViewModel by sharedViewModel()

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val forceRefresh = Date().day != Date(settings.lastUpdate).day
        val categoriesAdapter =
            BaseBindingAdapter<Category, CategoriesListItemBinding, MainViewModel>(
                R.layout.categories_list_item,
                BR.category,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        binding.rvCategory.apply {
            adapter = categoriesAdapter
            layoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        }
        viewModel.categories.addObserver {
            categoriesAdapter.addItems(it)
        }
        val recommendedAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemHorizontalBinding, MainViewModel>(
                R.layout.instruction_list_item_horizontal,
                BR.instruction,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })

        binding.rvRecommended.apply {
            adapter = recommendedAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        viewModel.recommended.addObserver {
            recommendedAdapter.addItems(it)
        }
        val favoritesAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })

        binding.rvFavorite.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.favorite.addObserver {
            favoritesAdapter.addItems(it)
        }

        val popularAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                }).also {
                if (viewModel.localStorage.accessToken.isNotEmpty()) {
                    it.emptyViewId = R.layout.new_item
                    it.onEmptyViewItemClick = {
                        val intent = Intent(activity, GuidelineActivity::class.java)
                        intent.putExtra("instructionId", 0)
                        startActivity(intent)
                    }
                }
            }

        binding.rvPopular.apply {
            adapter = popularAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.popular.addObserver {
            popularAdapter.addItems(it)
            binding.lSwipeRefresh.isRefreshing = false
        }
        viewModel.loadUserFavorites(forceRefresh)
        binding.lSwipeRefresh.setOnRefreshListener {
            viewModel.loadRecommended(true, 4)
            viewModel.loadFavorites(true, 3)
            viewModel.loadPopular(true, 3)
        }

        viewModel.updatedGuidelineId.addObserver {
            recommendedAdapter.itemsList.indexOfFirst { guideline -> guideline.id == it }.apply {
                if (this != -1)
                    recommendedAdapter.notifyItemChanged(this)
            }
            favoritesAdapter.itemsList.indexOfFirst { guideline -> guideline.id == it }.apply {
                if (this != -1) {
                    favoritesAdapter.notifyItemChanged(this)
                }
            }
            popularAdapter.itemsList.indexOfFirst { guideline -> guideline.id == it }.apply {
                if (this != -1)
                    popularAdapter.notifyItemChanged(this)
            }
        }

        viewModel.isOfflineMode.addObserver {
            val offlineMode = mainViewModel.offlineMode
            if(offlineMode.value != it)
                offlineMode.value = it
        }
        mainViewModel.isOfflineMode.addObserver {
            val offlineMode = viewModel.offlineMode
            if(offlineMode.value != it)
                offlineMode.value = it
        }

    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setNavigationIcon(false)
        val forceRefresh = Date().day != Date(settings.lastUpdate).day
        viewModel.loadUserFavorites(forceRefresh)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
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

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModel::class.java.name, msg)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onFavoritesLoaded(forceRefresh: Boolean) {
        viewModel.loadRecommended(forceRefresh, 4)
        viewModel.loadFavorites(forceRefresh, 3)
        viewModel.loadPopular(forceRefresh, 3)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun loadImage(url: String, guideline: Guideline) {
       DownloadManager(requireContext()).apply {
           this.downloadImage(url,
                               guideline.id,
                               remoteImageId =  guideline.remoteImageId,
                               item = guideline,
                               imageHandler =  imageHandler)
       }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val imageHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val guideline = msg.obj as Guideline
            viewModel.updatedGuidelineId.value = guideline.id
            viewModel.saveGuidelinePreviewImageInLocalDB(guideline)
            super.handleMessage(msg)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
    }

}