package by.iba.sbs.ui.guideline

import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CardsFragment : BaseFragment<StepsFragmentBinding, GuidelineViewModel>() {
    override val layoutId: Int = R.layout.cards_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: GuidelineViewModel by sharedViewModel()
}
