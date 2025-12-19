package com.example.shoestore.ui.theme.view

import android.Manifest
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shoestore.R
import com.example.shoestore.data.createTempImageUri
import com.example.shoestore.data.model.FieldType
import com.example.shoestore.data.model.ProfileState
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.EditableProfileField
import com.example.shoestore.ui.theme.components.MainButton
import com.example.shoestore.ui.theme.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

// Функции валидации
fun isValidName(name: String): Boolean {
    return name.isNotEmpty() && name.length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
}

fun isValidPhone(phone: String): Boolean {
    val digitsOnly = phone.filter { it.isDigit() }
    return digitsOnly.length == 11
}

fun isValidAddress(address: String): Boolean {
    return address.isNotEmpty() && address.length >= 5
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    var isEditMode by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Проверка валидности всех полей
    val isFormValid = remember(firstName, lastName, address, phone) {
        isValidName(firstName) &&
                isValidName(lastName) &&
                isValidAddress(address) &&
                isValidPhone(phone)
    }

    val context = LocalContext.current
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val vm: ProfileViewModel = viewModel()
    val profileState by vm.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
        }
    )

    // Обработка состояний профиля
    LaunchedEffect(profileState) {
        when (val state = profileState) {
            is ProfileState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
            }
            is ProfileState.Ready -> {
                val p = state.profile
                firstName = p.firstname.orEmpty()
                lastName = p.lastname.orEmpty()
                address = p.address.orEmpty()
                phone = p.phone.orEmpty()
            }
            is ProfileState.Saving -> {
                val p = state.profile
                firstName = p.firstname.orEmpty()
                lastName = p.lastname.orEmpty()
                address = p.address.orEmpty()
                phone = p.phone.orEmpty()
            }
            else -> {}
        }
    }

    val fullName by remember(firstName, lastName) {
        mutableStateOf(
            listOf(firstName.trim(), lastName.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
                .ifEmpty { "Пользователь" }
        )
    }

    // Проверяем состояние загрузки/сохранения
    val isLoading = profileState is ProfileState.Loading
    val isSaving = profileState is ProfileState.Saving

    Box(modifier = modifier.fillMaxSize()) {
        // Показываем загрузку при Loading состоянии
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.background),
                contentAlignment = Center
            ) {
                CircularProgressIndicator(color = CustomTheme.colors.accent)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 62.dp)
                    .background(CustomTheme.colors.background)
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
                            style = CustomTheme.typography.HeadingSemiBold16,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isEditMode && !isFormValid)
                                        CustomTheme.colors.accent.copy(alpha = 0.5f)
                                    else
                                        CustomTheme.colors.accent
                                )
                                .clickable(enabled = !isEditMode || isFormValid) {
                                    if (!isEditMode || isFormValid) {
                                        isEditMode = !isEditMode
                                    }
                                },
                            contentAlignment = Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(16.dp)
                                    .alpha(if (isEditMode && !isFormValid) 0.5f else 1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ===== Avatar =====
                    val profile = (profileState as? ProfileState.Ready)?.profile
                        ?: (profileState as? ProfileState.Saving)?.profile

                    val avatarModel: Any = avatarUri ?: (profile?.photo ?: R.drawable.profile)

                    AsyncImage(
                        model = avatarModel,
                        contentDescription = "Avatar",
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
                        style = CustomTheme.typography.BodyRegular20,
                        color = CustomTheme.colors.text
                    )

                    if (isEditMode) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.Change_Profile_Picture),
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

                    if (!isEditMode) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(65.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CustomTheme.colors.block),
                            contentAlignment = Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.shtrih),
                                contentDescription = "Barcode",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp)
                                    .height(49.dp),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                    } else {
                        Spacer(Modifier.height(10.dp))
                    }

                    EditableProfileField(
                        label = stringResource(R.string.Name),
                        value = firstName,
                        enabled = isEditMode,
                        fieldType = FieldType.FIRST_NAME,
                        onValueChange = { firstName = it }
                    )
                    Spacer(Modifier.height(12.dp))

                    EditableProfileField(
                        label = stringResource(R.string.Last_Name),
                        value = lastName,
                        enabled = isEditMode,
                        fieldType = FieldType.LAST_NAME,
                        onValueChange = { lastName = it }
                    )
                    Spacer(Modifier.height(12.dp))

                    EditableProfileField(
                        label = stringResource(R.string.Address),
                        value = address,
                        enabled = isEditMode,
                        fieldType = FieldType.ADDRESS,
                        onValueChange = { address = it }
                    )
                    Spacer(Modifier.height(12.dp))

                    EditableProfileField(
                        label = stringResource(R.string.phone_number),
                        value = phone,
                        enabled = isEditMode,
                        fieldType = FieldType.PHONE,
                        onValueChange = { phone = it }
                    )

                    // Показываем кнопку только если:
                    // 1. Находимся в режиме редактирования
                    // 2. НЕ идет процесс сохранения
                    if (isEditMode) {
                        Spacer(Modifier.height(18.dp))

                        MainButton(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = isFormValid,
                            onClick = {
                                if (isFormValid) {
                                    vm.saveProfile(firstName, lastName, address, phone)
                                    isEditMode = false
                                }
                            },
                            text = stringResource(R.string.save_Now)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}


@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
