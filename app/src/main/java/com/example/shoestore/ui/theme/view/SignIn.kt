package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoestore.R
import com.example.shoestore.data.model.SignInState
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.EmailTextBox
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.MainButton
import com.example.shoestore.ui.theme.components.PasswordTextBox
import com.example.shoestore.ui.theme.viewModel.SignInViewModel

@Composable
fun SignIn(modifier: Modifier = Modifier,
           signInViewModel: SignInViewModel = viewModel(),
           onRegisterClick: () -> Unit,
           onBackClick: () -> Unit,
           onForgotPasswordClick: () -> Unit,
           onHome: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val signInState by signInViewModel.signInState.collectAsState()

    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInState.Success -> {
                onHome()
                signInViewModel.resetState()
            }
            is SignInState.Error -> {
                dialogMessage = (signInState as SignInState.Error).message
                showDialog = true
                signInViewModel.resetState()
            }
            else -> {}
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Ошибка") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            containerColor = CustomTheme.colors.block,
            titleContentColor = CustomTheme.colors.text,
            textContentColor = CustomTheme.colors.text
        )
    }

    Column(modifier = modifier.background(CustomTheme.colors.block)
        .padding(start = 20.dp, top = 23.dp, end = 20.dp, bottom = 47.dp),
        horizontalAlignment = Alignment.CenterHorizontally){
        IconButtonBack(
            onClick = { onBackClick() },
            modifier = Modifier.align(Start)
        )
        Spacer(modifier = Modifier.height(11.dp))
        Text(style = CustomTheme.typography.HeadingRegular32,
            text = stringResource(R.string.Hello_Again),
            color = CustomTheme.colors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(style = CustomTheme.typography.BodyRegular16,
            text = stringResource(R.string.Fill_your_details),
            color = CustomTheme.colors.hint
        )
        Spacer(modifier = Modifier.height(54.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CustomTheme.colors.block
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(style = CustomTheme.typography.BodyRegular20,
                text = stringResource(R.string.Email_Address),
                color = CustomTheme.colors.text
            )
            Spacer(modifier = Modifier.height(12.dp))
            EmailTextBox(modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {email = it},
                placeholder = "xyz@gmail.com"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CustomTheme.colors.block
            ),
            shape = RoundedCornerShape(0.dp)) {
            Text(style = CustomTheme.typography.BodyRegular20,
                text = stringResource(R.string.Password),
                color = CustomTheme.colors.text
            )
            Spacer(modifier = Modifier.height(12.dp))
            PasswordTextBox(modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = {password = it},
                placeholder = "• • • • • • • •"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.align(End).clickable(onClick = { onForgotPasswordClick() }),
            style = CustomTheme.typography.BodyRegular12,
            text = stringResource(R.string.Recovery_Password),
            color = CustomTheme.colors.hint
        )
        Spacer(modifier = Modifier.height(24.dp))
        MainButton(modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = email.matches(Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z0-9]{2,3}$")),
            text = stringResource(R.string.Sign_In),
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    dialogMessage = "Поля не могут быть пустыми"
                    showDialog = true
                }
                else {
                    signInViewModel.signIn(email, password)
                }
            })
        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.clickable(onClick = { onRegisterClick() })){
            Text(style = CustomTheme.typography.BodyRegular16,
                text = stringResource(R.string.Create_Account),
                color = CustomTheme.colors.hint
            )
            Text(style = CustomTheme.typography.BodyRegular16,
                text = " ",
                color = CustomTheme.colors.hint
            )
            Text(style = CustomTheme.typography.BodyRegular16,
                text = stringResource(R.string.Create_Account_Name),
                color = CustomTheme.colors.text
            )
        }
    }
    if (signInState is SignInState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
                .clickable(enabled = false) {}, // Блокировка кликов
            contentAlignment = Center
        ) {
            CircularProgressIndicator(color = CustomTheme.colors.accent)
        }
    }
}

@Preview
@Composable
private fun Prew() {}