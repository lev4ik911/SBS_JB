package by.iba.sbs.ui.login

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SplashFragmentBinding
import by.iba.sbs.library.viewmodel.SplashViewModel
import by.iba.sbs.tools.SystemInfo
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain

class SplashFragment :
    MvvmEventsFragment<SplashFragmentBinding, SplashViewModel, SplashViewModel.EventsListener>(),
    SplashViewModel.EventsListener {
    override val layoutId: Int = R.layout.splash_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<SplashViewModel> =
        SplashViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        SplashViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            SystemInfo(requireContext()),
            eventsDispatcherOnMain()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkCredentials()
    }

    override fun routeToLoginScreen() {
        findNavController().navigate(R.id.navigation_login_fragment)
    }

    override fun routeToRegisterScreen() {
        findNavController().navigate(R.id.navigation_register)
    }

    override fun routeToMainScreen() {
        findNavController().navigate(R.id.navigation_profile_fragment)
    }


}
