package by.iba.sbs.ui.profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileEditFragmentBinding
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.tools.Tools
import com.google.android.material.appbar.AppBarLayout
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlin.math.abs


class ProfileEditFragment :
    MvvmFragment<ProfileEditFragmentBinding, ProfileViewModel>(),
    AppBarLayout.OnOffsetChangedListener {
    override val layoutId: Int = by.iba.sbs.R.layout.profile_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelStoreOwner(): ViewModelStoreOwner {
        return requireActivity()
    }

    override fun viewModelFactory(): ViewModelProvider.Factory =
        createViewModelFactory {
            requireActivity().let {
                ViewModelProvider(it).get(ProfileViewModel::class.java)
            }
        }

    private val percentageToShowTitleAtToolbar = 0.7f
    private val percentageToHideTitleDetails = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.fActionButton.setOnClickListener {
            viewModel.onSaveProfileButtonClick()
            findNavController().navigate(R.id.action_navigation_profile_edit_fragment_to_navigation_profile_fragment)
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appbar.totalScrollRange
        val percentage = abs(p1).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)

    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!mIsTheTitleVisible) {
                Tools.startAlphaAnimation(
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
                Tools.startAlphaAnimation(
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
        if (percentage >= percentageToHideTitleDetails) {
            if (mIsTheTitleContainerVisible) {
                binding.fActionButton.visibility = View.INVISIBLE
                Tools.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration,
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                Tools.startAlphaAnimation(
                    binding.btnToolbarAction,
                    mAlphaAnimationsDuration, View.INVISIBLE
                )
                binding.fActionButton.visibility = View.VISIBLE
                mIsTheTitleContainerVisible = true
            }
        }
    }
}
