package com.example.redcard.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcard.model.Match
import com.example.redcard.repository.StandingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed interface MatchesUiState {
    data class Success(val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
    object Loading : MatchesUiState
}

class LiveScoreViewModel(
    private val repository: StandingsRepository = StandingsRepository()
) : ViewModel() {

    private val _laLigaMatchesStateFlow = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val laLigaMatches: StateFlow<MatchesUiState> = _laLigaMatchesStateFlow.asStateFlow()

     private val _premierLeagueMatchesStateFlow = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
     val premierLeagueMatches: StateFlow<MatchesUiState> = _premierLeagueMatchesStateFlow.asStateFlow()

    private val _upcomingMatchesStateFlow = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val upcomingMatches: StateFlow<MatchesUiState> = _upcomingMatchesStateFlow.asStateFlow()


    companion object {
        private const val TAG = "LiveScoreVM"
        const val LA_LIGA_CODE = "PD"
        const val PREMIER_LEAGUE_CODE = "PL"
        const val BUNDESLIGA_CODE = "BL1"

    }

    init {
        Log.d(TAG, "ViewModel initialized.")
        fetchWeeklyMatchesForLeague(LA_LIGA_CODE)
        fetchWeeklyMatchesForLeague(PREMIER_LEAGUE_CODE)

        fetchUpcomingMatchesForLeagues(listOf(LA_LIGA_CODE, PREMIER_LEAGUE_CODE, BUNDESLIGA_CODE))

    }

    private fun getUpcomingMonthDateRange(): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // تاریخ شروع از امروز
        val dateFrom = sdf.format(calendar.time)
        // تاریخ پایان تا یک ماه دیگر
        calendar.add(Calendar.MONTH, 1)
        val dateTo = sdf.format(calendar.time)
        Log.d(TAG, "Fetching upcoming matches from $dateFrom to $dateTo")
        return Pair(dateFrom, dateTo)
    }

    fun fetchUpcomingMatchesForLeagues(leagueCodes: List<String>) {
        Log.d(TAG, "Attempting to fetch upcoming matches for leagues: $leagueCodes")
        viewModelScope.launch {
            _upcomingMatchesStateFlow.value = MatchesUiState.Loading
            Log.d(TAG, "Set upcoming matches to Loading state.")

            val (dateFrom, dateTo) = getUpcomingMonthDateRange()
            val allFetchedMatches = mutableListOf<Match>()
            var hasError = false
            var errorMessage = ""

            try {
                // واکشی موازی برای هر لیگ
                val deferredResults = leagueCodes.map { leagueCode ->
                    async {
                        Log.d(TAG, "Calling repository for upcoming $leagueCode matches ($dateFrom to $dateTo).")
                        repository.fetchMatchesForLeague(leagueCode, dateFrom, dateTo)
                    }
                }

                // منتظر ماندن برای نتایج همه واکشی‌ها
                val responses = deferredResults.awaitAll()

                responses.forEachIndexed { index, response ->
                    val leagueCode = leagueCodes[index]
                    Log.d(TAG, "Upcoming Matches Response for $leagueCode: Code=${response.code()}, Successful=${response.isSuccessful}, Message=${response.message()}, Body Present=${response.body() != null}")
                    if (response.isSuccessful) {
                        response.body()?.matches?.let { matches ->
                            allFetchedMatches.addAll(matches)
                            Log.i(TAG, "Success for upcoming $leagueCode matches. Found ${matches.size} matches.")
                        }
                    } else {
                        hasError = true
                        val errorBody = response.errorBody()?.string() ?: "No error body"
                        errorMessage += "API Error for $leagueCode: ${response.code()} - ${response.message()}. Details: $errorBody\n"
                        Log.e(TAG, "API Error for upcoming $leagueCode matches: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    }
                }

                if (hasError) {
                    _upcomingMatchesStateFlow.value = MatchesUiState.Error(errorMessage.trim())
                } else if (allFetchedMatches.isEmpty()) {
                    Log.w(TAG, "No upcoming match data found in response for any league.")
                    _upcomingMatchesStateFlow.value = MatchesUiState.Error("No upcoming match data found.")
                } else {
                    // مرتب سازی بر اساس تاریخ
                    val sortedMatches = allFetchedMatches.sortedBy { it.utcDate }
                    Log.i(TAG, "Total upcoming matches fetched and sorted: ${sortedMatches.size}")
                    _upcomingMatchesStateFlow.value = MatchesUiState.Success(sortedMatches)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Network/Conversion Exception for upcoming matches: ${e.message}", e)
                _upcomingMatchesStateFlow.value = MatchesUiState.Error("Network Error: ${e.localizedMessage ?: "Unknown error"}. Exception: ${e.javaClass.simpleName}")
            }
        }
    }



    private fun getCurrentWeekDates(): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val dateFrom = sdf.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val dateTo = sdf.format(calendar.time)

        Log.d(TAG, "Fetching matches from $dateFrom to $dateTo")
        return Pair(dateFrom, dateTo)
    }

    fun fetchWeeklyMatchesForLeague(leagueCode: String) {
        Log.d(TAG, "Attempting to fetch weekly matches for $leagueCode")
        viewModelScope.launch {
            val targetStateFlow = when (leagueCode) {
                LA_LIGA_CODE -> _laLigaMatchesStateFlow
                 PREMIER_LEAGUE_CODE -> _premierLeagueMatchesStateFlow
                else -> {
                    Log.e(TAG, "Unknown league code for matches: $leagueCode")
                    return@launch
                }
            }
            targetStateFlow.value = MatchesUiState.Loading
            Log.d(TAG, "Set $leagueCode matches to Loading state.")

            val (dateFrom, dateTo) = getCurrentWeekDates()

            try {
                Log.d(TAG, "Calling repository for $leagueCode matches ($dateFrom to $dateTo).")
                val response = repository.fetchMatchesForLeague(leagueCode, dateFrom, dateTo)
                Log.d(TAG, "Matches Response for $leagueCode: Code=${response.code()}, Successful=${response.isSuccessful}, Message=${response.message()}, Body Present=${response.body() != null}")

                if (response.isSuccessful) {
                    val matchesResponse = response.body()
                    if (matchesResponse != null && matchesResponse.matches.isNotEmpty()) {
                        Log.i(TAG, "Success for $leagueCode matches. Found ${matchesResponse.matches.size} matches.")
                        targetStateFlow.value = MatchesUiState.Success(matchesResponse.matches)
                    } else {
                        val reason = if (matchesResponse == null) "Response body is null." else "Matches list is empty or null."
                        Log.w(TAG, "No match data found in response for $leagueCode. Reason: $reason")
                        targetStateFlow.value = MatchesUiState.Error("No match data found for $leagueCode. $reason")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e(TAG, "API Error for $leagueCode matches: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    targetStateFlow.value = MatchesUiState.Error("API Error ${response.code()}: ${response.message()} for $leagueCode matches. Details: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network/Conversion Exception for $leagueCode matches: ${e.message}", e)
                targetStateFlow.value = MatchesUiState.Error("Network Error: ${e.localizedMessage ?: "Unknown error"} for $leagueCode matches. Exception: ${e.javaClass.simpleName}")
            }
        }
    }
}


