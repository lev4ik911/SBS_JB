package by.iba.sbs.ui.login

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SplashFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment :
    BaseEventsFragment<SplashFragmentBinding, SplashViewModel, SplashViewModel.EventsListener>(),
    SplashViewModel.EventsListener {
    override val layoutId: Int = R.layout.splash_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: SplashViewModel by viewModel()

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
        findNavController().navigate(R.id.navigation_mainActivity)
    }
}
