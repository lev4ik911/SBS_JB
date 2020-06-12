package by.iba.sbs.ui.profile

import android.view.View
import androidx.navigation.findNavController
import by.iba.mvvmbase.BaseEventsActivity
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity :
    BaseEventsActivity<ProfileActivityBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {
    override val layoutId: Int = R.layout.profile_activity
    override val viewModel: ProfileViewModel by viewModel()
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel

    fun onToolbarClick(view: View) {
        onBackPressed()
    }

    override fun onActionButtonAction() {
        when {
            viewModel.isMyProfile.value!! -> {
                findNavController(R.navigation.profile_navigation).navigate(R.id.navigation_profile_edit_fragment)
            }
            else -> {
                viewModel.isFavorite.value = viewModel.isFavorite.value?.not()
            }
        }
    }
}

