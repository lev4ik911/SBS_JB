package by.iba.sbs.ui.login

//import com.github.ybq.android.spinkit.style.FadingCircle
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginFragmentBinding
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.LoginViewModel
import by.iba.sbs.tools.SystemInfo
import by.iba.sbs.tools.Tools
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.android.synthetic.main.login_fragment.*


class LoginFragment :
    MvvmEventsFragment<LoginFragmentBinding, LoginViewModel, LoginViewModel.EventsListener>(),
    LoginViewModel.EventsListener, TextView.OnEditorActionListener {
    override val layoutId: Int = R.layout.login_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    override val viewModelClass: Class<LoginViewModel> =
        LoginViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        LoginViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain(),
            SystemInfo(requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            (activity as LoginActivity).navController.navigate(R.id.navigation_splash)
//        }
        viewModel.init()
        binding.includePassword.etPassword.setOnEditorActionListener(this)
        binding.includeEmail.etLogin.setOnEditorActionListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (v != null) {
            if (v.id == R.id.et_login && actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.includeEmail.btnNext.callOnClick()
                return true
            }

            if (v.id == R.id.et_password && actionId == EditorInfo.IME_ACTION_DONE) {
                binding.includePassword.btnLogin.callOnClick()
                return true
            }
        }
        return false
    }

    override fun onResetPassword() {
        findNavController().navigate(R.id.action_navigation_login_fragment_to_navigation_reset)
    }

    override fun onRegister() {
        findNavController().navigate(R.id.action_navigation_login_fragment_to_navigation_register)
    }


    override fun routeToProfile(userId: String) {
        findNavController().navigate(
            R.id.action_navigation_login_fragment_to_navigation_profile_fragment,
            bundleOf("userId" to userId)
        )
    }

    override fun flipToPassword() {
        binding.flipperLogin.also {
            it.setInAnimation(context, R.anim.slide_in_right)
            it.setOutAnimation(context, R.anim.slide_out_left)
            it.showPrevious()
        }
        motion_container.setTransition(R.id.transition_movement)
        motion_container.transitionToEnd()
//        val tl = object: MotionLayout.TransitionListener {
//            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
//            }
//
//            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
//            }
//
//            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
//            }
//
//            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
//                if(currentId == R.id.set2) {
//                    motion_container.setTransition(R.id.transition_rotation)
//                    motion_container.transitionToEnd()
//                }
//            }
//        }
//        motion_container.setTransitionListener(tl)
    }

    override fun flipToLogin() {
//        motion_container.setTransition(R.id.transition_return)
//        motion_container.transitionToEnd()
        motion_container.transitionToStart()
        binding.flipperLogin.also {
            it.setInAnimation(context, android.R.anim.slide_in_left)
            it.setOutAnimation(context, android.R.anim.slide_out_right)
            it.showNext()
        }
    }

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModelClass.name, msg)
    }
}
