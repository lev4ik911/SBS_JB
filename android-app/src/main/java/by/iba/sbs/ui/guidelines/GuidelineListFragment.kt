package by.iba.sbs.ui.guidelines

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionListFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.GuidelineListViewModelShared
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import com.shashank.sony.fancytoastlib.FancyToast
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
        when (msg.type) {
            MessageType.ERROR ->
                Log.e(viewModel::class.java.name, msg.getLogMessage())
            MessageType.WARNING ->
                Log.w(viewModel::class.java.name, msg.getLogMessage())
            MessageType.INFO ->
                Log.i(viewModel::class.java.name, msg.getLogMessage())
            else ->
                Log.v(viewModel::class.java.name, msg.getLogMessage())
        }
        FancyToast.makeText(
            requireContext(),
            msg.message,
            FancyToast.LENGTH_LONG,
            msg.type.index,
            false
        ).show()
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

        }
//        binding.lSwipeRefresh.setOnRefreshListener {
//            viewModel.loadInstructions(true)
//        }

        viewModel.instructions.addObserver {
            instructionsAdapter.addItems(it)
            binding.rvInstructions.apply {
                layoutAnimation = AnimationUtils.loadLayoutAnimation(
                    requireContext(),
                    R.anim.layout_animation_left_to_right
                )
                scheduleLayoutAnimation()
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
                instructionsAdapter.filter.filter(newText)
                return true
            }
        })
        menu.findItem(R.id.action_new).isVisible = viewModel.localStorage.accessToken.isNotEmpty()
//        mSearchView.setOnCloseListener {
//            when (activeCategory) {
//                GuidelineCategory.RECOMMENDED.ordinal ->  toolbar_main.title = resources.getString(R.string.title_recommended)
//                GuidelineCategory.FAVORITE.ordinal ->toolbar_main.title = resources.getString(R.string.title_favorites)
//                GuidelineCategory.POPULAR.ordinal -> toolbar_main.title = resources.getString(R.string.title_popular)
//            }
//            return@setOnCloseListener true
//        }
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

    override fun onStart() {
        super.onStart()
        val forceRefresh = Date().day != Date(settings.lastUpdate).day
        viewModel.loadInstructions(forceRefresh)
    }
}