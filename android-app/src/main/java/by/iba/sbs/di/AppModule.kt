package by.iba.sbs.di

import by.iba.sbs.library.service.SystemInformation
import by.iba.sbs.tools.SystemInfo
import by.iba.sbs.ui.MainViewModel
import by.iba.sbs.ui.dashboard.DashboardViewModel
import by.iba.sbs.ui.guideline.GuidelineFragment
import by.iba.sbs.ui.guideline.GuidelineViewModel
import by.iba.sbs.ui.guidelines.GuidelineListViewModel
import by.iba.sbs.ui.login.LoginViewModel
import by.iba.sbs.ui.login.RegisterViewModel
import by.iba.sbs.ui.login.ResetViewModel
import by.iba.sbs.ui.login.SplashViewModel
import by.iba.sbs.ui.profile.ProfileViewModel
import by.iba.sbs.ui.walkthrough.WalkthroughViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { DashboardViewModel(androidContext()) }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(androidContext(), get()) }
    viewModel { RegisterViewModel() }
    viewModel { ResetViewModel() }
    viewModel { ProfileViewModel(androidContext()) }
    viewModel { GuidelineViewModel(androidContext()) }
    viewModel { GuidelineListViewModel(androidContext()) }
    viewModel { WalkthroughViewModel() }
    //viewModel { PostDetailsViewModel(userPostUseCase = get(), commentsUseCase = get()) }
}
val serviceModule = module {
    single<SystemInformation> { SystemInfo(androidContext()) }
}
val fragmentModule = module {
    fragment { GuidelineFragment() }
}
