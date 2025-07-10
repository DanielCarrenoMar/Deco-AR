package com.app.homear.ui.screens.intro

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.usecase.auth.IsLoggedInUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val isLoggedInUseCase: IsLoggedInUseCase
): ViewModel() {
    suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        isLoggedInUseCase()
    }
}