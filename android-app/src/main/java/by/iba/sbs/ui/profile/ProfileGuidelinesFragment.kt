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
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.databinding.ProfileInstructionsFragmentBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileGuidelinesFragment :
    MvvmFragment<ProfileInstructionsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.profile_instructions_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java
    private lateinit var instructionsAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>
    override fun viewModelStoreOwner(): ViewModelStoreOwner {
        return requireActivity()
    }

    override fun viewModelFactory(): ViewModelProvider.Factory =
        createViewModelFactory {
//            ProfileViewModel(
//                AndroidSettings(PreferenceManager.getDefaultSharedPreferences(requireContext())),
//                eventsDispatcherOnMain()
//            )
            requireActivity().let {
                ViewModelProvider(it).get(ProfileViewModel::class.java)
            }
        }

    private val mainViewModel: MainViewModel by viewModel()
    @UnstableDefault
    @OptIn(ImplicitReflectionSerializer::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        @SuppressLint("ResourceType")
        instructionsAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>(
                R.layout.instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                mainViewModel
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
//                    onItemClick = { i: Int, view: View?, guideline: Guideline ->
//                        val transitionSharedNameImgView = requireActivity().getString(R.string.transition_name_img_view)
//                        val transitionSharedNameTxtView = requireActivity().getString(R.string.transition_name_txt_view)
//                        var imageViewPair: Pair<View, String>
//                        val textViewPair: Pair<View, String>
//                        view?.findViewById<ImageView>(R.id.iv_preview).apply {
//                            this?.transitionName = transitionSharedNameImgView
//                            imageViewPair = Pair.create(this, transitionSharedNameImgView)
//                        }
//                        view?.findViewById<TextView>(R.id.tv_title).apply {
//                            this?.transitionName = transitionSharedNameTxtView
//                            textViewPair = Pair.create(this, transitionSharedNameTxtView)
//                        }
//                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            requireActivity(),
//                            imageViewPair,
//                            textViewPair
//                        )
//                        val intent = Intent(requireActivity(), GuidelineActivity::class.java)
//                        intent.putExtra("instructionId", guideline.id)
//                        startActivity(intent, options.toBundle())
//                    }
                }
        binding.rvInstructions.apply {
            adapter = instructionsAdapter
        }
        binding.rvInstructions.also {
            it.adapter = instructionsAdapter
            instructionsAdapter.itemTouchHelper.attachToRecyclerView(it)

        }
        viewModel.guidelines.addObserver {
            instructionsAdapter.addItems(it)
        }
        viewModel.loadUserGuidelines(false)//TODO: refresh implementation needed
    }
}



