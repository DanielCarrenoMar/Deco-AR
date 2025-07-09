package com.app.homear.ui.screens.configuracion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.usecase.firestore.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfigurationState(
    val isLoading: Boolean = false,
    val user: UserModel? = null,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {

    var state by mutableStateOf(ConfigurationState())
        private set

    init {
        loadUserSession()
    }

    fun loadUserSession() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val user = result.data
                        state = state.copy(
                            isLoading = false,
                            user = user,
                            isAuthenticated = user != null,
                            errorMessage = null
                        )
                        Log.d("ConfigurationViewModel", "User session loaded: ${user?.name}")
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            user = null,
                            isAuthenticated = false,
                            errorMessage = result.message
                        )
                        Log.e(
                            "ConfigurationViewModel",
                            "Error loading user session: ${result.message}"
                        )
                    }

                    is Resource.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun refreshSession() {
        loadUserSession()
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }
}