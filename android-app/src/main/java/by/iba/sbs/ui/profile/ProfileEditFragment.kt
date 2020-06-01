package by.iba.sbs.ui.profile

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.databinding.ProfileEditFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ProfileEditFragment :
    BaseEventsFragment<ProfileEditFragmentBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {
    override val layoutId: Int = by.iba.sbs.R.layout.profile_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by sharedViewModel()

}
