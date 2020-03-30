package by.iba.mvvmbase

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.shashank.sony.fancytoastlib.FancyToast

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var binding: DB
    protected abstract val viewModel: VM
    protected abstract val layoutId: Int
    protected abstract val viewModelVariableId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(viewModelVariableId, viewModel)
        viewModel.notificationsQueue.observe(this, Observer {
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
                this,
                it.message,
                FancyToast.LENGTH_LONG,
                it.type.index,
                false
            ).show()
        })
    }
}