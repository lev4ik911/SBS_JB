package by.iba.mvvmbase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.shashank.sony.fancytoastlib.FancyToast

abstract class BaseFragment<DB : androidx.databinding.ViewDataBinding, VM : BaseViewModel> : Fragment() {

    private lateinit var binding: DB
    protected abstract val layoutId: Int
    protected abstract val viewModelVariableId: Int
    protected abstract val viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         binding = DataBindingUtil.inflate(
            inflater, layoutId, container, false
        )
        binding.lifecycleOwner = this
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(viewModelVariableId, viewModel)
        viewModel.notificationsQueue.observe(viewLifecycleOwner, Observer {
            when(it.type){
                MessageType.ERROR ->
                    Log.e(viewModel::class.java.name, it.getLogMessage())
                MessageType.WARNING ->
                    Log.w(viewModel::class.java.name, it.getLogMessage())
                MessageType.INFO ->
                    Log.i(viewModel::class.java.name, it.getLogMessage())
                else ->
                    Log.v(viewModel::class.java.name, it.getLogMessage())
            }
            FancyToast.makeText(
                context,
                it.message,
                FancyToast.LENGTH_LONG,
                it.type.index,
                false
            ).show()
        })
    }
}