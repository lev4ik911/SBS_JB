package by.iba.sbs.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileInstructionListItemBinding
import by.iba.sbs.databinding.ProfileInstructionsFragmentBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

class ProfileGuidelinesFragment :
    MvvmFragment<ProfileInstructionsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.profile_instructions_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java
    private lateinit var instructionsAdapter: BaseBindingAdapter<Guideline, ProfileInstructionListItemBinding, ProfileViewModel>
    override fun viewModelStoreOwner(): ViewModelStoreOwner {
        return requireActivity()
    }

    override fun viewModelFactory(): ViewModelProvider.Factory =
        createViewModelFactory {
            requireActivity().let {
                ViewModelProvider(it).get(ProfileViewModel::class.java)
            }
        }

    @UnstableDefault
    @OptIn(ImplicitReflectionSerializer::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        @SuppressLint("ResourceType")
        instructionsAdapter =
            BaseBindingAdapter<Guideline, ProfileInstructionListItemBinding, ProfileViewModel>(
                R.layout.profile_instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                viewModel
                ,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
                .apply {
                    val settings = LocalSettings(
                        AndroidSettings(
                            PreferenceManager.getDefaultSharedPreferences(requireContext())
                        )
                    )
                    if (settings.userId == viewModel.user.value.id) {
                        emptyViewId = R.layout.new_item
                        onEmptyViewItemClick = {
                            val intent = Intent(activity, GuidelineActivity::class.java)
                            intent.putExtra("instructionId", 0)
                            startActivity(intent)
                        }
                    }
                }
        binding.rvProfileInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)
        }
        viewModel.guidelines.addObserver {
            instructionsAdapter.addItems(it)
        }
        viewModel.updatedGuidelineId.addObserver {
            instructionsAdapter.itemsList.indexOfFirst { guideline -> guideline.id == it }.apply {
                if (this != -1)
                    instructionsAdapter.notifyItemChanged(this)
            }
        }
    }

    @ImplicitReflectionSerializer
    @UnstableDefault
    override fun onStart() {
        super.onStart()
        viewModel.loadUserGuidelines(false)
    }
}



