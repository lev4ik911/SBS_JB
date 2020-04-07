package by.iba.sbs.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import by.iba.ecl.ui.MainViewModel
import by.iba.mvvmbase.custom.bottomnavigation.BottomNavigation
import by.iba.sbs.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    enum class ActiveTabEnum(var index: Int) {
        ID_HOME(1),
        ID_INSTRUCTIONS(2),
        ID_SEARCH(3),
        ID_PROFILE(4)
    }
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigation = this.findViewById(R.id.nav_view)
        val toolbar = this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.fragment_navigation_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home,
//                R.id.navigation_dashboard,
//                R.id.navigation_notifications,
//                R.id.navigation_profile
//            )
//        )
//
//        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_HOME.index,
                R.drawable.baseline_home_24
            )
        )
        navView.add(
            BottomNavigation.Model(
                ActiveTabEnum.ID_INSTRUCTIONS.index,
                R.drawable.clipboard_list_outline
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
        navView.show(viewModel.activeTab.value!!)
        navView.setOnShowListener {

            val name = when (it.id) {
                ActiveTabEnum.ID_HOME.index -> "HOME"
                ActiveTabEnum.ID_INSTRUCTIONS.index -> "INSTRUCTIONS"
                ActiveTabEnum.ID_SEARCH.index -> "SEARCH"
                ActiveTabEnum.ID_PROFILE.index -> "PROFILE"
                else -> ""
            }
        }

        navView.setOnClickMenuListener {
            navController.popBackStack()
            viewModel.activeTab.value = it.id
            navController.navigate(
                when (it.id) {
                    ActiveTabEnum.ID_HOME.index -> R.id.navigation_dashboard
                    ActiveTabEnum.ID_INSTRUCTIONS.index -> R.id.navigation_search
                    ActiveTabEnum.ID_SEARCH.index -> R.id.navigation_notifications
                    ActiveTabEnum.ID_PROFILE.index -> R.id.navigation_profile
                    else -> R.id.navigation_home
                }
            )
            invalidateOptionsMenu()
        }
    }
}
