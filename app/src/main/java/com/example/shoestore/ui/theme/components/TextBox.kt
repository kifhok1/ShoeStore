package com.example.shoestore.ui.theme.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    )
}
//@Composable
//fun PasswordTextBox(
//    modifier: Modifier = Modifier,
//    textIn: String = "",
//    isPasswordVisible: Boolean = false,
//    onPasswordVisibilityChange: (Boolean) -> Unit = {},
//    onValueChange: (String) -> Unit
//) {
//
//    TextField(
//        modifier = modifier,
//        value = textIn,
//        onValueChange = onValueChange,
//        shape = RoundedCornerShape(14.dp),
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//        placeholder = {
//            Text(
//                text = "Пароль",
//                style = CustomTheme.typography.BodyRegular16
//            )
//        },
//        visualTransformation = if (isPasswordVisible)
//            VisualTransformation.None
//        else
//            PasswordVisualTransformation(),
//        trailingIcon = {
//            IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
//                Icon(
//                    painter = painterResource(
//                        id = if (isPasswordVisible)
//                        // Используйте ваши реальные ресурсы
//                            android.R.drawable.ic_menu_view
//                        else
//                            android.R.drawable.ic_menu_info_details
//                    ),
//                    contentDescription = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль"
//                )
//            }
//        },
//        colors = TextFieldDefaults.colors(
//            focusedContainerColor = CustomTheme.colors.background,
//            unfocusedContainerColor = CustomTheme.colors.background,
//            disabledContainerColor = CustomTheme.colors.background,
//            errorContainerColor = CustomTheme.colors.background,
//            cursorColor = CustomTheme.colors.accent,
//            focusedIndicatorColor = CustomTheme.colors.accent,
//            unfocusedIndicatorColor = CustomTheme.colors.background,
//            disabledIndicatorColor = CustomTheme.colors.background,
//            errorIndicatorColor = CustomTheme.colors.red
//        )
//    )
//}

@Preview
@Composable
private fun Prev() {
    var text = ""
    MainTextBox(textIn = text, placeholder = "34236325325")
}