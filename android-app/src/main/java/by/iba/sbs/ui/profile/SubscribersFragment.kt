package by.iba.sbs.ui.profile

import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SubscribersFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SubscribersFragment : BaseFragment<SubscribersFragmentBinding, ProfileViewModel>() {

    override val layoutId: Int = R.layout.subscribers_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by sharedViewModel()

}

