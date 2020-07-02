package by.iba.sbs.ui.login

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginRegisterFragmentBinding
import by.iba.sbs.library.viewmodel.RegisterViewModel
import by.iba.sbs.tools.SystemInfo
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain


class RegisterFragment :
    MvvmFragment<LoginRegisterFragmentBinding, RegisterViewModel>() {
    override val layoutId: Int = R.layout.login_register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<RegisterViewModel> =
        RegisterViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        RegisterViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain(),
            SystemInfo(requireContext())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.navigation_login_fragment)
        }
    }

}
