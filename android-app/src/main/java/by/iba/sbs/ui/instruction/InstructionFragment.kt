package by.iba.sbs.ui.instruction

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InstructionFragment :
    BaseFragment<InstructionFragmentBinding, InstructionViewModel>()
 {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()
    private lateinit var viewPager: ViewPager2
     var instructionId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
        instructionId = arguments?.getInt("instructionId") ?: 0
        viewModel.loadInstruction(instructionId)

        binding.toolbarDescription.apply {
            title = viewModel.name.value
        }

        initActionButton()
        viewModel.isFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            initActionButton()
        })
        viewPager = binding.viewPager
        viewPager.adapter = TabsFragmentAdapter(this)

        TabLayoutMediator(binding.tabsProfile, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_steps)
                1 -> getString(R.string.title_feedback)
                else -> ""
            }
        }.attach()
    }

    private fun initActionButton() {
        binding.fActionButton.apply {
            when {
                viewModel.isInstructionOwner.value!! -> {
                    this.setImageResource(R.drawable.file_document_edit_outline)
                    this.setColorFilter(resources.getColor(R.color.colorAccent))
                }
                viewModel.isFavorite.value!! -> {
                    this.setImageResource(R.drawable.heart)
                    this.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
                else -> {
                    this.setImageResource(R.drawable.heart_outline)
                    this.setColorFilter(resources.getColor(R.color.colorLightRed))
                }
            }
        }
    }

     inner class TabsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StepsFragment()
                1 -> FeedbackFragment()
                else -> StepsFragment()
            }
        }
    }
}
