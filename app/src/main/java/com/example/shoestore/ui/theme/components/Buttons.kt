@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoestore.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.ShoeStoreTheme

@Composable
fun MainButton(modifier: Modifier = Modifier,
               enabled: Boolean,
               onClick: () -> Unit,
               text: String) {
    ShoeStoreTheme {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomTheme.colors.accent,
                contentColor = CustomTheme.colors.background,
                disabledContainerColor = CustomTheme.colors.disable,
                disabledContentColor = CustomTheme.colors.background
            ),
            enabled = enabled,
            shape = RoundedCornerShape(14.dp)
        ){
            Text(
                text = text,
                style = CustomTheme.typography.BodyRegular14
            )
        }
    }
}

//@Composable
//fun MainButtonIcon(modifier: Modifier = Modifier,
//               enabled: Boolean,
//               onClick: () -> Unit,
//               text: String) {
//    ShoeStoreTheme {
//        Button(
//            onClick = onClick,
//            modifier = modifier,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = CustomTheme.colors.accent,
//                contentColor = CustomTheme.colors.background,
//                disabledContainerColor = CustomTheme.colors.disable,
//                disabledContentColor = CustomTheme.colors.background
//            ),
//            enabled = enabled,
//            shape = RoundedCornerShape(14.dp)
//        ){
//            Row(){
//                Image(
//                    painter = painterResource(R.drawable.add)
//                )
//                Text(
//                    text = text,
//                    style = CustomTheme.typography.BodyRegular14
//                )
//            }
//        }
//    }
//}

@Composable
fun IconButtonBack(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(CustomTheme.colors.background, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = null,
            tint = CustomTheme.colors.text,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun IconButtonPersonalData(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    var buttonEnabled = enabled
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(
                if (buttonEnabled) {
                    CustomTheme.colors.accent
                } else {
                    CustomTheme.colors.background
                }, shape = RoundedCornerShape(14.dp)
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.policy_check),
            contentDescription = null,
            tint = if (buttonEnabled) {
                CustomTheme.colors.block
            } else {
                CustomTheme.colors.text
            },
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview
@Composable
private fun Prev() {
    MainButton(enabled = true,
               onClick = {},
               text = "1243")
}