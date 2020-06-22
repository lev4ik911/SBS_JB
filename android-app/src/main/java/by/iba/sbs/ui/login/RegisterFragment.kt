package by.iba.sbs.ui.login

import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginRegisterFragmentBinding
import by.iba.sbs.library.viewmodel.LoginViewModel
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class RegisterFragment :
    MvvmFragment<LoginRegisterFragmentBinding, LoginViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.login_register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<LoginViewModel> =
        LoginViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        val viewModel: LoginViewModel by sharedViewModel()
        return@createViewModelFactory viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.navigation_login_fragment)
        }
    }
}
