package by.iba.sbs.ui.guideline

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.ViewModelProvider
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.StepEditFragmentBinding
import by.iba.sbs.library.model.Step
import by.iba.sbs.library.viewmodel.GuidelineViewModel
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain


class StepEditFragment : MvvmFragment<StepEditFragmentBinding, GuidelineViewModel>() {

    override val layoutId: Int = R.layout.step_edit_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    private var stepWeight = 0
    override val viewModelClass: Class<GuidelineViewModel> =
        GuidelineViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        GuidelineViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepWeight = arguments?.getInt("stepWeight") ?: 0
        if (stepWeight > 0)
            binding.step = viewModel.steps.value!!.find { step -> step.weight == stepWeight }
        else {
            if (viewModel.steps.value.isNullOrEmpty())
                viewModel.steps.value = listOf()
            binding.step = Step(weight = viewModel.steps.value!!.size.plus(1))
        }
    }

}
