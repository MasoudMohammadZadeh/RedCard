package com.example.redcard.api

import com.example.redcard.model.MatchesResponse
import com.example.redcard.model.StandingsResponse
import retrofit2.Response
import retrofit2.http.GET
// import retrofit2.http.Headers // No longer needed here
import retrofit2.http.Path
import retrofit2.http.Query

interface FootballApi {

    // @Headers("X-Auth-Token: ${Credentials.API_KEY_VALUE1}") // REMOVE THIS LINE
    @GET("competitions/{leagueCode}/standings")
    suspend fun getStandings(
        @Path("leagueCode") leagueCode: String
    ): Response<StandingsResponse>


    @GET("competitions/{leagueCode}/matches")
    suspend fun getMatches(
        @Path("leagueCode") leagueCode: String,
        @Query("dateFrom") dateFrom: String, // YYYY-MM-DD
        @Query("dateTo") dateTo: String      // YYYY-MM-DD
    ): Response<MatchesResponse>


}