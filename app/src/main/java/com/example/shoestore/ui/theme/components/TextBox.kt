package com.example.shoestore.ui.theme.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme

@Composable
fun MainTextBox(modifier: Modifier = Modifier,
                textIn: String,
                placeholder: String) {
            var text by rememberSaveable { mutableStateOf(textIn) }
        TextField(
            modifier = modifier,
            value = text,
            onValueChange = { text = it },
            shape = RoundedCornerShape(14.dp),

            textStyle = CustomTheme.typography.BodyRegular16,
            placeholder = {
                Text(
                    text = placeholder,
                    color = CustomTheme.colors.hint,
                    style = CustomTheme.typography.BodyRegular16
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CustomTheme.colors.background,
                unfocusedContainerColor = CustomTheme.colors.background,
                disabledContainerColor = CustomTheme.colors.background,
                errorContainerColor = CustomTheme.colors.background,

                cursorColor = CustomTheme.colors.accent,
                focusedIndicatorColor = CustomTheme.colors.accent,
                unfocusedIndicatorColor = CustomTheme.colors.background,
                disabledIndicatorColor = CustomTheme.colors.background,
                errorIndicatorColor = CustomTheme.colors.red,

                errorTextColor = CustomTheme.colors.red,
                unfocusedTextColor = CustomTheme.colors.text,
                focusedTextColor = CustomTheme.colors.text
            ),
            singleLine = true
        )
}

@Composable
fun PasswordTextBox(
    modifier: Modifier = Modifier,
    textIn: String,
    placeholder: String
){
    var text by rememberSaveable { mutableStateOf(textIn) }
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { text = it },
        shape = RoundedCornerShape(14.dp),
        textStyle = CustomTheme.typography.BodyRegular16,
        placeholder = {
            Text(
                text = placeholder,
                color = CustomTheme.colors.hint,
                style = CustomTheme.typography.BodyRegular16
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CustomTheme.colors.background,
            unfocusedContainerColor = CustomTheme.colors.background,
            disabledContainerColor = CustomTheme.colors.background,
            errorContainerColor = CustomTheme.colors.background,

            cursorColor = CustomTheme.colors.accent,
            focusedIndicatorColor = CustomTheme.colors.accent,
            unfocusedIndicatorColor = CustomTheme.colors.background,
            disabledIndicatorColor = CustomTheme.colors.background,
            errorIndicatorColor = CustomTheme.colors.red,

            errorTextColor = CustomTheme.colors.red,
            unfocusedTextColor = CustomTheme.colors.text,
            focusedTextColor = CustomTheme.colors.text
        ),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisibility)
                R.drawable.eye_close
            else
                R.drawable.eye_open

            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = if (passwordVisibility) "Скрыть пароль" else "Показать пароль",
                    tint = CustomTheme.colors.hint
                )
            }
        },
        singleLine = true
    )
}

@Composable
fun EmailTextBox(
    modifier: Modifier = Modifier,
    textIn: String,
    placeholder: String
) {
    var text by rememberSaveable { mutableStateOf(textIn) }

    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { text = it },
        shape = RoundedCornerShape(14.dp),
        textStyle = CustomTheme.typography.BodyRegular16,
        placeholder = {
            Text(
                text = placeholder,
                color = CustomTheme.colors.hint,
                style = CustomTheme.typography.BodyRegular16
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CustomTheme.colors.background,
            unfocusedContainerColor = CustomTheme.colors.background,
            disabledContainerColor = CustomTheme.colors.background,
            errorContainerColor = CustomTheme.colors.background,

            cursorColor = CustomTheme.colors.accent,
            focusedIndicatorColor = CustomTheme.colors.accent,
            unfocusedIndicatorColor = CustomTheme.colors.background,
            disabledIndicatorColor = CustomTheme.colors.background,
            errorIndicatorColor = CustomTheme.colors.red,

            errorTextColor = CustomTheme.colors.red,
            unfocusedTextColor = CustomTheme.colors.text,
            focusedTextColor = CustomTheme.colors.text
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        singleLine = true
    )
}


@Preview
@Composable
private fun Prev() {
    var text = "12341314"
    EmailTextBox(textIn = text, placeholder = "123453@gmail.com")
}