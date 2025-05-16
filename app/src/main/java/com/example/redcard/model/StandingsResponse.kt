package com.example.redcard.model

data class StandingsResponse(
    val competition: Competition,
    val season: Season,
    val standings: List<Standing>
)

data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String
)

data class Season(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int?,
    val winner: Team?
)

data class Standing(
    val stage: String,
    val type: String,
    val group: String?,
    val table: List<TableEntry>
)

data class TableEntry(
    val position: Int,
    val team: Team,
    val playedGames: Int,
    val form: String?,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int
)

data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String
)

data class TeamStanding(
    val team: Team,
    val draw: Int,
    val lost: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,
    val logoUrl: String
)


data class StandingsResponse2(
    val standings: List<Standing>
)
