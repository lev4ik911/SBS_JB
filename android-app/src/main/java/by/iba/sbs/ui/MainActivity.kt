package by.iba.sbs.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import by.iba.mvvmbase.custom.bottomnavigation.BottomNavigation
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ActivityMainBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity :
    MvvmEventsActivity<ActivityMainBinding, MainViewModel, MainViewModel.EventsListener>(),
    MainViewModel.EventsListener {
    enum class ActiveTabEnum(var index: Int) {
        ID_HOME(1),
        ID_SEARCH(2),
        ID_FAVORITES(3),
        ID_PROFILE(4)
    }

    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)))
    }
    override val layoutId: Int = R.layout.activity_main
    override val viewModelVariableId: Int = BR.viewmodel

    override val viewModelClass: Class<MainViewModel> =
        MainViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        MainViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)),
            eventsDispatcherOnMain()
        )
    }

    lateinit var navView: BottomNavigation
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navView = this.findViewById(R.id.nav_view)
        toolbar = this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.fragment_navigation_main)

        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_HOME.index,
                R.drawable.baseline_home_24
            )
        )
        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_SEARCH.index,
                R.drawable.baseline_search_24
            )
        )
        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_FAVORITES.index,
                R.drawable.star_outline
            )
        )
        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_PROFILE.index,
                R.drawable.account_tie
            )
        )
     //   navView.setCount(ActiveTabEnum.ID_HOME.index, "15")

        setVisibilityForFavorites()

        navView.setOnShowListener {

            when (it.id) {
                ActiveTabEnum.ID_HOME.index ->title = resources.getString(R.string.title_home)
                ActiveTabEnum.ID_FAVORITES.index ->title = resources.getString(R.string.title_favorites)
                ActiveTabEnum.ID_PROFILE.index ->title = ""
            }
        }

        navView.setOnClickMenuListener {
            navController.popBackStack()
            viewModel.activeTab.value = it.id
            when (it.id) {
                ActiveTabEnum.ID_HOME.index -> navController.navigate(R.id.navigation_dashboard)
                ActiveTabEnum.ID_FAVORITES.index -> navController.navigate(R.id.navigation_favorites)
                ActiveTabEnum.ID_SEARCH.index -> navController.navigate(R.id.navigation_guideline_list)
                ActiveTabEnum.ID_PROFILE.index -> {
                    if (settings.accessToken.isEmpty()) {
                        navController.navigate(R.id.navigation_login_fragment)
                    } else {
                        navController.navigate(R.id.navigation_profile_fragment, bundleOf("userId" to settings.userId))
                    }
                }
                else -> navController.navigate(R.id.navigation_home)
            }
            invalidateOptionsMenu()
            toolbar.visibility =
                if (it.id == ActiveTabEnum.ID_PROFILE.index) View.GONE else View.VISIBLE
        }
        navView.show(viewModel.activeTab.value!!)
        btn_close_offline_mode.setOnClickListener {
            viewModel.setOfflineMode(false)
        }
    }

    fun setNavigationIcon(visible: Boolean) {
        if (visible)
            toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.chevron_left)
        else
            toolbar.navigationIcon = null
    }

    fun setVisibilityForFavorites() {
        if(viewModel.localStorage.userId.isEmpty()) {
            navView.getCellById(ActiveTabEnum.ID_FAVORITES.index)?.visibility = View.GONE
            navView.getCellById(ActiveTabEnum.ID_FAVORITES.index)?.disableCell()
        }
        else{
            navView.getCellById(ActiveTabEnum.ID_FAVORITES.index)?.visibility = View.VISIBLE
            navView.getCellById(ActiveTabEnum.ID_FAVORITES.index)?.enableCell(false)
        }
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
            this,
            imageViewPair,
            textViewPair
        )
        val intent = Intent(this, GuidelineActivity::class.java)
        intent.putExtra("instructionId", guideline.id)
        startActivity(intent, options.toBundle())

    }


}
