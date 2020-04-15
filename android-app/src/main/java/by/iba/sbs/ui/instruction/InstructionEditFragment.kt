package by.iba.sbs.ui.instruction

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.ui.profile.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class InstructionEditFragment : BaseEventsFragment<InstructionEditFragmentBinding, InstructionEditViewModel, InstructionEditViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionEditViewModel by viewModel()

}
