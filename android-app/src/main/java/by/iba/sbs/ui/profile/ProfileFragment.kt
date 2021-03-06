package by.iba.sbs.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileFragmentBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.tools.DownloadManager
import by.iba.sbs.tools.Tools
import by.iba.sbs.tools.Tools.Companion.startAlphaAnimation
import by.iba.sbs.tools.visibleOrGone
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlin.math.abs

class ProfileFragment :
    MvvmEventsFragment<ProfileFragmentBinding, ProfileViewModel, ProfileViewModel.EventsListener>(),
    ProfileViewModel.EventsListener,
    AppBarLayout.OnOffsetChangedListener {

    override val layoutId: Int = R.layout.profile_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelStoreOwner(): ViewModelStoreOwner {
        return requireActivity()
    }

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        ProfileViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(requireContext())),
            eventsDispatcherOnMain()
        )
    }

    private lateinit var viewPager: ViewPager2
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
        viewModel.user.addObserver {
            if (isAdded) {
                viewPager = binding.viewPager
                viewPager.adapter = TabsFragmentAdapter(this)
                TabLayoutMediator(binding.tabsProfile, viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> if (viewModel.isMyProfile.value) getString(R.string.title_settings) else getString(R.string.title_instructions)
                        1 -> if (viewModel.isMyProfile.value) getString(R.string.title_instructions) else getString(R.string.title_subscribers)
                        2 -> getString(R.string.title_subscribers)
                        else -> ""
                    }
                }.attach()
            }
        }
        viewModel.isFavorite.addObserver {
            initActionButton()
        }
        val userId = arguments?.getString("userId").orEmpty()
        viewModel.loadUser(userId, false)
        when (activity) {
            is MainActivity -> {
                binding.toolbar.navigationIcon = null
            }
            else -> {
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.chevron_left)
            }
        }
        binding.appbar.addOnOffsetChangedListener(this)
    }

    private fun initActionButton() {
        binding.fActionButton.apply {
            when {
                viewModel.isMyProfile.value -> {
                    this.setImageResource(R.drawable.account_edit_outline)
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
                viewModel.isMyProfile.value -> {
                    this.setImageResource(R.drawable.account_edit_outline)
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
        binding.btnToolbarLogout.visibleOrGone(viewModel.isMyProfile.value)
    }

    override fun onResume() {
        super.onResume()
        initActionButton()
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

    @ImplicitReflectionSerializer
    @UnstableDefault
    inner class TabsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = if (viewModel.isMyProfile.value) 3 else 2


        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> if (viewModel.isMyProfile.value) SettingsFragment() else ProfileGuidelinesFragment()
                    .also {
                        it.arguments?.putString("userId", viewModel.user.value.id)
                    }
                1 -> if (viewModel.isMyProfile.value) ProfileGuidelinesFragment()
                    .also {
                        it.arguments?.putString("userId", viewModel.user.value.id)
                    }
                else SubscribersFragment()
                2 -> SubscribersFragment()
                else -> ProfileGuidelinesFragment()
            }
        }
    }

    override fun onActionButtonAction() {
        when {
            viewModel.isMyProfile.value -> {
                findNavController().navigate(R.id.navigation_profile_edit_fragment)
            }
            else -> {
                viewModel.isFavorite.value = viewModel.isFavorite.value.not()
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onLogoutAction() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.title_logout_dialog))
            setMessage(
                resources.getString(
                    R.string.msg_logout_dialog
                )
            )
            setPositiveButton(
                resources.getString(R.string.btn_logout)
            ) { _: DialogInterface, _: Int ->
                viewModel.logout()
                (requireActivity() as MainActivity).setVisibilityForFavorites()
            }
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
            .create()
            .show()
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onSaveProfileAction() {
        findNavController().navigate(R.id.action_navigation_profile_edit_fragment_to_navigation_profile_fragment)
    }

    override fun routeToLoginScreen() {
        findNavController().navigate(R.id.action_navigation_profile_to_navigation_login_fragment)
        // findNavController().navigate()
    }

    override fun routeToProfileScreen() {
        findNavController().navigate(R.id.action_navigation_profile_edit_fragment_to_navigation_profile_fragment)
    }

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModelClass.name, msg)
    }

    override fun requireAccept() {
        val builder = AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.title_clear_history_dialog))
            setMessage(
                resources.getString(R.string.msg_clear_history_dialog)
            )
            setPositiveButton(
                resources.getString(R.string.btn_clear)
            ) { _: DialogInterface, _: Int ->
                viewModel.onAcceptClearHistory()
            }
            setNegativeButton(resources.getString(R.string.btn_cancel), null)
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onOpenGuidelineAction(guideline: Guideline) {
        val intent = Intent(requireContext(), GuidelineActivity::class.java)
        intent.putExtra("instructionId", guideline.id)
        startActivity(intent)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun loadImage(url: String, guideline: Guideline) {
        DownloadManager(requireContext()).apply {
            this.downloadImage(url,
                guideline.id,
                remoteImageId = guideline.remoteImageId,
                item = guideline,
                imageHandler =  imageHandler)
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val imageHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val guideline = msg.obj as Guideline
            viewModel.updatedGuidelineId.value = guideline.id
            viewModel.saveGuidelinePreviewImageInLocalDB(guideline)
            super.handleMessage(msg)
        }
    }
}
