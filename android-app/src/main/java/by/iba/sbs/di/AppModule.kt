package by.iba.sbs.di

import android.preference.PreferenceManager
import by.iba.sbs.library.service.SystemInformation
import by.iba.sbs.library.viewmodel.ProfileViewModel
import by.iba.sbs.tools.SystemInfo
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.guideline.GuidelineFragment
import by.iba.sbs.ui.login.LoginViewModel
import by.iba.sbs.ui.login.ResetViewModel
import by.iba.sbs.ui.login.SplashViewModel
import by.iba.sbs.ui.walkthrough.WalkthroughViewModel
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    single<SystemInformation> { SystemInfo(androidContext()) }
    single<Settings> { AndroidSettings(PreferenceManager.getDefaultSharedPreferences(androidContext())) }
    viewModel { MainViewModel() }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(androidContext(), get()) }
    viewModel { ResetViewModel() }
    // viewModel { GuidelineViewModel(androidContext()) }
    viewModel { WalkthroughViewModel() }
    //viewModel { PostDetailsViewModel(userPostUseCase = get(), commentsUseCase = get()) }
    viewModel { ProfileViewModel(get(), eventsDispatcherOnMain()) }
}
val serviceModule = module {

}
val fragmentModule = module {
    fragment { GuidelineFragment() }
}
