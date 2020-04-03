package by.iba.sbs.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.lifecycle.Observer
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.Extentions.Companion.waitForLayout
import by.iba.mvvmbase.visibleOrNot
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginFragmentBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.ybq.android.spinkit.style.FadingCircle
//import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment :
    BaseEventsFragment<LoginFragmentBinding, LoginViewModel, LoginViewModel.EventsListener>(),
    LoginViewModel.EventsListener, TextView.OnEditorActionListener {
    override val layoutId: Int = R.layout.login_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: LoginViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
        view.findViewById<TextInputEditText>(R.id.et_password)?.setOnEditorActionListener(this)
        view.findViewById<TextInputEditText>(R.id.et_login)?.setOnEditorActionListener(this)
        view.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container).also {
            it?.waitForLayout { inner ->
                inner.visibleOrNot(inner.top > 0)
            }
        }
        val mWaveDrawable = FadingCircle().apply {
            val size = resources.getDimension(R.dimen.spacing_large).toInt()
            this.setBounds(0, 0, size, size)
            this.color = resources.getColor(R.color.textColorPrimaryInverse)
            view.findViewById<MaterialButton>(R.id.btn_login).also {
                it.setCompoundDrawables(this, null, null, null)
                it.setPadding(
                    it.paddingLeft,
                    it.paddingTop,
                    it.paddingRight + size,
                    it.paddingBottom
                )
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                mWaveDrawable.start()
            else
                mWaveDrawable.stop()
        })
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (v != null) {
            if (v.id == R.id.et_login && actionId == EditorInfo.IME_ACTION_NEXT) {
                view?.findViewById<MaterialButton>(R.id.btn_next)?.callOnClick()
                return true
            }

            if (v.id == R.id.et_password && actionId == EditorInfo.IME_ACTION_DONE) {
                view?.findViewById<MaterialButton>(R.id.btn_login)?.callOnClick()
                return true
            }
        }
        return false
    }

    override fun routeToLoginScreen() {
        (activity as LoginActivity).navController.navigate(R.id.navigation_login)
    }


    override fun routeToMainScreen() {
        (activity as LoginActivity).navController.navigate(R.id.navigation_mainActivity)
    }

    override fun flipToPassword() {
        view?.findViewById<ViewFlipper>(R.id.flipper_login).also {
            it?.setInAnimation(context, R.anim.slide_in_right)
            it?.setOutAnimation(context, R.anim.slide_out_left)
            it?.showPrevious()
        }
    }

    override fun flipToLogin() {
        view?.findViewById<ViewFlipper>(R.id.flipper_login).also {
            it?.setInAnimation(context, android.R.anim.slide_in_left)
            it?.setOutAnimation(
                context,
                android.R.anim.slide_out_right
            )
            it?.showNext()
        }
    }
}
