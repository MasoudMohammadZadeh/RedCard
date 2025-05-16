package com.example.redcard.model

data class MatchesResponse(
    val filters: Filters?,
    val resultSet: ResultSet?,
    val competition: Competition?,
    val matches: List<Match>
)

data class Filters(
    val dateFrom: String?,
    val dateTo: String?,
    val permission: String?
)

data class ResultSet(
    val count: Int?,
    val first: String?,
    val last: String?,
    val played: Int?
)

data class Match(
    val area: Area?,
    val competition: Competition?,
    val season: Season?,
    val id: Int,
    val utcDate: String,
    val status: String, //  SCHEDULED, LIVE, IN_PLAY, PAUSED, FINISHED, POSTPONED, SUSPENDED, CANCELED
    val minute: Int?,
    val injuryTime: Int?,
    val attendance: Int?,
    val venue: String?,
    val matchday: Int?,
    val stage: String?,
    val group: String?,
    val lastUpdated: String?,
    val homeTeam: TeamScore,
    val awayTeam: TeamScore,
    val score: Score,
    val goals: List<Goal>?,
    val penalties: List<Penalty>?,
    val bookings: List<Booking>?,
    val substitutions: List<Substitution>?,
    val odds: Odds?,
    val referees: List<Referee>?
)

data class TeamInMatch(
    val id: Int,
    val name: String,
    val shortName: String?,
    val tla: String?,
    val crest: String?
)

data class TeamScore(
    val id: Int,
    val name: String,
    val shortName: String?,
    val tla: String?,
    val crest: String?,
    val goals: Int?
)


data class Score(
    val winner: String?, // HOME_TEAM, AWAY_TEAM, DRAW
    val duration: String?, // REGULAR, EXTRA_TIME, PENALTY_SHOOTOUT
    val fullTime: TimeScore,
    val halfTime: TimeScore?,
    val regularTime: TimeScore?,
    val extraTime: TimeScore?,
    val penalties: TimeScore?
)

data class TimeScore(
    val home: Int?,
    val away: Int?
)

data class Goal(
    val minute: Int?,
    val injuryTime: Int?,
    val type: String?, // REGULAR, OWN, PENALTY
    val team: TeamIdName?,
    val scorer: Player?,
    val assist: Player?
)

data class Penalty(
    val player: Player?,
    val team: TeamIdName?,
    val scored: Boolean?
)


data class Area(
    val id: Int?,
    val name: String?,
    val code: String?,
    val flag: String?
)

data class Player(
    val id: Int?,
    val name: String?,
    val position: String?,
    val dateOfBirth: String?,
    val nationality: String?
)

data class TeamIdName(
    val id: Int?,
    val name: String?
)

data class Booking(
    val minute: Int?,
    val team: TeamIdName?,
    val player: Player?,
    val card: String? // YELLOW, RED, YELLOW_RED
)

data class Substitution(
    val minute: Int?,
    val team: TeamIdName?,
    val playerOut: Player?,
    val playerIn: Player?
)

data class Odds(
    val homeWin: Float?,
    val draw: Float?,
    val awayWin: Float?
)

data class Referee(
    val id: Int?,
    val name: String?,
    val type: String?, // REFEREE, ASSISTANT_REFEREE_N1, ASSISTANT_REFEREE_N2, FOURTH_OFFICIAL, VIDEO_ASSISANT_REFEREE_N1, VIDEO_ASSISANT_REFEREE_N2
    val nationality: String?
)