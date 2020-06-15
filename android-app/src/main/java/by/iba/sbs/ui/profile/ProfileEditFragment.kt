package by.iba.sbs.ui.profile

import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import by.iba.sbs.BR
import by.iba.sbs.databinding.ProfileEditFragmentBinding
import by.iba.sbs.library.viewmodel.ProfileViewModel
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain


class ProfileEditFragment :
    MvvmFragment<ProfileEditFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.profile_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        ProfileViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

}
