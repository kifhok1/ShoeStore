package com.example.shoestore.ui.theme.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoestore.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoestore.ui.theme.Accent
import com.example.shoestore.ui.theme.Background
import com.example.shoestore.ui.theme.CustomTheme
import java.util.jar.Manifest

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Профиль",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {

    var activeTab by remember { mutableIntStateOf(3) }

    val tabs = remember {
        listOf(
            NavTab(R.drawable.home, "Home"),
            NavTab(R.drawable.favorite, "Favorite"),
            NavTab(R.drawable.orders, "Orders"),
            NavTab(R.drawable.profile, "Profile")
        )
    }

    var isEditMode by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val context = LocalContext.current
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val vm: ProfileViewModel = viewModel()
    val profileState by vm.state.collectAsState()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val uri = pendingCameraUri
                avatarUri = uri
                if (uri != null) vm.saveAvatar(uri)
            }
            pendingCameraUri = null
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val uri = context.createTempImageUri()
                pendingCameraUri = uri
                takePictureLauncher.launch(uri)
            }
            // если пользователь запретил — можно показать MessageDialog
        }
    )

    LaunchedEffect(profileState) {
        val p = (profileState as? ProfileState.Ready)?.profile ?: return@LaunchedEffect
        firstName = p.firstname.orEmpty()
        lastName = p.lastname.orEmpty()
        address = p.address.orEmpty()
        phone = p.phone.orEmpty()
    }
    val fullName by remember(firstName, lastName) {
        mutableStateOf(
            listOf(firstName.trim(), lastName.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
                .ifEmpty { "Пользователь" }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ===== Header =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.Profile),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Accent)
                        .clickable { isEditMode = !isEditMode },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ===== Avatar =====
            val profile = (profileState as? ProfileState.Ready)?.profile

            val avatarModel: Any = avatarUri ?: (profile?.photo ?: R.drawable.profile_avatar)

            AsyncImage(
                model = avatarModel,
                contentDescription = "Avatar",
                onError = { err -> android.util.Log.e("AvatarUI", "Coil error", err.result.throwable) },
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = fullName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            if (isEditMode) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.PhotoProfile),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = CustomTheme.colors.subTextDark
                )
            }

            Spacer(Modifier.height(14.dp))

            // Barcode можешь оставить как было (или скрывать в edit)
            // Здесь оставлю скрытым в edit, как в твоём предыдущем варианте:
            if (!isEditMode) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CustomTheme.colors.block),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Barcode",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .height(26.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
                Spacer(Modifier.height(18.dp))
            } else {
                Spacer(Modifier.height(10.dp))
            }

            EditableProfileField(stringResource(R.string.Name), firstName, isEditMode, isEditMode) { firstName = it }
            Spacer(Modifier.height(12.dp))
            EditableProfileField(stringResource(R.string.Last_Name), lastName, isEditMode, isEditMode) { lastName = it }
            Spacer(Modifier.height(12.dp))
            EditableProfileField(stringResource(R.string.Address), address, isEditMode, isEditMode) { address = it }
            Spacer(Modifier.height(12.dp))
            EditableProfileField(stringResource(R.string.phone_number), phone, isEditMode, isEditMode) { phone = it }

            if (isEditMode) {
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Accent)
                        .clickable {
                            vm.save(firstName, lastName, address, phone)
                            isEditMode = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.Save), fontSize = 14.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditableProfileField(
    label: String,
    value: String,
    enabled: Boolean,
    showCheck: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                if (showCheck) {
                    Image(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Ok",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
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

@Preview()
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
