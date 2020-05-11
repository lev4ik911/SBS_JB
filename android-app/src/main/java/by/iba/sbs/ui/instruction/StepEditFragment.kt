package by.iba.sbs.ui.instruction

import androidx.fragment.app.Fragment
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepEditFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * A simple [Fragment] subclass.
 */
class StepEditFragment : BaseFragment<StepEditFragmentBinding, InstructionViewModel>() {

    override val layoutId: Int = R.layout.step_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()

}
