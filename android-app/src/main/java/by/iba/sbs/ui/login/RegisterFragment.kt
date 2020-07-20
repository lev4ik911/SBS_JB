package by.iba.sbs.ui.login

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.LoginRegisterFragmentBinding
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.viewmodel.RegisterViewModel
import by.iba.sbs.library.viewmodel.ValidationErrors
import by.iba.sbs.tools.SystemInfo
import by.iba.sbs.tools.Tools
import com.russhwolf.settings.AndroidSettings
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlinx.android.synthetic.main.login_register_fragment.*


class RegisterFragment :
    MvvmEventsFragment<LoginRegisterFragmentBinding, RegisterViewModel, RegisterViewModel.EventsListener>(),
    RegisterViewModel.EventsListener {
    override val layoutId: Int = R.layout.login_register_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModelClass: Class<RegisterViewModel> = RegisterViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        RegisterViewModel(
            AndroidSettings(PreferenceManager.getDefaultSharedPreferences(context)),
            eventsDispatcherOnMain(),
            SystemInfo(requireContext())
        )
    }

    override fun routeToLoginScreen() {
        findNavController().navigate(R.id.action_navigation_register_to_navigation_login_fragment)
    }

    override fun showToast(msg: ToastMessage) {
        Tools.showToast(requireContext(), viewModelClass.name, msg)
    }

    override fun showErrors(errorList: List<ValidationErrors>) {
        et_login_layout.error = null
        et_email_layout.error = null
        et_new_password_layout.error = null
        et_confirm_password_layout.error = null

        errorList.forEach {
            when (it) {
                ValidationErrors.LOGIN_IS_EMPTY -> et_login_layout.error =
                    resources.getString(R.string.error_login_is_empty)
                ValidationErrors.INVALID_EMAIL -> et_email_layout.error =
                    resources.getString(R.string.error_invalid_email)
                ValidationErrors.PASSWORD_IS_TOO_SMALL -> et_new_password_layout.error =
                    resources.getString(R.string.error_password_is_too_small)
                ValidationErrors.PASSWORD_HAS_INCORRECT_SYMBOLS -> et_new_password_layout.error =
                    resources.getString(R.string.error_password_has_incorrect_symbols)
                ValidationErrors.PASSWORD_MISMATCH -> et_confirm_password_layout.error =
                    resources.getString(R.string.error_password_mismatch)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_login.doOnTextChanged { _, _, _, _ ->
            et_login_layout.error = null
        }
        et_email.doOnTextChanged { _, _, _, _ ->
            et_email_layout.error = null
        }
        et_new_password.doOnTextChanged { _, _, _, _ ->
            et_new_password_layout.error = null
        }
        et_confirm_password.doOnTextChanged { _, _, _, _ ->
            et_confirm_password_layout.error = null
        }
    }

//    override fun showErrors() {
//        et_login_layout.error = null
//        et_email_layout.error = null
//        et_new_password_layout.error = null
//        et_confirm_password_layout.error = null
//
//        viewModel.errorList.forEach{
//            when (it) {
//                ValidationErrors.LOGIN_IS_EMPTY -> et_login_layout.error = resources.getString(R.string.error_login_is_empty)
//                ValidationErrors.INVALID_EMAIL -> et_email_layout.error = resources.getString(R.string.error_invalid_email)
//                ValidationErrors.PASSWORD_IS_TOO_SMALL -> et_new_password_layout.error = resources.getString(R.string.error_password_is_too_small)
//                ValidationErrors.PASSWORD_HAS_INCORRECT_SYMBOLS -> et_new_password_layout.error = resources.getString(R.string.error_password_has_incorrect_symbols)
//                ValidationErrors.PASSWORD_MISMATCH -> et_confirm_password_layout.error = resources.getString(R.string.error_password_mismatch)
//            }
//        }
//    }
}
