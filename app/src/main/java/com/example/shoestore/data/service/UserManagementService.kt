package com.example.shoestore.data.service

import com.example.shoestore.data.model.ChangePasswordRequest
import com.example.shoestore.data.model.ForgotPasswordRequest
import com.example.shoestore.data.model.SignInRequest
import com.example.shoestore.data.model.SignUpRequest
import com.example.shoestore.data.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

// Убедитесь, что здесь ваш актуальный Supabase Anon Key
const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bnBqaWpjZHduY2RiZ3p2YnhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU4NzYyOTYsImV4cCI6MjA4MTQ1MjI5Nn0.nkqKnReUN5rtiGEE6r9sBiMb0QOh5b6be2fU3TNIW6g"

interface UserManagementService {

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<Any>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<Any>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/verify")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<Any>

    // Используем стандартный эндпоинт verify для восстановления (тип recovery передается в теле)
    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/verify")
    suspend fun verifyRecoveryOtp(@Body request: VerifyOtpRequest): Response<Any>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<Any>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<Any>

    // Новый метод для повторной отправки OTP
    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/resend")
    suspend fun resendOtp(@Body request: VerifyOtpRequest): Response<Any>
}
