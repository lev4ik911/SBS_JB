package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
//import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment :
    BaseEventsFragment<LoginFragmentBinding, LoginViewModel, LoginViewModel.EventsListener>(),
    LoginViewModel.EventsListener {
    override val layoutId: Int = R.layout.login_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: LoginViewModel by viewModel()

    override fun routeToLoginScreen() {
        (activity as LoginActivity).navController.navigate(R.id.navigation_login)
    }


    override fun routeToMainScreen() {
        (activity as LoginActivity).navController.navigate(R.id.navigation_mainActivity)
    }

    override fun flipToPassword() {

  //      flipper_login.setInAnimation(context, R.anim.slide_in_right)
   //     flipper_login.setOutAnimation(context, R.anim.slide_out_left)
    //    flipper_login.showPrevious()
    }

    override fun flipToLogin() {
      //  flipper_login.setInAnimation(context, android.R.anim.slide_in_left)
      //  flipper_login.setOutAnimation(
      //      context,
      //      android.R.anim.slide_out_right
      //  )
      //  flipper_login.showNext()
    }
}
