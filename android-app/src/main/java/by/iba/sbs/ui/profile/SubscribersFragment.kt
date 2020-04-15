package by.iba.sbs.ui.profile

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SubscribersFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscribersFragment : BaseEventsFragment<SubscribersFragmentBinding, SubscribersViewModel, SubscribersViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {

    override val layoutId: Int = R.layout.subscribers_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: SubscribersViewModel by viewModel()

}

