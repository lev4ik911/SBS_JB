package by.iba.sbs.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.navigation.findNavController
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.mvvmbase.custom.bottomnavigation.BottomNavigation
import by.iba.sbs.R
import by.iba.sbs.databinding.ActivityMainBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity :
    BaseEventsActivity<ActivityMainBinding, MainViewModel, MainViewModel.EventsListener>(),
    MainViewModel.EventsListener {
    enum class ActiveTabEnum(var index: Int) {
        ID_HOME(1),
        ID_FAVORITES(2),
        ID_SEARCH(3),
        ID_PROFILE(4)
    }

    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)))
    }
    override val viewModel: MainViewModel by viewModel()
    override val layoutId: Int = R.layout.activity_main
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: BottomNavigation = this.findViewById(R.id.nav_view)
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
                ActiveTabEnum.ID_FAVORITES.index,
                R.drawable.star_outline
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
                ActiveTabEnum.ID_PROFILE.index,
                R.drawable.account_tie
            )
        )
        navView.setCount(ActiveTabEnum.ID_HOME.index, "15")

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
            navController.navigate(
                when (it.id) {
                    ActiveTabEnum.ID_HOME.index -> R.id.navigation_dashboard
                    ActiveTabEnum.ID_FAVORITES.index -> R.id.navigation_favorites
                    ActiveTabEnum.ID_SEARCH.index -> R.id.navigation_notifications
                    ActiveTabEnum.ID_PROFILE.index -> {
                        if (settings.accessToken.isEmpty()) {
                            R.id.navigation_login_fragment
                        } else {
                            R.id.navigation_profile
                        }
                    }
                    else -> R.id.navigation_home
                }
            )
            invalidateOptionsMenu()
        }
        navView.show(viewModel.activeTab.value!!)
    }

    fun setNavigationIcon(visible: Boolean) {
        if (visible)
            toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.chevron_left)
        else
            toolbar.navigationIcon = null
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
