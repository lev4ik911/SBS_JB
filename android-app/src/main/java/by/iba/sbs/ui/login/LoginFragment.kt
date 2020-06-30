package by.iba.sbs.ui.login

//import com.github.ybq.android.spinkit.style.FadingCircle
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.Extentions.Companion.waitForLayout
import by.iba.mvvmbase.visibleOrNot
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginFragmentBinding
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.LoginViewModel
import by.iba.sbs.tools.SystemInfo
import com.github.ybq.android.spinkit.style.FadingCircle
import com.russhwolf.settings.AndroidSettings
import com.shashank.sony.fancytoastlib.FancyToast
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain


class LoginFragment :
    MvvmEventsFragment<LoginFragmentBinding, LoginViewModel, LoginViewModel.EventsListener>(),
    LoginViewModel.EventsListener, TextView.OnEditorActionListener {
    override val layoutId: Int = R.layout.login_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    override val viewModelClass: Class<LoginViewModel> =
        LoginViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        //   val viewModel: LoginViewModel by viewModel()
        //    return@createViewModelFactory viewModel
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
        binding.shimmerViewContainer.also {
            it.waitForLayout { inner ->
                inner.visibleOrNot(inner.top > 0)
            }
        }
        val mWaveDrawable = FadingCircle().apply {
            val size = resources.getDimension(R.dimen.spacing_large).toInt()
            this.setBounds(0, 0, size, size)
            this.color = ContextCompat.getColor(requireContext(), R.color.textColorPrimaryInverse)
            binding.includePassword.btnLogin.also {
                it.setCompoundDrawables(this, null, null, null)
                it.setPadding(
                    it.paddingLeft,
                    it.paddingTop,
                    it.paddingRight + size,
                    it.paddingBottom
                )
            }
        }
        viewModel.isLoading.addObserver {
            if (it)
                mWaveDrawable.start()
            else
                mWaveDrawable.stop()
        }
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

    override fun routeToLoginScreen() {
        findNavController().navigate(R.id.navigation_login_fragment)
    }

    override fun onResetPassword() {
        findNavController().navigate(R.id.navigation_reset)
    }

    override fun onRegister() {
        findNavController().navigate(R.id.navigation_register)
    }


    override fun routeToProfile(userId: String) {
        val bundle = bundleOf("profileId" to userId)
        findNavController().navigate(
            R.id.action_navigation_login_fragment_to_navigation_profile_fragment,
            bundle
        )
    }

    override fun flipToPassword() {
        binding.flipperLogin.also {
            it.setInAnimation(context, R.anim.slide_in_right)
            it.setOutAnimation(context, R.anim.slide_out_left)
            it.showPrevious()
        }
    }

    override fun flipToLogin() {
        binding.flipperLogin.also {
            it.setInAnimation(context, android.R.anim.slide_in_left)
            it.setOutAnimation(context, android.R.anim.slide_out_right)
            it.showNext()
        }
    }


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


}
