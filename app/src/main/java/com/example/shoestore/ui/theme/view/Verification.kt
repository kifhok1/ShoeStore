package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoestore.R
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.data.model.VerificationState
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.OtpTextBox
import com.example.shoestore.ui.theme.viewModel.EmailVerificationViewModel

@Composable
fun Verification(
    modifier: Modifier = Modifier,
    email: String,
    otpType: OtpType = OtpType.EMAIL, // Default to Email/Signup
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EmailVerificationViewModel = viewModel()
) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(30) }
    val isTimerFinished = timeLeft <= 0
    val verificationState by viewModel.verificationState.collectAsState()

    // Timer logic
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft -= 1
        }
    }

    // React to State Changes
    LaunchedEffect(verificationState) {
        if (verificationState is VerificationState.Success) {
            viewModel.resetState()
            onSuccess()
        }
    }

    // Auto-submit when OTP is 6 digits
    LaunchedEffect(otp) {
        if (otp.length == 6) {
            viewModel.verifyOtp(email, otp, otpType)
        }
    }

    Column(
        modifier = modifier
            .background(CustomTheme.colors.block)
            .padding(start = 20.dp, top = 23.dp, end = 20.dp, bottom = 47.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButtonBack(
            onClick = { onBackClick() },
            modifier = Modifier.align(Start)
        )

        Spacer(modifier = Modifier.height(11.dp))
        Text(
            style = CustomTheme.typography.HeadingRegular32,
            text = stringResource(R.string.OTP_Verification),
            color = CustomTheme.colors.text
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            style = CustomTheme.typography.BodyRegular16,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.check_your_email),
            color = CustomTheme.colors.hint
        )

        Spacer(modifier = Modifier.height(54.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CustomTheme.colors.block),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                style = CustomTheme.typography.BodyRegular20,
                text = stringResource(R.string.OTP_Code),
                color = CustomTheme.colors.text
            )
            Spacer(modifier = Modifier.height(12.dp))
            OtpTextBox(
                modifier = Modifier.fillMaxWidth(),
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row {
            // Resend Logic
            if (isTimerFinished) {
                Text(
                    modifier = Modifier.clickable {
                        timeLeft = 30 // Reset timer
                        viewModel.resendOtp(email, otpType) // Call API
                    },
                    style = CustomTheme.typography.BodyRegular12,
                    text = stringResource(R.string.resend),
                    color = CustomTheme.colors.hint
                )
            } else {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                val timeText = String.format("%d:%02d", minutes, seconds)
                Text(
                    style = CustomTheme.typography.BodyRegular12,
                    text = timeText,
                    color = CustomTheme.colors.hint
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Loading and Error UI
        if (verificationState is VerificationState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        if (verificationState is VerificationState.Error) {
            Text(
                text = (verificationState as VerificationState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
