package com.example.lastproject.client


import TokenManager
import android.content.Context
import android.net.Uri
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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
    private data class AddTicketResponse(val success: String)

    data class Ticket(
        val id: Int,
        val name: String,
        val file_field: String?,
        val travel_date: String,
        val start_location: Location?,
        val end_location: Location?
    )

    data class TicketPost(
        val name: String,
        val travel_date: String,
        val start_location: Location?,
        val end_location: Location?
    )

    data class Location(
        val name: String,
        val latitude: String,
        val longitude: String
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

        @POST("api/tickets/")
        suspend fun addTicket(
            @Header("Authorization") token: String,
            @Body ticketPost: TicketPost
        ): Response<Ticket>

        @Multipart
        @POST("/api/tickets/{ticketId}/set_file/")
        suspend fun uploadFile(
            @Header("Authorization") token: String,
            @Path("ticketId") ticketId: Int,
            @Part file: MultipartBody.Part
        ): Response<AddTicketResponse>

        @GET("api/tickets/{ticketId}/")
        suspend fun getTicket(@Header("Authorization") token: String,
                               @Path("ticketId") ticketId: Int): Response<Ticket>


    }

    init {
        createApiService()
    }

    fun setClientsJwt(token: String) {
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

    suspend fun getTicket(context: Context, ticketId: Int): Ticket{
        val accessToken = TokenManager.getAccessToken(context) ?: ""
        val response = apiService.getTicket("Bearer $accessToken", ticketId)
        return response.body()!!
    }

    fun fetchLocationInfo(latitude: Double, longitude: Double): String? {
        val apiKey = "AIzaSyCghuAC_LCbKmjCS3l2oFX4NU8_2v6fEv0" // Replace with your actual API key
        val url =
            URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey")

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(response)

            val formattedAddress = jsonObject.getJSONArray("results")
                .getJSONObject(0)
                .getString("formatted_address")
            return formattedAddress
        }

        return null
    }


    suspend fun addTicket(context: Context, ticketPost: TicketPost): Int {
        return try {
            val accessToken = TokenManager.getAccessToken(context) ?: ""
            val response = apiService.addTicket("Bearer $accessToken", ticketPost)
            response.body()!!.id

        } catch (e: Exception) {
            -1
        }
    }

    suspend fun addFileToTicket(context: Context, ticketId: Int, filePath: String): Boolean {
        val accessToken = TokenManager.getAccessToken(context) ?: ""
        val uri = Uri.parse(filePath)
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_file.pdf")

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = RequestBody.create(MediaType.parse("application/pdf"), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        try {
            val response = apiService.uploadFile("Bearer $accessToken", ticketId, filePart)
            if (response.isSuccessful) {
            } else {
            }
        } catch (e: Exception) {
            println(e)

        }
        return true
    }


}