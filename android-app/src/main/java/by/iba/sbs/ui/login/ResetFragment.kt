package by.iba.sbs.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginResetFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetFragment :
    BaseEventsFragment<LoginResetFragmentBinding, ResetViewModel, ResetViewModel.EventsListener>(),
    ResetViewModel.EventsListener, TextView.OnEditorActionListener {

    override val layoutId: Int = R.layout.login_reset_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ResetViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.navigation_login_fragment)
        }
    }

    override fun onNextButtonPressed() {
        binding.flipperLogin.also {
            it.setInAnimation(context, R.anim.slide_in_right)
            it.setOutAnimation(
                context,
                R.anim.slide_out_left
            )
            it.showNext()
        }
    }

    override fun onBackButtonPressed() {
        binding.flipperLogin.also {
            it.setInAnimation(context, R.anim.slide_in_left)
            it.setOutAnimation(context, R.anim.slide_out_right)
            it.showPrevious()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        TODO("Not yet implemented")
    }

}
