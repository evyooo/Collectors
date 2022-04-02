package com.yoo.collectors.injection

import com.yoo.collectors.EditRemoteDataSource
import com.yoo.collectors.model.EditRepository
import com.yoo.collectors.viewmodel.EditViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { EditRemoteDataSource() }
    single { EditRepository(get()) }
    viewModel { EditViewModel(get()) }
}