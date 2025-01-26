package com.example.formulariokotlin.core.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.example.formulariokotlin.features.login.data.models.LoginResponse
import com.example.formulariokotlin.features.register.data.models.RegisterResponse
import com.example.formulariokotlin.features.tasks.data.models.Task

class ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://192.168.100.17:3000"

    suspend fun login(email: String, password: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$baseUrl/api/login")
                    .post(requestBody)
                    .build()

                withTimeout(5000L) {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: throw Exception("Empty response")
                    val jsonResponse = JSONObject(responseBody)

                    if (response.isSuccessful) {
                        Result.success(
                            LoginResponse(
                                success = jsonResponse.optBoolean("success", false),
                                message = jsonResponse.optString("message", "No message"),
                                token = jsonResponse.optString("token", null)
                            )
                        )
                    } else {
                        Result.failure(Exception(jsonResponse.optString("error", "Unknown error")))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> =
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("name", name)
                    put("email", email)
                    put("password", password)
                }
                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$baseUrl/api/register")
                    .post(requestBody)
                    .build()

                withTimeout(5000L) {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: throw Exception("Empty response")
                    val jsonResponse = JSONObject(responseBody)

                    if (response.isSuccessful) {
                        Result.success(
                            RegisterResponse(
                                success = jsonResponse.optBoolean("success", false),
                                message = jsonResponse.optString("message", "No message")
                            )
                        )
                    } else {
                        Result.failure(Exception(jsonResponse.optString("error", "Unknown error")))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAllTasks(token: String): Result<List<Task>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tasks")
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            withTimeout(5000L) {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: throw Exception("Empty response")
                val json = JSONObject(body)

                if (response.isSuccessful) {
                    val tasksJson = json.optJSONArray("tasks") ?: return@withTimeout Result.success(emptyList())
                    val tasks = mutableListOf<Task>()
                    for (i in 0 until tasksJson.length()) {
                        val t = tasksJson.getJSONObject(i)
                        tasks.add(
                            Task(
                                id = t.getInt("id"),
                                title = t.getString("title"),
                                content = t.getString("content")
                            )
                        )
                    }
                    Result.success(tasks)
                } else {
                    Result.failure(Exception(json.optString("error", "Unknown error")))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(token: String, title: String, content: String): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("title", title)
                put("content", content)
            }
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/tasks")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            withTimeout(5000L) {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: throw Exception("Empty response")
                val json = JSONObject(body)

                if (response.isSuccessful) {
                    val taskJson = json.getJSONObject("task")
                    val task = Task(
                        id = taskJson.getInt("id"),
                        title = taskJson.getString("title"),
                        content = taskJson.getString("content")
                    )
                    Result.success(task)
                } else {
                    Result.failure(Exception(json.optString("error", "Unknown error")))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(token: String, taskId: Int, title: String, content: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("title", title)
                put("content", content)
            }
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/tasks/$taskId")
                .patch(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            withTimeout(5000L) {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: throw Exception("Empty response")
                val json = JSONObject(body)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(json.optString("error", "Unknown error")))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(token: String, taskId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tasks/$taskId")
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            withTimeout(5000L) {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: throw Exception("Empty response")
                val json = JSONObject(body)
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(json.optString("error", "Unknown error")))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
