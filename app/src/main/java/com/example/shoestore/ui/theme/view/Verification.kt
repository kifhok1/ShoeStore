package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.OtpTextBox

@Composable
fun Verification(modifier: Modifier = Modifier,
                 email: String,
                 onBackClick: () -> Unit,
                 onSuccess: () -> Unit) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(30) }      // в секундах
    val isTimerFinished = timeLeft <= 0

    // Простой таймер на LaunchedEffect: раз в секунду уменьшаем timeLeft
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft -= 1
        }
    }

    LaunchedEffect(key1 = otp)
    {
        // Тут типо проверка на правильность кода подтверждания
        if (otp.length == 6){
            onSuccess()
        }
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
            text = stringResource(R.string.OTP_Verification),
            color = CustomTheme.colors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(style = CustomTheme.typography.BodyRegular16,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.check_your_email),
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
                text = stringResource(R.string.OTP_Code),
                color = CustomTheme.colors.text
            )
            Spacer(modifier = Modifier.height(12.dp))
            OtpTextBox(modifier = Modifier.fillMaxWidth(),
                value = otp,
                onValueChange = {otp = it}
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(){
            if (isTimerFinished) {
                Text(
                    modifier = Modifier.clickable(onClick = {}),
                    style = CustomTheme.typography.BodyRegular12,
                    text = stringResource(R.string.resend),
                    color = CustomTheme.colors.hint
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Сам таймер: показываем только пока не закончился
            if (!isTimerFinished) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                val timeText = String.format("%d:%02d", minutes, seconds)

                Text(
                    style = CustomTheme.typography.BodyRegular12,
                    text = timeText,
                    color = CustomTheme.colors.hint
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun Prew() {}