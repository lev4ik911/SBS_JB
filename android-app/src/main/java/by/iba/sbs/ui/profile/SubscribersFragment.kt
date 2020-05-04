package by.iba.sbs.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.SubscribersFragmentBinding
import by.iba.sbs.library.model.Author
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SubscribersFragment : BaseFragment<SubscribersFragmentBinding, ProfileViewModel>() {

    override val layoutId: Int = R.layout.subscribers_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: ProfileViewModel by sharedViewModel()
    private lateinit var subscribersStr: String
    private lateinit var instructionsStr: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribersStr = resources.getString(R.string.text_subscribers)
        instructionsStr = resources.getString(R.string.text_instructions)

        binding.rvSubscribers.apply {
            adapter = subscribersAdapter
        }
        viewModel.subscribers.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            subscribersAdapter.addItems(it)
        })
    }

    @SuppressLint("ResourceType")
    private val subscribersAdapter =
        BaseAdapter<Author>(
            R.layout.profile_subscriber_list_item,
            onBind = { view, item, _ ->
                view.findViewById<TextView>(R.id.tv_author_name)?.text = item.name
                view.findViewById<TextView>(R.id.tv_subscribers_count)?.text =
                    String.format(subscribersStr, item.subscribersCount)
                view.findViewById<TextView>(R.id.tv_instructions_count)?.text =
                    String.format(instructionsStr, item.instructionsCount)
            },
            isItemsEquals = { oldItem, newItem ->
                oldItem.name == newItem.name
            }
        )
}

