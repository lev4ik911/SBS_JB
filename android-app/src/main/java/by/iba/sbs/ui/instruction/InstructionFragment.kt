package by.iba.sbs.ui.instruction

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class InstructionFragment :
    BaseEventsFragment<InstructionFragmentBinding, InstructionViewModel, InstructionViewModel.EventsListener>(),
    InstructionViewModel.EventsListener {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by viewModel()
    private lateinit var demoCollectionAdapter: TabsFragmentAdapter
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_description)?.apply {
            title = viewModel.name.value
        }

        initActionButton()
        viewModel.isFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            initActionButton()
        })
        demoCollectionAdapter = TabsFragmentAdapter(this)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = demoCollectionAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tabs_profile)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Steps"
                1 -> "Feedback"
                else -> ""
            }
        }.attach()
    }

    private fun initActionButton() {
        view?.findViewById<ImageView>(R.id.f_action_button).apply {
            when {
                viewModel.isInstructionOwner.value!! -> {
                    this?.setImageResource(R.drawable.file_document_edit_outline)
                    this?.setColorFilter(resources.getColor(R.color.colorAccent))
                }
                viewModel.isFavorite.value!! -> {
                    this?.setImageResource(R.drawable.heart)
                    this?.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
                else -> {
                    this?.setImageResource(R.drawable.heart_outline)
                    this?.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
            }
        }
    }


    override fun onCallInstructionEditor(instructionId: Int) {
        (activity as InstructionActivity).callInstructionEditor(instructionId)
    }

    class TabsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    StepsFragment()
                }
                1 -> {
                    FeedbackFragment()
                }
                else -> {
                    StepsFragment()
                }
            }
        }
    }
}
