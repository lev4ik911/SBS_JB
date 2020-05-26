package by.iba.sbs.ui.dashboard

import androidx.fragment.app.Fragment
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.databinding.FavoritesFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : BaseFragment<FavoritesFragmentBinding, DashboardViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.favorites_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()


}
