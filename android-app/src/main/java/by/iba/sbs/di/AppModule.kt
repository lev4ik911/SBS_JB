package by.iba.sbs.di

import by.iba.sbs.ui.login.LoginViewModel
import by.iba.sbs.ui.login.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel() }
    viewModel { LoginViewModel() }

    //viewModel { PostDetailsViewModel(userPostUseCase = get(), commentsUseCase = get()) }
}