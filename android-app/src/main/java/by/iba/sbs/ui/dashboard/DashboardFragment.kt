package by.iba.sbs.ui.dashboard

import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.DashboardFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DashboardFragment : BaseFragment<DashboardFragmentBinding, DashboardViewModel>() {
    override val layoutId: Int = R.layout.dashboard_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()
}