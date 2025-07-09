package com.app.homear.ui.screens.editProfile

import android.app.Application // Importar Application
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel // Cambiar a AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import androidx.core.content.FileProvider // Importar FileProvider

// Cambiar de ViewModel a AndroidViewModel para tener acceso al contexto de la aplicación
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    application: Application // Inyectar el contexto de la aplicación
) : AndroidViewModel(application) { // Pasar la aplicación al constructor de AndroidViewModel

    private val _name = MutableStateFlow(TextFieldValue(""))
    val name: StateFlow<TextFieldValue> = _name.asStateFlow()

    private val _email = MutableStateFlow(TextFieldValue(""))
    val email: StateFlow<TextFieldValue> = _email.asStateFlow()

    private val _phone = MutableStateFlow(TextFieldValue(""))
    val phone: StateFlow<TextFieldValue> = _phone.asStateFlow()

    private val _coverImageUri = MutableStateFlow<Uri?>(null)
    val coverImageUri: StateFlow<Uri?> = _coverImageUri.asStateFlow()

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    // NEW: URI temporal que el ViewModel gestionará para la cámara
    private val _tempCameraPhotoUri = MutableStateFlow<Uri?>(null)
    val tempCameraPhotoUri: StateFlow<Uri?> = _tempCameraPhotoUri.asStateFlow() // Expuesta para que la UI la use

    fun updateName(newName: TextFieldValue) {
        _name.value = newName
    }

    fun updateEmail(newEmail: TextFieldValue) {
        _email.value = newEmail
    }

    fun updatePhone(newPhone: TextFieldValue) {
        _phone.value = newPhone
    }

    fun updateCoverImageUri(uri: Uri?) {
        _coverImageUri.value = uri
    }

    fun updateProfileImageUri(uri: Uri?) {
        _profileImageUri.value = uri
    }

    /**
     * Crea y establece una URI temporal para una foto tomada con la cámara.
     * Esta URI debe ser usada por el ActivityResultLauncher.
     * @return La URI temporal creada.
     */
    fun createAndSetTempImageUri(): Uri {
        val context = getApplication<Application>().applicationContext // Obtener el contexto de la aplicación
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile: File = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        val tempUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Debe coincidir con la autoridad de tu FileProvider
            photoFile
        )
        _tempCameraPhotoUri.value = tempUri // Almacena la URI temporal en el ViewModel
        return tempUri
    }

    /**
     * Limpia la URI temporal de la cámara después de su uso.
     */
    fun clearTempImageUri() {
        _tempCameraPhotoUri.value = null
    }

    fun saveUserProfile() {
        val currentName = _name.value.text
        val currentEmail = _email.value.text
        val currentPhone = _phone.value.text
        val currentCoverImageUri = _coverImageUri.value
        val currentProfileImageUri = _profileImageUri.value

        println("Guardando perfil: Nombre=$currentName, Email=$currentEmail, Teléfono=$currentPhone, CoverImageUri=$currentCoverImageUri, ProfileImageUri=$currentProfileImageUri")
    }
}
