package com.app.homear.ui.screens.profileProv
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.homear.domain.model.UserModel

// Estado que representará la UI de la pantalla de perfil del proveedor
data class ProfileProvedorUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: UserModel? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileProvViewModel @Inject constructor(
    // Aquí se inyectarían dependencias como repositorios de usuario o servicios de autenticación.
    // Por ahora, lo dejamos vacío ya que simulamos los datos.
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileProvedorUiState())
    val uiState: StateFlow<ProfileProvedorUiState> = _uiState

    init {
        // Llamamos a cargar el perfil cuando se inicializa el ViewModel
        // loadProvedorProfile() // Comentado porque ahora recibe un parámetro
    }

    fun loadProvedorProfile(user: UserModel) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isAuthenticated = true,
            user = user
        )
    }

    fun logout() {
        viewModelScope.launch {
            // Simular un proceso de cierre de sesión
            delay(500)
            _uiState.value = ProfileProvedorUiState(isAuthenticated = false, user = null)
        }
    }

    fun refreshUser(user: UserModel) {
        loadProvedorProfile(user)
    }

    // Funciones adicionales para manejar lógica específica del proveedor,
    // como cargar listas de muebles o espacios subidos.
    // fun loadUploadedFurniture() { ... }
    // fun loadUploadedSpaces() { ... }
}
