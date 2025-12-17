package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.shoestore.ui.theme.components.EmailSentDialog
import com.example.shoestore.ui.theme.components.EmailTextBox
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.MainButton

@Composable
fun ForgotPassword(modifier: Modifier = Modifier,
                   onBackClick: () -> Unit,
                   onOTPClick: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var personalData by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        EmailSentDialog(
            onDismissRequest = {
                showDialog = false
                onOTPClick("242525")
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
            enabled = personalData,
            text = stringResource(R.string.Sign_In),
            onClick = {
                showDialog = !showDialog
            })
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun Prew() {}