package by.iba.sbs.ui.instruction

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.mvvmbase.BaseFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InstructionFragment :
    BaseFragment<InstructionFragmentBinding, InstructionViewModel>(),
    AppBarLayout.OnOffsetChangedListener
 {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()
    private lateinit var viewPager: ViewPager2
     private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f
     private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.2f
     private val ALPHA_ANIMATIONS_DURATION = 200L
     private var mIsTheTitleVisible = false
     private var mIsTheTitleContainerVisible = true
     var instructionId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
        binding.appBar.addOnOffsetChangedListener(this)
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
                 viewModel.isMyInstruction.value!! -> {
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
         binding.btnToolbarAction.apply {
             when {
                 viewModel.isMyInstruction.value!! -> {
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

     override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
         val maxScroll = binding.appBar.totalScrollRange
         val percentage = Math.abs(p1).toFloat() / maxScroll.toFloat()
         handleAlphaOnTitle(percentage)
         handleToolbarTitleVisibility(percentage)
     }

     private fun handleToolbarTitleVisibility(percentage: Float) {
         if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

             if (!mIsTheTitleVisible) {

                 startAlphaAnimation(binding.tvTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                 mIsTheTitleVisible = true
                 binding.tvTitle.text = binding.tvName.text
                 binding.btnToolbarAction.visibility = View.INVISIBLE
             }
         } else {
             if (mIsTheTitleVisible) {
                 startAlphaAnimation(binding.tvTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                 mIsTheTitleVisible = false
                 binding.tvTitle.text = ""
                 binding.btnToolbarAction.visibility = View.VISIBLE
             }
         }
     }

     private fun handleAlphaOnTitle(percentage: Float) {
         if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
             if (mIsTheTitleContainerVisible) {
                 binding.fActionButton.visibility = View.INVISIBLE
                 startAlphaAnimation(
                     binding.btnToolbarAction,
                     ALPHA_ANIMATIONS_DURATION,
                     View.VISIBLE
                 )
                 mIsTheTitleContainerVisible = false
             }
         } else {
             if (!mIsTheTitleContainerVisible) {
                 startAlphaAnimation(
                     binding.btnToolbarAction,
                     ALPHA_ANIMATIONS_DURATION, View.INVISIBLE
                 )
                 binding.fActionButton.visibility = View.VISIBLE
                 mIsTheTitleContainerVisible = true
             }
         }
     }

     private fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
         val alphaAnimation = if (visibility == View.VISIBLE)
             AlphaAnimation(0f, 1f)
         else
             AlphaAnimation(1f, 0f)

         alphaAnimation.duration = duration
         alphaAnimation.fillAfter = true
         v.startAnimation(alphaAnimation)
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
