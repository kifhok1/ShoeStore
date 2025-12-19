package com.example.shoestore.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoestore.data.model.FieldType
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme

// Функция валидации телефона (11 цифр)
fun isValidPhone(phone: String): Boolean {
    val digitsOnly = phone.filter { it.isDigit() }
    return digitsOnly.length == 11
}

// Форматирование номера телефона - только цифры, максимум 11
fun formatPhoneNumber(digits: String): String {
    return digits.take(11)
}


// Капитализация первой буквы
fun capitalizeFirstLetter(text: String): String {
    return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

// Функции валидации
fun isValidName(name: String): Boolean {
    return name.isNotEmpty() && name.length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
}

fun isValidAddress(address: String): Boolean {
    return address.isNotEmpty() && address.length >= 5
}

@Composable
fun EditableProfileField(
    label: String,
    value: String,
    enabled: Boolean,
    fieldType: FieldType,
    onValueChange: (String) -> Unit
) {
    val isValid = when (fieldType) {
        FieldType.FIRST_NAME, FieldType.LAST_NAME -> isValidName(value)
        FieldType.PHONE -> isValidPhone(value)
        FieldType.ADDRESS -> isValidAddress(value)
    }

    val showCheck = value.isNotEmpty() && when (fieldType) {
        FieldType.PHONE -> value != "+7"
        else -> true
    }

    val checkColor = if (isValid) CustomTheme.colors.accent else CustomTheme.colors.red

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = label, style = CustomTheme.typography.BodyRegular20, color = CustomTheme.colors.text)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                when (fieldType) {
                    FieldType.PHONE -> {
                        // Извлекаем только цифры из введенного текста
                        val digitsOnly = newValue.filter { it.isDigit() }
                        // Форматируем номер
                        val formatted = formatPhoneNumber(digitsOnly)
                        onValueChange(formatted)
                    }
                    FieldType.FIRST_NAME, FieldType.LAST_NAME -> {
                        // Фильтруем только буквы и пробелы
                        val filtered = newValue.filter { it.isLetter() || it.isWhitespace() }
                        // Капитализируем первую букву
                        onValueChange(capitalizeFirstLetter(filtered))
                    }
                    FieldType.ADDRESS -> {
                        onValueChange(newValue)
                    }
                }
            },
            enabled = enabled,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = when (fieldType) {
                FieldType.PHONE -> KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
                FieldType.FIRST_NAME, FieldType.LAST_NAME -> KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                )
                else -> KeyboardOptions.Default
            },
            trailingIcon = {
                if (showCheck) {
                    Image(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = if (isValid) "Valid" else "Invalid",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(checkColor)
                    )
                }
            },
            textStyle = CustomTheme.typography.BodyRegular14,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CustomTheme.colors.block,
                unfocusedContainerColor = CustomTheme.colors.block,
                disabledContainerColor = CustomTheme.colors.block,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = CustomTheme.colors.subTextDark,
                disabledTextColor = CustomTheme.colors.subTextDark
            )
        )
    }
}
