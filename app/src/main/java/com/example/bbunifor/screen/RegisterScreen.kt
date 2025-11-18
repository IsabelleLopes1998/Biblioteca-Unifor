package com.example.bbunifor.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import com.example.bbunifor.components.TopBarNavigation
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.unifor.yetanothertodo.ui.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState  = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarNavigation(
                title = "Cadastro",
                navigateBack = { navController.popBackStack() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    )
    { innerPadding ->

        val register = registerViewModel.registerState.collectAsState()

        if(register.value.isCreated){
            navController.navigate(LoginScreen)
        }

        if(register.value.error && register.value.errorMessage.isNotEmpty()){
            SideEffect {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(register.value.errorMessage)
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {


            OutlinedTextField(
                value = register.value.name,
                onValueChange = {
                    registerViewModel.updateName(it)
                },
                label = { Text("Nome") },
                isError = false,
                supportingText = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp)
            )

            OutlinedTextField(
                value = register.value.matricula,
                onValueChange = {
                    registerViewModel.updateMatricula(it)
                },
                label = { Text("Matricula") },
                isError = false,
                supportingText = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp)
            )



            OutlinedTextField(
                value = register.value.phone,
                onValueChange = {
                    registerViewModel.updatePhone(it)
                },
                label = { Text("Telefone") },
                isError = false,
                supportingText = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp)
            )

            OutlinedTextField(
                value = register.value.email,
                onValueChange = {
                    registerViewModel.updateEmail(it)
                },
                label = { Text("E-mail") },
                isError = false,
                supportingText = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp)
            )

            var passwordVisible by remember { mutableStateOf(false) }
            var confirmPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = register.value.password,
                onValueChange = {
                    registerViewModel.updatePassword(it)
                },
                label = { Text("Senha") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                isError = register.value.errorMessagePassword.isNotEmpty(),
                supportingText = {
                    if (register.value.errorMessagePassword.isNotEmpty()) {
                        Text(register.value.errorMessagePassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp)
            )

            OutlinedTextField(
                value = register.value.confirmPassword,
                onValueChange = {
                    registerViewModel.updateConfirmPassword(it)
                },
                label = { Text("Confirmar Senha") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                isError = register.value.errorMessagePassword.isNotEmpty(),
                supportingText = {
                    if (register.value.errorMessagePassword.isNotEmpty()) {
                        Text(register.value.errorMessagePassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp)
            )

            Button(
                onClick = {
                    registerViewModel.register()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) { Text("Cadastrar") }
        }
    }
}

@Serializable
object RegisterScreen