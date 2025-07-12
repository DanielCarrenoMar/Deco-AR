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
import com.app.homear.domain.usecase.auth.SingOutUseCase
import com.app.homear.domain.usecase.auth.IsLoggedInUseCase
import com.app.homear.domain.usecase.proyect.DeleteAllProyectUseCase
import com.app.homear.domain.usecase.space.DeleteAllSpacesUseCase
import com.app.homear.domain.usecase.spaceFurniture.DeleteAllSpaceFurnituresUseCase
import com.app.homear.domain.usecase.spaceFurniture.SaveSpaceFurnitureUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfigurationState(
    val isLoading: Boolean = false,
    val user: UserModel? = null,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val userType: String? = null
)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val singOutUseCase: SingOutUseCase,
    private val deleteAllProjectsUseCase: DeleteAllProyectUseCase,
    private val deleteAllSpacesUseCase: DeleteAllSpacesUseCase,
    private val deleteAllSpacesFurnitureUseCase: DeleteAllSpaceFurnituresUseCase
): ViewModel() {

    var state by mutableStateOf(ConfigurationState())
        private set

    init {
        loadUserSession()
    }

    fun deleteAllProjectandSpaces(){
        viewModelScope.launch {
            deleteAllProjectsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ConfigurationViewModel", "Se borraron ${result.data!!} proyectos")
                    }
                    is Resource.Error -> {
                        Log.e("ConfigurationViewModel", "Error deleting projects: ${result.message}")
                    }
                    is Resource.Loading -> {
                        // Puedes manejar el estado de carga si lo deseas
                    }
                }
            }
            deleteAllSpacesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ConfigurationViewModel", "Se borraron ${result.data!!} espacios")
                    }
                    is Resource.Error -> {
                        Log.e("ConfigurationViewModel", "Error deleting spaces: ${result.message}")
                    }
                    is Resource.Loading -> {
                        // Puedes manejar el estado de carga si lo deseas
                    }
                }
            }
            deleteAllSpacesFurnitureUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ConfigurationViewModel", "Se borraron ${result.data!!} muebles de espacios")
                    }
                    is Resource.Error -> {
                        Log.e("ConfigurationViewModel", "Error deleting space furniture: ${result.message}")
                    }
                    is Resource.Loading -> {
                        // Puedes manejar el estado de carga si lo deseas
                    }
                }
            }
        }
    }

    fun loadUserSession() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            val isLogged = isLoggedInUseCase() // usamos tu nuevo caso de uso
            Log.d("ConfigVM", "¿Está logueado? " + isLogged);

            if (isLogged) {
                getCurrentUserUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val user = result.data
                            state = state.copy(
                                isLoading = false,
                                user = user,
                                isAuthenticated = true,
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
            } else {
                // Si no está logueado, actualiza el estado acorde
                state = state.copy(
                    isLoading = false,
                    user = null,
                    isAuthenticated = false,
                    errorMessage = null
                )
            }
        }
    }

    var userType by mutableStateOf<String?>(null)
        private set


    fun refreshSession() {
        loadUserSession()
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }

    fun signOut(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            singOutUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Actualiza estado y navega fuera
                        state = state.copy(isAuthenticated = false, user = null)
                        onResult(true)
                    }
                    is Resource.Error -> {
                        // Muestra mensaje de error si quieres
                        state = state.copy(errorMessage = result.message ?: "Hubo un error al cerrar sesión")
                        onResult(false)
                    }
                    is Resource.Loading -> { /* Puedes mostrar loading si gustas */ }
                }
            }
        }
    }
}