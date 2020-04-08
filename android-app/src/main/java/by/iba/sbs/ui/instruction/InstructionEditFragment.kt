package by.iba.sbs.ui.instruction

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileFragmentBinding
import by.iba.sbs.ui.profile.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionEditFragment : BaseEventsFragment<ProfileFragmentBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {

    override val layoutId: Int = R.layout.profile_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by viewModel()

}
