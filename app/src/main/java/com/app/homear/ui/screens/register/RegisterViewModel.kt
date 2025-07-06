package com.app.homear.ui.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.ProviderModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.usecase.auth.CurrentUserUseCase
import com.app.homear.domain.usecase.auth.SignUpUseCase
import com.app.homear.domain.usecase.firestore.UpdateProviderUseCase
import com.app.homear.domain.usecase.firestore.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val updateProviderUseCase: UpdateProviderUseCase
): ViewModel() {
    private val _name = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()
    private val _pass = MutableLiveData<String>()
    private val _isLogged = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Boolean>()
    val email: LiveData<String> = _email
    val pass: LiveData<String> = _pass
    val isLogged: LiveData<Boolean> = _isLogged
    val error: LiveData<Boolean> = _error
    val name: LiveData<String> = _name
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun registerUser(email: String, pass: String, name: String){
        viewModelScope.launch {
            signUpUseCase.invoke(email, pass).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val id = currentUserUseCase.invoke()?.uid
                        if (id != null) {
                            updateUserUseCase.invoke(id, UserModel(name, email,"USER")).collect{

                                _isLoading.value = false
                                _error.value = false
                                Log.e("SingIn", "Error al iniciar Sesion, ${result.data}")

                            }
                            _isLogged.value = true
                        }

                    }

                    is Resource.Loading -> {
                        _isLoading.value = true
                    }

                    is Resource.Error -> {
                        _error.value = true
                        Log.e("SingIn", "Error al iniciar Sesion, ${result.message}")
                    }


                }
            }
        }
    }

    fun registerProvider(rif:String, pass: String, name: String ) {
        viewModelScope.launch {
            val email = "$rif@deco.ar.com"
            signUpUseCase(email, pass).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val id = currentUserUseCase.invoke()?.uid
                        if (id != null) {
                            updateProviderUseCase.invoke(ProviderModel(name = name, email = email, id = rif, address = null, phone = null, image = null, description = null, state = null, city = null, country = null)).collect {

                            }
                            updateUserUseCase.invoke(id,UserModel(name = name, email = email, type = "PROVIDER")).collect {}
                        }
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Error -> {
                        _error.value = true
                        Log.e("SingIn", "Error al iniciar Sesion, ${result.message}")
                    }

                }
            }
        }
    }

}