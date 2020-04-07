package by.iba.sbs.di

import by.iba.ecl.ui.MainViewModel
import by.iba.sbs.library.service.SystemInformation
import by.iba.sbs.service.SystemInfo
import by.iba.sbs.ui.login.LoginViewModel
import by.iba.sbs.ui.login.RegisterViewModel
import by.iba.sbs.ui.login.ResetViewModel
import by.iba.sbs.ui.login.SplashViewModel
import by.iba.sbs.ui.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(androidContext(), get()) }
    viewModel { RegisterViewModel() }
    viewModel { ResetViewModel() }
    viewModel { ProfileViewModel() }
    //viewModel { PostDetailsViewModel(userPostUseCase = get(), commentsUseCase = get()) }
}
val serviceModule = module {
    single<SystemInformation> { SystemInfo(androidContext()) }
}
