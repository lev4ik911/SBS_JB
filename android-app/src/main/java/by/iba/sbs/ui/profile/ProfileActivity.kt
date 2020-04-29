package by.iba.sbs.ui.profile

import android.view.View
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.sbs.databinding.ProfileActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity :
    BaseEventsActivity<ProfileActivityBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {
    override val layoutId: Int = by.iba.sbs.R.layout.profile_activity
    override val viewModel: ProfileViewModel by viewModel()
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel

    fun onToolbarClick(view: View) {
        onBackPressed()
    }
}

