package by.iba.sbs.ui.profile

import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.iba.sbs.BR
import by.iba.sbs.databinding.ProfileEditFragmentBinding
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.tools.Extentions
import com.google.android.material.appbar.AppBarLayout
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.math.abs


class ProfileEditFragment :
    MvvmFragment<ProfileEditFragmentBinding, ProfileViewModel>(),
    AppBarLayout.OnOffsetChangedListener {
    override val layoutId: Int = by.iba.sbs.R.layout.profile_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        val viewModel: ProfileViewModel by sharedViewModel()
        return@createViewModelFactory viewModel
//        ProfileViewModel(
//            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
//            eventsDispatcherOnMain()
//        )
    }

    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.7f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appbar.totalScrollRange
        val percentage = abs(p1).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                Extentions.startAlphaAnimation(
                    binding.tvTitle,
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleVisible = true
                binding.tvTitle.text = binding.tvUserName.text
                binding.btnToolbarAction.visibility = View.INVISIBLE
            }
        } else {
            if (mIsTheTitleVisible) {
                Extentions.startAlphaAnimation(
                    binding.tvTitle,
                    mAlphaAnimationsDuration,
                    View.INVISIBLE
                )
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
                Extentions.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                Extentions.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration, View.INVISIBLE
                )
                binding.fActionButton.visibility = View.VISIBLE
                mIsTheTitleContainerVisible = true
            }
        }
    }
}
