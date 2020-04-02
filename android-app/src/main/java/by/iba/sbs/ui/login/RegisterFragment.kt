package by.iba.sbs.ui.login

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.databinding.RegisterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment :
    BaseEventsFragment<RegisterFragmentBinding, RegisterViewModel, RegisterViewModel.EventsListener>(),
    RegisterViewModel.EventsListener  {
    override val layoutId: Int = by.iba.sbs.R.layout.register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: RegisterViewModel by viewModel()



}
