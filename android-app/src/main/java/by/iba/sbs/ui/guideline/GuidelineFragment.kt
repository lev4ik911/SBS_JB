package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionFragmentBinding
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import by.iba.sbs.tools.Tools.Companion.startAlphaAnimation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.android.synthetic.main.instruction_fragment.*
import kotlinx.serialization.UnstableDefault
import kotlin.math.abs

class GuidelineFragment :
    MvvmFragment<InstructionFragmentBinding, GuidelineViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    override val layoutId: Int = R.layout.instruction_fragment
    override val viewModelVariableId: Int = BR.viewmodel

    private lateinit var viewPager: ViewPager2
    private val percentageToShowTitleAtToolbar = 0.7f
    private val percentageToHideTitleDetails = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        requireActivity().let {
            ViewModelProvider(it).get(GuidelineViewModel::class.java)
        }
//        GuidelineViewModel(
//            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
//            eventsDispatcherOnMain()
//        )
    }

    @UnstableDefault
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
        binding.appBar.addOnOffsetChangedListener(this)

        binding.toolbarDescription.apply {
            title = viewModel.guideline.value.name
            setNavigationOnClickListener {
                activity?.finish()
            }
        }

        initActionButton()
        viewModel.isFavorite.addObserver {
            initActionButton()
        }
        viewPager = binding.viewPager
        viewPager.adapter = TabsFragmentAdapter(this)

        TabLayoutMediator(binding.tabsProfile, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_steps)
                1 -> getString(R.string.title_feedback)
                else -> ""
            }
        }.attach()

        btn_close_offline_mode?.setOnClickListener {
            viewModel.offlineMode.value = false
        }
    }

    private fun initActionButton() {
        binding.fActionButton.apply {
            when {
                viewModel.isMyInstruction.value -> {
                    this.setImageResource(R.drawable.file_document_edit_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                }
                viewModel.isFavorite.value -> {
                    this.setImageResource(R.drawable.star)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorGold))
                }
                else -> {
                    this.setImageResource(R.drawable.star_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorGold))
                }
            }
        }
        binding.btnToolbarAction.apply {
            when {
                viewModel.isMyInstruction.value -> {
                    this.setImageResource(R.drawable.file_document_edit_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                }
                viewModel.isFavorite.value -> {
                    this.setImageResource(R.drawable.star)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorGold))
                }
                else -> {
                    this.setImageResource(R.drawable.star_outline)
                    this.setColorFilter(ContextCompat.getColor(context, R.color.colorGold))
                }
            }
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appBar.totalScrollRange
        val percentage = abs(p1).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {

            if (!mIsTheTitleVisible) {

                startAlphaAnimation(binding.tvTitle, mAlphaAnimationsDuration, View.VISIBLE)
                mIsTheTitleVisible = true
                binding.tvTitle.text = binding.tvName.text
                binding.btnToolbarAction.visibility = View.INVISIBLE
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.tvTitle, mAlphaAnimationsDuration, View.INVISIBLE)
                mIsTheTitleVisible = false
                binding.tvTitle.clearComposingText()
                binding.btnToolbarAction.visibility = View.VISIBLE
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= percentageToHideTitleDetails) {
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

    class TabsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

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
