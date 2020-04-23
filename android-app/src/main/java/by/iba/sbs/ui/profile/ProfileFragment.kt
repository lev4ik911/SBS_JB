package by.iba.sbs.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileFragmentBinding
import by.iba.sbs.ui.walkthrough.WalkthroughActivity
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment :  BaseEventsFragment<ProfileFragmentBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener {

    override val layoutId: Int = R.layout.profile_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by viewModel()
    private lateinit var demoCollectionPagerAdapter: TabsAdapter
    private lateinit var viewPager: ViewPager2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   demoCollectionPagerAdapter = TabsAdapter(this)
    //    viewPager = view.findViewById(R.id.view_pager)
      //  viewPager.adapter = demoCollectionPagerAdapter
       // val tabLayout = view.findViewById<TabLayout>(R.id.tabs_profile)
       // tabLayout.setupWithViewPager(viewPager)
    }
    class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            val fragment = SubscribersFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                //putInt(ARG_OBJECT, position + 1)
            }
            return fragment
        }
    }

}
