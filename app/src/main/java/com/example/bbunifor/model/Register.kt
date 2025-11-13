package com.example.bbunifor.model

data class Register(
    val name:String,
    val phone: String,
    val email: String,
    val matricula: String,
    val password: String,
    val confirmPassword: String,
    val isCreated: Boolean,
    val error: Boolean,
    val errorMessage: String,
    val errorMessagePassword: String

)