package by.iba.sbs.ui.instruction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.EmptyViewAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepsFragmentBinding
import by.iba.sbs.library.model.Step
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
internal const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [StepsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StepsFragment : BaseFragment<StepsFragmentBinding, InstructionViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    override val layoutId: Int = R.layout.steps_fragment

    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: InstructionViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.rv_steps).also {
            it.adapter = stepsAdapter
            stepsAdapter.itemTouchHelper.attachToRecyclerView(it)
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
        EmptyViewAdapter<Step>(
            R.layout.instruction_step_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_info).text = item.description
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.description == newItem.description
            }).also {
        }
}
