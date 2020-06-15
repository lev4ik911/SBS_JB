package by.iba.sbs.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.ProfileInstructionsFragmentBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.ui.guideline.GuidelineActivity
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain

class ProfileInstructionsFragment :
    MvvmFragment<ProfileInstructionsFragmentBinding, ProfileViewModel>() {
    override val layoutId: Int = R.layout.profile_instructions_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<ProfileViewModel> =
        ProfileViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        ProfileViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvInstructions.apply {
            adapter = instructionsAdapter
        }
        viewModel.instructions.addObserver {
            instructionsAdapter.addItems(it)
        }
        instructionsAdapter.onItemClick = { pos, itemView, item ->
            val transitionSharedNameImgView = this.getString(R.string.transition_name_img_view)
            val transitionSharedNameTxtView = this.getString(R.string.transition_name_txt_view)
            var imageViewPair: Pair<View, String>
            val textViewPair: Pair<View, String>
            itemView.findViewById<ImageView>(R.id.iv_preview).apply {
                this.transitionName = transitionSharedNameImgView
                imageViewPair = Pair.create(this, transitionSharedNameImgView)
            }
            itemView.findViewById<TextView>(R.id.tv_title).apply {
                this.transitionName = transitionSharedNameTxtView
                textViewPair = Pair.create(this, transitionSharedNameTxtView)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity as Activity,
                imageViewPair,
                textViewPair
            )
            val intent = Intent(activity, GuidelineActivity::class.java)
            intent.putExtra("instructionId", 12)
            startActivity(intent, options.toBundle())
        }
        instructionsAdapter.onEmptyViewItemClick = {
            val intent = Intent(activity, GuidelineActivity::class.java)
            intent.putExtra("instructionId", 0)
            // findNavController().navigate(R.id.navigation_instruction_edit, bundle)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceType")
    private val instructionsAdapter =
        EmptyViewAdapter<Guideline>(
            R.layout.profile_instruction_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_title)?.text = item.name
                view.findViewById<TextView>(R.id.tv_info)?.text = item.author
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }
        ).also {
            it.emptyViewId = R.layout.new_item
        }
}
