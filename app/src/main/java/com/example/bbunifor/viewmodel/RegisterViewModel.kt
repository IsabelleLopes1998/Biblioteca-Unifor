package br.unifor.yetanothertodo.ui.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.bbunifor.model.Register
import com.example.bbunifor.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    private var _registerState = MutableStateFlow(
        Register(
            name = "",
            phone = "",
            email = "",
            password = "",
            matricula = "",
            isCreated = false,
            error = false,
            errorMessage = ""
        )
    )
    val registerState = _registerState.asStateFlow()

    fun updateName(name: String){
        this._registerState.update { it.copy(name = name) }
    }

    fun updatePhone(phone: String){
        this._registerState.update { it.copy(phone = phone) }
    }

    fun updateEmail(email: String){
        this._registerState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String){
        this._registerState.update { it.copy(password = password) }
    }

    fun updateMatricula(matricula: String){
        this._registerState.update { it.copy(matricula = matricula) }
    }

    fun register() {

        val name = this._registerState.value.name
        val phone = this._registerState.value.phone
        val email = this._registerState.value.email
        val password = this._registerState.value.password
        val matricula = this._registerState.value.matricula


        if(email.isNotEmpty() && password.isNotEmpty()) {
            Log.d("RegisterViewModel", "Iniciando registro para: $email")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val uid = task.result.user!!.uid
                        Log.d("RegisterViewModel", "Usuário criado com sucesso! UID: $uid")
                        
                        val user = User(name = name, phone = phone, email = email, matricula = matricula)
                        Log.d("RegisterViewModel", "Salvando dados do usuário no Realtime Database...")
                        
                        database.child("users").child(uid).setValue(user)
                            .addOnSuccessListener {
                                Log.d("RegisterViewModel", "✅ Dados salvos com sucesso no Realtime Database!")
                                this._registerState.update { it.copy(isCreated = true) }
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterViewModel", "❌ Erro ao salvar dados: ${e.message}", e)
                                this._registerState.update {
                                    it.copy(
                                        error = true,
                                        errorMessage = "Erro ao salvar dados: ${e.message}"
                                    )
                                }
                            }
                    } else {
                        Log.e("RegisterViewModel", "❌ Erro ao criar usuário: ${task.exception?.message}")
                        this._registerState.update {
                            it.copy(
                                error = true,
                                errorMessage = "Erro ao criar conta: ${task.exception?.message ?: "Erro desconhecido"}"
                            )
                        }
                    }
                }
        } else {
            this._registerState.update {
                it.copy(
                    error = true,
                    errorMessage = "Os campos devem ser preenchidos"
                )
            }
        }
    }

}