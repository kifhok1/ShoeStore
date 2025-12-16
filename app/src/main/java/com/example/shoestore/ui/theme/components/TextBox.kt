package com.example.shoestore.ui.theme.components

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme
import java.time.format.TextStyle

@Composable
fun MainTextBox(modifier: Modifier = Modifier,
                value: String,
                onValueChange: (String) -> Unit,
                placeholder: String) {
        TextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
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
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
){
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
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
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
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

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    onOtpComplete: (String) -> Unit
) {
    var otpValues by remember { mutableStateOf(List(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until otpLength) {
            // Обертка для обработки событий клавиатуры (Backspace) на пустом поле
            Box(
                modifier = Modifier
                    .width(48.dp) // Ширина одного блока
                    .height(60.dp) // Высота одного блока
                    // Перехват нажатий клавиш для обработки Backspace
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown ) { //&& event.key == Key.Backspace
                            if (otpValues[i].isEmpty() && i > 0) {
                                // Если поле пустое и нажат Backspace -> идем назад и стираем предыдущее
                                val newOtpValues = otpValues.toMutableList()
                                newOtpValues[i - 1] = ""
                                otpValues = newOtpValues
                                focusRequesters[i - 1].requestFocus()
                                return@onKeyEvent true
                            }
                        }
                        false
                    }
            ) {
                TextField(
                    value = TextFieldValue(
                        text = otpValues[i],
                        selection = TextRange(otpValues[i].length) // Курсор всегда в конце
                    ),
                    onValueChange = { newValue ->
                        val text = newValue.text

                        // Логика ввода: только цифры, макс 1 символ
                        if (text.length <= 1 && text.all { it.isDigit() }) {
                            val newOtpValues = otpValues.toMutableList()
                            newOtpValues[i] = text
                            otpValues = newOtpValues

                            if (text.isNotEmpty()) {
                                // Если введен символ и это не последнее поле -> фокус вперед
                                if (i < otpLength - 1) {
                                    focusRequesters[i + 1].requestFocus()
                                } else {
                                    // Если последнее поле заполнено -> завершение
                                    focusManager.clearFocus()
                                    onOtpComplete(newOtpValues.joinToString(""))
                                }
                            }
                        } else if (text.isEmpty()) {
                            // Логика удаления символа внутри поля (обычный backspace, когда есть символ)
                            val newOtpValues = otpValues.toMutableList()
                            newOtpValues[i] = ""
                            otpValues = newOtpValues
                            // При удалении символа остаемся на месте, переход назад обрабатывается onKeyEvent
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequesters[i])
                        // Кастомный бордер (как на дизайне: серый по умолчанию, красный/акцентный при фокусе можно добавить)
                        .border(
                            width = 1.dp,
                            color = if (otpValues[i].isEmpty()) Color.LightGray else CustomTheme.colors.red, // Пример цвета из дизайна
                            shape = RoundedCornerShape(12.dp)
                     ),
//                    textStyle = TextStyle(
//                        fontSize = 20.sp,
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold
//                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5), // Светло-серый фон
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent, // Убираем стандартную линию подчеркивания
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    OtpTextField() {  }
}