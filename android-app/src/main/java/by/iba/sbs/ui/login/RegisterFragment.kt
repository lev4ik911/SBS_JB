package by.iba.sbs.ui.login

import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginRegisterFragmentBinding
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.RegisterViewModel
import by.iba.sbs.tools.SystemInfo
import by.iba.sbs.tools.Tools
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain


class RegisterFragment :
    MvvmEventsFragment<LoginRegisterFragmentBinding, RegisterViewModel, RegisterViewModel.EventsListener>(),
    RegisterViewModel.EventsListener {
    override val layoutId: Int = R.layout.login_register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<RegisterViewModel> = RegisterViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        RegisterViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain(),
            SystemInfo(requireContext())
        )
    }

    override fun routeToLoginScreen() {
        findNavController().navigate(R.id.action_navigation_register_to_navigation_login_fragment)
    }

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModelClass.name, msg)
    }
}
