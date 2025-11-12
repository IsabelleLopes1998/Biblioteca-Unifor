package br.unifor.yetanothertodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bbunifor.model.Login
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    private var _loginState = MutableStateFlow(
        Login(
            email = "",
            emailError = false,
            emailErrorText = "",
            password = "",
            passwordError = false,
            passwordErrorText = "",
            isLoggedIn = false
        )
    )
    val loginState = _loginState.asStateFlow()

    fun updateEmail(email: String){
        this._loginState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String){
        this._loginState.update { it.copy(password = password) }
    }

    fun singIn() {

        val email = _loginState.value.email
        val password = _loginState.value.password

        if(email.isEmpty()) {
            this._loginState.update {
                it.copy(
                    emailError = true,
                    emailErrorText = "O campo não pode estar vazio"
                )
            }
        } else {
            this._loginState.update {
                it.copy(
                    emailError = false,
                    emailErrorText = ""
                )
            }
        }

        if(password.isEmpty()) {
            this._loginState.update {
                it.copy(
                    passwordError = true,
                    passwordErrorText = "O campo não pode estar vazio"
                )
            }
        } else {
            this._loginState.update {
                it.copy(
                    passwordError = false,
                    passwordErrorText = ""
                )
            }
        }

        if(email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        this._loginState.update {
                            it.copy(isLoggedIn = true)
                        }
                    } else {
                        this._loginState.update {
                            it.copy(
                                emailError = true,
                                emailErrorText = "Email ou senha incorretos",
                                passwordError = true,
                                passwordErrorText = "Email ou senha incorretos"
                            )
                        }
                    }
                }
        }
    }

}