package by.iba.sbs.ui.profile

import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileActivityBinding
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.tools.Tools
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain

class ProfileActivity :
    MvvmEventsActivity<ProfileActivityBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {
    override val layoutId: Int = R.layout.profile_activity
    override val viewModelVariableId: Int = by.iba.sbs.BR.viewmodel
    fun onToolbarClick(view: View) {
        onBackPressed()
    }

    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        //val viewModel: ProfileViewModel by viewModel()
        //return@createViewModelFactory viewModel
        ProfileViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(this)),
            eventsDispatcherOnMain()
        )
    }

    override fun onActionButtonAction() {
        when {
            viewModel.isMyProfile.value!! -> {
                findNavController(R.navigation.profile_navigation).navigate(R.id.navigation_profile_edit_fragment)
            }
            else -> {
                viewModel.isFavorite.value = viewModel.isFavorite.value.not()
            }
        }
    }

    override fun routeToLoginScreen() {
        findNavController(R.navigation.mobile_navigation).navigate(R.id.navigation_login_fragment)
    }

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(this, viewModel::class.java.name, msg)
    }

    override fun requireAccept() {

    }
}

