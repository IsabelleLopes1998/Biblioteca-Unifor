package com.example.bbunifor.model

data class Login(
    val email: String,
    val emailError:Boolean,
    val emailErrorText:String,
    val password: String,
    val passwordError:Boolean,
    val passwordErrorText:String,
    val isLoggedIn: Boolean = false
)
