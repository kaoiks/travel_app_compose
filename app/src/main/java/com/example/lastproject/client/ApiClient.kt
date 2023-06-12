package com.example.projectapp.client


import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

object ApiClient {
    private lateinit var apiService: ApiService
    private lateinit var jwtToken: String
    private data class RegistrationData(
        val username: String,
        val email: String,
        val password: String
    )

    private data class RegistrationResponse(val email: String, val username: String)

    private data class LoginData(val username: String, val password: String)
    private data class LoginResponse(val access: String, val refresh: String)

    data class Ticket(
        val id: Int,
        val name: String,
        val file_field: String?,
        val travel_date: String,
        val start_location: String?,
        val end_location: String?
    )

    class AuthInterceptor(private val authToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val requestBuilder = chain.request().newBuilder()
                .header("Authorization", "Bearer $authToken")
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }

    private var retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.117:8000") // Replace with your API URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private interface ApiService {
        @POST("auth/register/")
        suspend fun registerUser(@Body registrationData: RegistrationData): Response<RegistrationResponse>

        @POST("auth/login/")
        suspend fun loginUser(@Body loginData: LoginData): Response<LoginResponse>

        @GET("api/tickets")
        suspend fun getTickets(@Header("Authorization") token: String): Response<List<Ticket>>

    }

    init {
        createApiService()
    }

    fun setClientsJwt(token: String){
        jwtToken = token
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.117:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(token))
                    .build()
            )
            .build()
    }

    private fun createApiService() {
        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        val registrationData =
            RegistrationData(username = username, email = email, password = password)
        val response = apiService.registerUser(registrationData)
        return response.isSuccessful
    }

    suspend fun loginUser(context: Context, username: String, password: String): Boolean {
        val loginData = LoginData(username = username, password = password)
        val response = apiService.loginUser(loginData)
        val responseData = response.body()
        if (responseData != null) {
            TokenManager.saveRefreshToken(context, responseData.refresh)
            TokenManager.saveAccessToken(context, responseData.access)
        }
        return response.isSuccessful
    }

    suspend fun getTickets(jwt: String): List<Ticket> {

        val response = apiService.getTickets("Bearer $jwt")
        return response.body()!!
    }


}