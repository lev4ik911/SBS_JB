package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseAdapter
import by.iba.mvvmbase.visibleOrGone
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepsFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File


class StepsFragment : BaseFragment<StepsFragmentBinding, InstructionViewModel>() {
    override val layoutId: Int = R.layout.steps_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSteps.apply {
            adapter = stepsAdapter
        }
        viewModel.steps.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            stepsAdapter.addItems(it)
        })
        stepsAdapter.onItemClick = { pos, itemView, item ->
            Toast.makeText(context, pos.toString(), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("ResourceType")
    private val stepsAdapter =
        BaseAdapter<Step>(
            R.layout.instruction_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<ImageView>(R.id.iv_preview)?.apply {
                    if (item.imagePath.isNotEmpty()) {
                        val imageUri = Uri.fromFile(File(item.imagePath))
                        this.setImageURI(imageUri)
                    } else
                        this.setImageResource(R.drawable.ic_paneer)
                    //TODO(Change ic_paneer on image by default)
                }
                view.findViewById<TextView>(R.id.tv_step_number)?.text = item.stepId.toString()
                view.findViewById<TextView>(R.id.tv_title)?.text = item.name
                view.findViewById<TextView>(R.id.tv_description)?.apply {
                    visibleOrGone(item.description.isNotEmpty())
                    text = item.description
                }
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }
        )
}
