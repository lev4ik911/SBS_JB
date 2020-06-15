package by.iba.sbs.ui.profile

import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SettingsFragmentBinding
import by.iba.sbs.library.viewmodel.ProfileViewModel
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : MvvmFragment<SettingsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.settings_fragment
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
