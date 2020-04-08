package by.iba.sbs.ui.instruction

import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class InstructionFragment : BaseEventsFragment<InstructionFragmentBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {

    override val layoutId: Int = R.layout.profile_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by viewModel()
}
