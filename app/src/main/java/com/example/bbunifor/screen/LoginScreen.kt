package com.example.bbunifor.screen


import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import br.unifor.yetanothertodo.ui.viewmodel.LoginViewModel
import com.example.bbunifor.R
import kotlinx.serialization.Serializable


@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {

    val login = loginViewModel.loginState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Navegar para HomeScreen quando login for bem-sucedido
    LaunchedEffect(login.value.isLoggedIn) {
        if(login.value.isLoggedIn) {
            navController.navigate(HomeScreen) {
                popUpTo(LoginScreen) { inclusive = true }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        Image(
            painter = painterResource(R.drawable.unifor_),
            contentDescription = "Logo da aplicação",
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
        )

        /*Text(
            text = "Biblioteca Unifor",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 30.sp,
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp)
        )*/

        OutlinedTextField(
            value = login.value.email,
            onValueChange = {
                loginViewModel.updateEmail(it)
            },
            label = { Text("E-mail") },
            isError = login.value.emailError,
            supportingText = {
                if(login.value.emailError){
                    Text(login.value.emailErrorText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 32.dp,
                    start = 16.dp,
                    end = 16.dp)
        )

        OutlinedTextField(
            value = login.value.password,
            onValueChange = {
                loginViewModel.updatePassword(it)
            },
            label = { Text("Senha") },
            isError = login.value.passwordError,
            supportingText = {
                if(login.value.passwordError){
                    Text(login.value.passwordErrorText)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp)
        )

        Text(
            text = "Clique aqui para se registar",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .clickable(enabled = true, onClick = {
                    navController.navigate(RegisterScreen)
                })
        )

        Button(
            onClick = {
                loginViewModel.singIn()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) { 
            Text("Entrar") 
        }
    }
}

@Serializable
object LoginScreen
