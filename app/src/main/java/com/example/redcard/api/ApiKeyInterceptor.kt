package com.example.redcard.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
// import java.io.IOException

class ApiKeyInterceptor : Interceptor {
    private var currentApiKeyIndex = 0
    private var keysTriedInCurrentCycle = mutableSetOf<Int>()

    companion object {
        private const val TAG = "ApiKeyInterceptor"
    }

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        if (Credentials.API_KEYS.isEmpty()) {
            Log.w(TAG, "No API keys provided. Proceeding without X-Auth-Token.")
            return chain.proceed(chain.request())
        }

        var attempt = 0
        val maxAttempts = Credentials.API_KEYS.size
        var localCurrentApiKeyIndex = currentApiKeyIndex

        while (attempt < maxAttempts) {
            if (keysTriedInCurrentCycle.contains(localCurrentApiKeyIndex) && keysTriedInCurrentCycle.size == Credentials.API_KEYS.size) {
                Log.w(TAG, "All API keys have been tried and failed in the current cycle for ${chain.request().url}. Breaking retry loop.")
                break
            }


            val apiKey = Credentials.API_KEYS[localCurrentApiKeyIndex]
            Log.d(TAG, "Attempt ${attempt + 1}/$maxAttempts: Trying API Key ending with: ...${apiKey.takeLast(5)} (Index: $localCurrentApiKeyIndex) for URL: ${chain.request().url}")

            val requestWithKey = chain.request().newBuilder()
                .header(Credentials.API_KEY_HEADER, apiKey)
                .build()

            val response = chain.proceed(requestWithKey)
            Log.d(TAG, "Response for ${requestWithKey.url} with key ...${apiKey.takeLast(5)} (Index: $localCurrentApiKeyIndex): ${response.code} ${response.message}")
            keysTriedInCurrentCycle.add(localCurrentApiKeyIndex) // این کلید امتحان شد

            // 401: Unauthorized (usually bad key)
            // 403: Forbidden (key valid, but no permission for this resource/tier)
            // 429: Too Many Requests (rate limit exceeded for this key)
            if (response.code == 401 || response.code == 403 || response.code == 429) {
                Log.w(TAG, "Received ${response.code} for key ...${apiKey.takeLast(5)} (Index: $localCurrentApiKeyIndex). Trying next key.")
                response.close()

                localCurrentApiKeyIndex = (localCurrentApiKeyIndex + 1) % Credentials.API_KEYS.size
                currentApiKeyIndex = localCurrentApiKeyIndex
                attempt++

                if (attempt >= maxAttempts || keysTriedInCurrentCycle.size == Credentials.API_KEYS.size) {
                    Log.w(TAG, "All API keys tried or max attempts reached for ${chain.request().url}. Returning last unsuccessful response.")
                    return response
                }
            } else {
                Log.i(TAG, "Response successful or non-key related error (${response.code}) for key ...${apiKey.takeLast(5)}. Returning response.")
                keysTriedInCurrentCycle.clear()
                return response
            }
        }
        Log.e(TAG, "Exited retry loop unexpectedly for ${chain.request().url}. This should not happen. Returning a generic error or last response if available.")

        throw IllegalStateException("ApiKeyInterceptor: Should have returned a response from within the loop.")
    }
}