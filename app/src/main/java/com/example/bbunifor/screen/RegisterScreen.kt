package com.example.bbunifor.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    )
    { innerPadding ->

        val register = registerViewModel.registerState.collectAsState()

        if(register.value.isCreated){
            navController.navigate(LoginScreen)
        }

        if(register.value.error){
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

            Text(
                text = "Formulário Cadastro",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp)
            )

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

            OutlinedTextField(
                value = register.value.password,
                onValueChange = {
                    registerViewModel.updatePassword(it)
                },
                label = { Text("Senha") },
                isError = false,
                supportingText = {},
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