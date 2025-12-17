package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoestore.R
import com.example.shoestore.data.model.PasswordRecoveryState
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.EmailSentDialog
import com.example.shoestore.ui.theme.components.EmailTextBox
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.MainButton
import com.example.shoestore.ui.theme.viewModel.ForgotPasswordViewModel

@Composable
fun ForgotPassword(modifier: Modifier = Modifier,
                   onBackClick: () -> Unit,
                   onOTPClick: (String) -> Unit,
                   viewModel: ForgotPasswordViewModel = viewModel()  ) {
    var email by remember { mutableStateOf("") }
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val recoveryState by viewModel.passwordRecoveryState.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(recoveryState) {
        when (val state = recoveryState) {
            is PasswordRecoveryState.Success -> {
                showSuccessDialog = true
                viewModel.resetState()
            }
            is PasswordRecoveryState.Error -> {
                errorMessage = state.message
                showErrorDialog = true
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Диалог ошибки (Пункт 9)
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) }, // Текст ошибки из ViewModel
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            },
            containerColor = CustomTheme.colors.block,
            titleContentColor = CustomTheme.colors.text,
            textContentColor = CustomTheme.colors.text
        )
    }

    if (showSuccessDialog) {
        EmailSentDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onOTPClick(email)
            }
        )
    }
    Column(modifier = modifier
        .background(CustomTheme.colors.block)
        .padding(start = 20.dp, top = 23.dp, end = 20.dp, bottom = 47.dp),
        horizontalAlignment = Alignment.CenterHorizontally){
        IconButtonBack(
            onClick = { onBackClick() },
            modifier = Modifier.align(Start)
        )
        Spacer(modifier = Modifier.height(11.dp))
        Text(style = CustomTheme.typography.HeadingRegular32,
            text = stringResource(R.string.Forgot_Password),
            color = CustomTheme.colors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(style = CustomTheme.typography.BodyRegular16,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.Enter_your_Email),
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
            EmailTextBox(modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {email = it},
                placeholder = "xyz@gmail.com"
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        MainButton(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
            enabled = isEmailValid && recoveryState !is PasswordRecoveryState.Loading,
            text = stringResource(R.string.Sign_In),
            onClick = {
                viewModel.recoverPassword()
            })
        Spacer(modifier = Modifier.weight(1f))
    }
    if (recoveryState is PasswordRecoveryState.Loading) {
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