package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Production-ready Supabase Client for NEXCHAT using OkHttp and REST/PostgREST.
 * Directly integrates with the user's provisioned Supabase instance:
 * Project URL: https://refaejedfgunfioxhdzu.supabase.co
 */
object SupabaseClient {
    private const val TAG = "SupabaseClient"

    const val supabaseUrl: String = "https://refaejedfgunfioxhdzu.supabase.co"
    const val supabaseAnonKey: String = "sb_publishable_V4jW9ynXUS_LCj7b3Xvoig_W2oY2jo3"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    data class SupabaseConnectionStatus(
        val isConnected: Boolean,
        val endpoint: String,
        val message: String,
        val latencyMs: Long = 0
    )

    /**
     * Tests connectivity to the live Supabase instance by pinging PostgREST root schema.
     */
    suspend fun testConnection(): SupabaseConnectionStatus = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val url = "$supabaseUrl/rest/v1/"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - start
                if (response.isSuccessful || response.code == 404 || response.code == 200 || response.code == 401) {
                    SupabaseConnectionStatus(
                        isConnected = true,
                        endpoint = supabaseUrl,
                        message = "Connected to Supabase (HTTP ${response.code})",
                        latencyMs = duration
                    )
                } else {
                    SupabaseConnectionStatus(
                        isConnected = false,
                        endpoint = supabaseUrl,
                        message = "Supabase returned HTTP ${response.code}: ${response.message}",
                        latencyMs = duration
                    )
                }
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - start
            Log.e(TAG, "Connection test failed", e)
            SupabaseConnectionStatus(
                isConnected = false,
                endpoint = supabaseUrl,
                message = e.localizedMessage ?: "Network connection failed",
                latencyMs = duration
            )
        }
    }

    /**
     * PostgREST query helper to fetch rows from a table.
     */
    suspend fun queryTable(
        table: String,
        select: String = "*",
        limit: Int = 20
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$supabaseUrl/rest/v1/$table?select=$select&limit=$limit"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    Result.success(body)
                } else {
                    Result.failure(IOException("HTTP ${response.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * PostgREST insert helper to insert a row into a table.
     */
    suspend fun insertRow(
        table: String,
        jsonBody: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$supabaseUrl/rest/v1/$table"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(jsonBody.toRequestBody(jsonMediaType))
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    Result.success(body)
                } else {
                    Result.failure(IOException("HTTP ${response.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
