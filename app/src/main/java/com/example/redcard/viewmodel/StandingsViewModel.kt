package com.example.redcard.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcard.model.TableEntry
import com.example.redcard.repository.StandingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LeagueStandingsUiState {
    data class Success(val standingsTable: List<TableEntry>) : LeagueStandingsUiState
    data class Error(val message: String) : LeagueStandingsUiState
    object Loading : LeagueStandingsUiState
}


class StandingsViewModel(
    private val repository: StandingsRepository = StandingsRepository()
) : ViewModel() {

    private val _laLigaStandingsStateFlow = MutableStateFlow<LeagueStandingsUiState>(LeagueStandingsUiState.Loading)
    val laLigaStandings: StateFlow<LeagueStandingsUiState> = _laLigaStandingsStateFlow.asStateFlow()

    private val _premierLeagueStandingsStateFlow = MutableStateFlow<LeagueStandingsUiState>(LeagueStandingsUiState.Loading)
    val premierLeagueStandings: StateFlow<LeagueStandingsUiState> = _premierLeagueStandingsStateFlow.asStateFlow()

    private val _bundesligaStandingsStateFlow = MutableStateFlow<LeagueStandingsUiState>(LeagueStandingsUiState.Loading)
    val bundesligaStandings: StateFlow<LeagueStandingsUiState> = _bundesligaStandingsStateFlow.asStateFlow()


    companion object {
        const val LA_LIGA_CODE = "PD"
        const val PREMIER_LEAGUE_CODE = "PL"
        const val BUNDESLIGA_CODE = "BL1"
        private const val TAG = "StandingsVM"
    }

    init {
        Log.d(TAG, "ViewModel initialized.")
//        fetchStandingsForLeague(BUNDESLIGA_CODE)
         fetchStandingsForLeague(LA_LIGA_CODE)
         fetchStandingsForLeague(PREMIER_LEAGUE_CODE)
    }

    fun fetchStandingsForLeague(leagueCode: String) {
        Log.d(TAG, "Attempting to fetch standings for $leagueCode")
        viewModelScope.launch {
            val targetStateFlow = when (leagueCode) {
                LA_LIGA_CODE -> _laLigaStandingsStateFlow
                PREMIER_LEAGUE_CODE -> _premierLeagueStandingsStateFlow
                BUNDESLIGA_CODE -> _bundesligaStandingsStateFlow
                else -> {
                    Log.e(TAG, "Unknown league code: $leagueCode")
                    return@launch
                }
            }
            targetStateFlow.value = LeagueStandingsUiState.Loading
            Log.d(TAG, "Set $leagueCode to Loading state.")

            try {
                Log.d(TAG, "Calling repository for $leagueCode.")
                val response = repository.fetchLeagueStandings(leagueCode)
                Log.d(TAG, "Response for $leagueCode: Code=${response.code()}, Successful=${response.isSuccessful}, Message=${response.message()}, Body Present=${response.body() != null}")

                if (response.isSuccessful) {
                    val standingsResponse = response.body()
                    val totalStandings = standingsResponse?.standings?.find { it.type == "TOTAL" }
                    if (totalStandings != null && totalStandings.table.isNotEmpty()) {
                        Log.i(TAG, "Success for $leagueCode. Found ${totalStandings.table.size} entries.")
                        targetStateFlow.value = LeagueStandingsUiState.Success(totalStandings.table)
                    } else {
                        val reason = when {
                            standingsResponse == null -> "Response body is null."
                            standingsResponse.standings.isEmpty() -> "Standings list is empty."
                            totalStandings == null -> "TOTAL type standing not found."
                            totalStandings.table.isEmpty() -> "Table in TOTAL standing is empty."
                            else -> "Unknown reason."
                        }
                        Log.w(TAG, "No standings data found in response for $leagueCode. Reason: $reason")
                        targetStateFlow.value = LeagueStandingsUiState.Error("No standings data found for $leagueCode. $reason")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e(TAG, "API Error for $leagueCode: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    targetStateFlow.value = LeagueStandingsUiState.Error("API Error ${response.code()}: ${response.message()} for $leagueCode. Details: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network/Conversion Exception for $leagueCode: ${e.message}", e)
                targetStateFlow.value = LeagueStandingsUiState.Error("Network Error: ${e.localizedMessage ?: "Unknown error"} for $leagueCode. Exception: ${e.javaClass.simpleName}")
            }
        }
    }
}