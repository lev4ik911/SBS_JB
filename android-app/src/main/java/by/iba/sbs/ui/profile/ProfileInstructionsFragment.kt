package by.iba.sbs.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.databinding.ProfileInstructionsFragmentBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileInstructionsFragment :
    MvvmFragment<ProfileInstructionsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.profile_instructions_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java
    private lateinit var instructionsAdapter: BaseBindingAdapter<Guideline, InstructionListItemBinding, MainViewModel>
    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        val viewModel: ProfileViewModel by sharedViewModel()
        return@createViewModelFactory viewModel
    }

    private val mainViewModel: MainViewModel by sharedViewModel()

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
                mainViewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                }).also {

            }
        binding.rvInstructions.apply {
            adapter = instructionsAdapter
        }
        viewModel.loadInstructions(true)//TODO: refresh implementation needed
        viewModel.instructions.addObserver {
            instructionsAdapter.addItems(it)
        }
//        instructionsAdapter.onItemClick = { pos, itemView, item ->
//            val transitionSharedNameImgView = this.getString(R.string.transition_name_img_view)
//            val transitionSharedNameTxtView = this.getString(R.string.transition_name_txt_view)
//            var imageViewPair: Pair<View, String>
//            val textViewPair: Pair<View, String>
//            itemView.findViewById<ImageView>(R.id.iv_preview).apply {
//                this.transitionName = transitionSharedNameImgView
//                imageViewPair = Pair.create(this, transitionSharedNameImgView)
//            }
//            itemView.findViewById<TextView>(R.id.tv_title).apply {
//                this.transitionName = transitionSharedNameTxtView
//                textViewPair = Pair.create(this, transitionSharedNameTxtView)
//            }
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                activity as Activity,
//                imageViewPair,
//                textViewPair
//            )
//            val intent = Intent(activity, GuidelineActivity::class.java)
//            intent.putExtra("instructionId", 12)
//            startActivity(intent, options.toBundle())
//        }
        instructionsAdapter.onEmptyViewItemClick = {
            val intent = Intent(activity, GuidelineActivity::class.java)
            intent.putExtra("instructionId", 0)
            // findNavController().navigate(R.id.navigation_instruction_edit, bundle)
            startActivity(intent)
        }
    }


}
