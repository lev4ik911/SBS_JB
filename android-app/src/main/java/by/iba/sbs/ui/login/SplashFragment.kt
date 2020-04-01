package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.databinding.SplashFragmentBinding


class SplashFragment :
    BaseEventsFragment<SplashFragmentBinding, SplashViewModel, SplashViewModel.EventsListener>(),
    SplashViewModel.EventsListener {
    override val layoutId: Int = by.iba.sbs.R.layout.splash_fragment


    override val viewModelVariableId: Int
        get() = TODO("Not yet implemented")
    override val viewModel: SplashViewModel
        get() = TODO("Not yet implemented")
    override fun routeToMainScreen() {
        TODO("Not yet implemented")
    }

    override fun routeToLoginScreen() {
        TODO("Not yet implemented")
    }


}
