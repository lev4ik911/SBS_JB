package by.iba.sbs.ui.profile

import androidx.fragment.app.Fragment
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SettingsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment<SettingsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.settings_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by sharedViewModel()
}
