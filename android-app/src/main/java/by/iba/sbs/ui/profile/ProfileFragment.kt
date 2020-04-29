package by.iba.sbs.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileFragmentBinding
import by.iba.sbs.tools.Extentions.Companion.startAlphaAnimation
import by.iba.sbs.ui.MainActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class ProfileFragment :
    BaseEventsFragment<ProfileFragmentBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener, AppBarLayout.OnOffsetChangedListener {

    override val layoutId: Int = R.layout.profile_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by viewModel()
    private lateinit var viewPager: ViewPager2
    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.7f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionButton()
        viewModel.isFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            initActionButton()
        })
        when (activity) {
            is MainActivity -> {
                binding.toolbar.navigationIcon = null
            }
            is ProfileActivity -> {
                binding.toolbar.navigationIcon = resources.getDrawable(R.drawable.chevron_left)
            }
        }
        binding.appbar.addOnOffsetChangedListener(this)
        viewPager = binding.viewPager
        viewPager.adapter = TabsFragmentAdapter(this)

        TabLayoutMediator(binding.tabsProfile, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_instructions)
                1 -> getString(R.string.title_subscribers)
                else -> ""
            }
        }.attach()
    }

    private fun initActionButton() {
        binding.fActionButton.apply {
            when {
                viewModel.isMyProfile.value!! -> {
                    this.setImageResource(R.drawable.file_document_edit_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                }
                viewModel.isFavorite.value!! -> {
                    this.setImageResource(R.drawable.heart)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorLightRed))
                }
                else -> {
                    this.setImageResource(R.drawable.heart_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorLightRed))
                }
            }
        }
        binding.btnToolbarAction.apply {
            when {
                viewModel.isMyProfile.value!! -> {
                    this.setImageResource(R.drawable.file_document_edit_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                }
                viewModel.isFavorite.value!! -> {
                    this.setImageResource(R.drawable.heart)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorLightRed))
                }
                else -> {
                    this.setImageResource(R.drawable.heart_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorLightRed))
                }
            }
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appbar.totalScrollRange
        val percentage = abs(p1).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {

                startAlphaAnimation(binding.tvTitle, mAlphaAnimationsDuration, View.VISIBLE)
                mIsTheTitleVisible = true
                binding.tvTitle.text = binding.tvUserName.text
                binding.btnToolbarAction.visibility = View.INVISIBLE
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.tvTitle, mAlphaAnimationsDuration, View.INVISIBLE)
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
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration, View.INVISIBLE
                )
                binding.fActionButton.visibility = View.VISIBLE
                mIsTheTitleContainerVisible = true
            }
        }
    }

    inner class TabsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ProfileInstructionsFragment()
                1 -> SubscribersFragment()
                else -> ProfileInstructionsFragment()
            }
        }
    }
}
