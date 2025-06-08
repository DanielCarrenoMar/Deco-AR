package com.app.homear.ui.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.auth.CurrentUserUseCase
import com.app.homear.domain.usecase.auth.SignInUseCase
import com.app.homear.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// El codigo esta demasiado formidable, bajale tantito

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val currentUserUseCase: CurrentUserUseCase
): ViewModel() {
    private val _email  = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _pass = MutableLiveData<String>()
    val pass: LiveData<String> = _pass
    private val _isLogged = MutableLiveData<Boolean>()
    val isLogged: LiveData<Boolean> = _isLogged
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun  loginUser(email:String, pass:String, onLoginSuccess: () -> Unit){
        viewModelScope.launch {
            signInUseCase.invoke(email,pass).collect{
                    result ->
                when(result){
                    is Resource.Success ->{
                        _isLogged.value = true
                        onLoginSuccess()

                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error -> {
                        Log.e("SingIn", "Error al iniciar Sesion, ${result.message}")
                        _error.value = true
                    }
                }
            }
        }
    }

    fun registerUser(email: String, pass: String, onRegisterSuccess: () -> Unit){
        viewModelScope.launch {
            signUpUseCase.invoke(email,pass).collect{
                    result ->
                when(result){
                    is Resource.Success -> {
                        _isLogged.value = true
                        onRegisterSuccess()
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {
                        Log.e("SingIn", "Error al iniciar Sesion, ${result.message}")
                        _error.value = true
                    }
                }
            }
        }
    }

    fun isLogeed(){
        viewModelScope.launch {
            val currentUser = currentUserUseCase.invoke()
            if (currentUser != null) {
                _isLogged.value = true
            }
        }
    }

    fun onLoginChange(email: String, pass: String){
        _email.value = email
        _pass.value = pass
    }

    fun onChangeError(showError: Boolean){
        _error.value = false
    }
}