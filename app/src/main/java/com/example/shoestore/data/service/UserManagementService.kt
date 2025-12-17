package com.example.shoestore.data.service

import com.example.shoestore.data.model.ChangePasswordRequest
import com.example.shoestore.data.model.ChangePasswordResponse
import com.example.shoestore.data.model.ForgotPasswordRequest
import com.example.shoestore.data.model.ForgotPasswordResponse
import com.example.shoestore.data.model.SignInRequest
import com.example.shoestore.data.model.SignInResponse
import com.example.shoestore.data.model.SignUpRequest
import com.example.shoestore.data.model.SignUpResponse
import com.example.shoestore.data.model.VerifyOtpRequest
import com.example.shoestore.data.model.VerifyOtpResponse
import com.example.shoestore.data.model.VerifyRecoveryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT


const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bnBqaWpjZHduY2RiZ3p2YnhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU4NzYyOTYsImV4cCI6MjA4MTQ1MjI5Nn0.nkqKnReUN5rtiGEE6r9sBiMb0QOh5b6be2fU3TNIW6g"
interface UserManagementService {

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/verify")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("auth/verify-recovery-otp")
    suspend fun verifyRecoveryOtp(@Body request: VerifyOtpRequest): Response<VerifyRecoveryResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

}