package com.example.shoestore.ui.theme.screens

import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.EmailTextBox
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.IconButtonPersonalData
import com.example.shoestore.ui.theme.components.MainButton
import com.example.shoestore.ui.theme.components.MainTextBox
import com.example.shoestore.ui.theme.components.PasswordTextBox

@Composable
fun RegistrationScreen(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var personalData by remember { mutableStateOf(false) }
    Column(modifier = modifier.background(CustomTheme.colors.block)
                              .padding(start = 20.dp, top = 23.dp, end = 20.dp, bottom = 47.dp),
        horizontalAlignment = Alignment.CenterHorizontally){
        IconButtonBack(
            modifier = Modifier.align(Start)
        ){}
        Spacer(modifier = Modifier.height(11.dp))
        Text(style = CustomTheme.typography.HeadingRegular32,
             text = stringResource(R.string.Register_Account),
            color = CustomTheme.colors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(style = CustomTheme.typography.BodyRegular24,
            text = stringResource(R.string.Fill_your_details),
            color = CustomTheme.colors.text
        )
        Spacer(modifier = Modifier.height(54.dp))
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CustomTheme.colors.block
            ),
            shape = RoundedCornerShape(0.dp)) {
            Text(style = CustomTheme.typography.BodyRegular20,
                text = stringResource(R.string.Your_Name),
                color = CustomTheme.colors.text
            )
            Spacer(modifier = Modifier.height(12.dp))
            MainTextBox(modifier = Modifier.fillMaxWidth(),
                        value = name,
                        onValueChange = {name = it},
                        placeholder = "xxxxxxxx"
            )
        }
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
        Row(modifier = Modifier.height(38.dp)){
            IconButtonPersonalData(
                modifier = Modifier,
                enabled = personalData,
                onClick = { personalData = !personalData }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.personal_data),
                style = CustomTheme.typography.BodyRegular16,
                color = CustomTheme.colors.text
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        MainButton(modifier = Modifier.fillMaxWidth().height(50.dp),
                   enabled = personalData,
                   text = stringResource(R.string.Sign_up),
                   onClick = {})
        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.clickable(onClick = {})){
            Text(style = CustomTheme.typography.BodyRegular16,
                text = stringResource(R.string.Already_Have_Account),
                color = CustomTheme.colors.hint
            )
            Text(style = CustomTheme.typography.BodyRegular16,
                text = " ",
                color = CustomTheme.colors.hint
            )
            Text(style = CustomTheme.typography.BodyRegular16,
                text = stringResource(R.string.Sign_In),
                color = CustomTheme.colors.text
            )
        }
    }
}

@Preview
@Composable
private fun Prew() {
    RegistrationScreen(modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block))
}