

package com.example.redcard.api

object Credentials {
    const val BASE_URL = "https://api.football-data.org/v4/"
    const val API_KEY_HEADER = "X-Auth-Token"
    // Store keys in a list for easier cycling
    val API_KEYS = listOf(
        "d129793f6a2a46d5a250a3e1d1c68b65",
        "326636559b3144e09401a33ffdecc8e2",
        "eef7ff7283ee44e583a3c2cac69c34fd"
        // Add more keys here if you have them
    )
}