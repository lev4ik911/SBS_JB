package by.iba.sbs.ui.login

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.widget.TextView
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginResetFragmentBinding
import by.iba.sbs.library.viewmodel.ResetViewModel
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain

class ResetFragment :
    MvvmEventsFragment<LoginResetFragmentBinding, ResetViewModel, ResetViewModel.EventsListener>(),
    ResetViewModel.EventsListener, TextView.OnEditorActionListener {

    override val layoutId: Int = R.layout.login_reset_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ResetViewModel> =
        ResetViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        ResetViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

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
