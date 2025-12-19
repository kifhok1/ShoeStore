package com.example.shoestore.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempImageUri(): Uri {
    val imageFile = File.createTempFile(
        "profile_${System.currentTimeMillis()}",
        ".jpg",
        cacheDir
    )

    val authority = "${packageName}.fileprovider"

    return FileProvider.getUriForFile(
        applicationContext,
        authority,
        imageFile
    )
}