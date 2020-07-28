package by.iba.sbs.ui.profile

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SettingsFragmentBinding
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.ProfileViewModel
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import nl.dionsegijn.steppertouch.OnStepCallback
import nl.dionsegijn.steppertouch.StepperTouch

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : MvvmFragment<SettingsFragmentBinding, ProfileViewModel>() {
    private val settings: LocalSettings by lazy {
        LocalSettings(AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)))
    }
    override val layoutId: Int = R.layout.settings_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelStoreOwner(): ViewModelStoreOwner {
        return requireActivity()
    }

    override fun viewModelFactory(): ViewModelProvider.Factory =
        createViewModelFactory {
            requireActivity().let {
                ViewModelProvider(it).get(ProfileViewModel::class.java)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stepperTouch = view.findViewById<StepperTouch>(R.id.stepperTouch)
        stepperTouch.minValue = 0
        stepperTouch.maxValue = 10
        stepperTouch.count = viewModel.searchHistoryCount.value
        stepperTouch.sideTapEnabled = true
        stepperTouch.addStepCallback(object : OnStepCallback {
            override fun onStep(value: Int, positive: Boolean) {
                viewModel.searchHistoryCount.value = value
            }
        })
    }

}
