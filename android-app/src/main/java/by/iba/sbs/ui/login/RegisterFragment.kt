package by.iba.sbs.ui.login

import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginRegisterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class RegisterFragment :
    BaseFragment<LoginRegisterFragmentBinding, LoginViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.login_register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: LoginViewModel by sharedViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.navigation_login_fragment)
        }
    }
}
