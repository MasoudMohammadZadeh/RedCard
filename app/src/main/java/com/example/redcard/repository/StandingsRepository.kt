package com.example.redcard.repository

import com.example.redcard.api.FootballApi // Change this
import com.example.redcard.api.FootballClient
import com.example.redcard.model.MatchesResponse
import com.example.redcard.model.StandingsResponse
import retrofit2.Response

// You can inject FootballApi if you use a DI framework like Hilt
// For simplicity, we'll access it directly from FootballClient
class StandingsRepository(private val footballApi: FootballApi = FootballClient.api) {

    suspend fun fetchLeagueStandings(leagueCode: String): Response<StandingsResponse> {
        return footballApi.getStandings(leagueCode)
    }

    suspend fun fetchMatchesForLeague(
        leagueCode: String,
        dateFrom: String,
        dateTo: String
    ): Response<MatchesResponse> {
        return footballApi.getMatches(leagueCode, dateFrom, dateTo)
    }

}