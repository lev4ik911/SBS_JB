package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.iba.mvvmbase.BaseEventsFragment
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.InstructionEditFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import by.iba.sbs.tools.Extentions
import com.google.android.material.appbar.AppBarLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class InstructionEditFragment :
    BaseFragment<InstructionEditFragmentBinding, InstructionViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    override val layoutId: Int = R.layout.instruction_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()
    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.7f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.7f
    private val mAlphaAnimationsDuration = 200L
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var instructionId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.addOnOffsetChangedListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (instructionId == 0)
                activity?.finish()
            else {
                viewModel.onBackBtnClick()
                findNavController().navigate(R.id.navigation_instruction_view)
            }
        }
        instructionId = arguments?.getInt("instructionId") ?: 0

        binding.rvSteps.apply {
            this.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(this)
        }
        viewModel.name.observe(viewLifecycleOwner, Observer {
            binding.toolbarDescription.title = it
        })
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            stepsAdapter.addItems(it)
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
        stepsAdapter.onEmptyViewItemClick = {
            // startActivity(Intent(activity, InstructionActivity::class.java))
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        val maxScroll = binding.appBar.totalScrollRange
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
                binding.tvTitle.text = binding.teName.text
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

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        EmptyViewAdapter<Step>(
            R.layout.instruction_edit_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info)?.text = item.description
                view.findViewById<ImageView>(R.id.iv_camera)?.setOnClickListener {
                    (activity as InstructionActivity).callImageSelector(item.stepId)
                }
                view.findViewById<ImageView>(R.id.iv_preview).apply {
                    if (item.imagePath.isNotEmpty()) {
                        val imageUri = Uri.fromFile(File(item.imagePath))
                        this.setImageURI(imageUri)
                    }
                    else
                        this.setImageResource(R.drawable.ic_paneer)
                    //TODO(Change ic_paneer on image by default)
                }
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }).also {
            it.emptyViewId = R.layout.new_step
            it.dragLayoutId = R.id.iv_drag
        }
}
