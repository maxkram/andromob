package ru.school21.tictactoe.web.module

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.school21.tictactoe.datasource.model.Games
import ru.school21.tictactoe.datasource.model.Users
import ru.school21.tictactoe.datasource.repository.AuthRepository
import ru.school21.tictactoe.web.model.CreateGameRequest
import ru.school21.tictactoe.web.model.GameResponse
import ru.school21.tictactoe.web.model.MoveRequest
import ru.school21.tictactoe.web.model.PlayerResponse
import java.util.Base64
import java.util.UUID

data class SignUpRequest(val login: String, val password: String)

private fun parseBoard(value: String): List<List<String>> =
    value.removePrefix("[[").removeSuffix("]]").split("],[").map { row ->
        row.split(",").map { cell -> cell.trim().removeSurrounding("\"") }
    }

private fun boardToString(board: List<List<String>>): String =
    board.joinToString(
        prefix = "[",
        postfix = "]",
        separator = ","
    ) { row ->
        row.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ","
        ) { cell ->
            "\"$cell\""
        }
    }

private fun gameFromRow(row: ResultRow): GameResponse {
    val players = row[Games.players].split("|").filter { it.isNotBlank() }.map { item ->
        val values = item.split(",", limit = 3)
        PlayerResponse(values[0], values[1], values[2])
    }
    return GameResponse(
        uuid = row[Games.uuid],
        state = row[Games.state],
        currentTurn = row[Games.currentTurn],
        winner = row[Games.winner],
        board = parseBoard(row[Games.board]),
        players = players
    )
}

private fun hasWinner(board: List<List<String>>, mark: String): Boolean {
    for (i in 0..2) {
        if (board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) return true
        if (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark) return true
    }
    return (board[0][0] == mark && board[1][1] == mark && board[2][2] == mark) ||
        (board[0][2] == mark && board[1][1] == mark && board[2][0] == mark)
}

private fun hasEmptyCells(board: List<List<String>>) = board.any { row -> row.any { it.isEmpty() } }

private fun firstEmptyCell(board: List<List<String>>): Pair<Int, Int>? {
    for (row in 0..2) for (col in 0..2) if (board[row][col].isEmpty()) return row to col
    return null
}

fun Application.configureTicTacToe() {
    val databaseUrl = environment.config.property("database.url").getString()
    val databaseUser = environment.config.property("database.user").getString()
    val databasePassword = environment.config.property("database.password").getString()

    Database.connect(databaseUrl, "org.postgresql.Driver", databaseUser, databasePassword)
    transaction { SchemaUtils.create(Users, Games) }

    val auth = AuthRepository()

    install(ContentNegotiation) { gson() }
    install(Authentication) {
        basic("auth-basic") {
            realm = "TicTacToe"
            validate { credentials ->
                val uuid = kotlinx.coroutines.runBlocking { auth.login("${credentials.name}:${credentials.password}") }
                if (uuid != null) UserIdPrincipal(uuid) else null
            }
        }
    }

    routing {
        get("/") { call.respondText("TicTacToe server is running") }

        route("/auth") {
            post("/signup") {
                val request = call.receive<SignUpRequest>()
                if (request.login.isBlank() || request.password.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Login and password are required"))
                    return@post
                }
                if (!auth.register(request.login.trim(), request.password)) {
                    call.respond(HttpStatusCode.Conflict, mapOf("message" to "Login already exists"))
                    return@post
                }
                call.respond(HttpStatusCode.Created)
            }
            post("/signin") {
                val header = call.request.headers[HttpHeaders.Authorization]
                if (header == null || !header.startsWith("Basic ")) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val credentials = runCatching {
                    String(Base64.getDecoder().decode(header.removePrefix("Basic ").trim()), Charsets.UTF_8)
                }.getOrNull()
                val uuid = credentials?.let { auth.login(it) }
                if (uuid == null) call.respond(HttpStatusCode.Unauthorized)
                else call.respond(mapOf("uuid" to uuid, "login" to credentials.substringBefore(":")))
            }
        }

        get("/games") {
            val header = call.request.headers[HttpHeaders.Authorization]
            val credentials = header?.takeIf { it.startsWith("Basic ") }?.let {
                runCatching { String(Base64.getDecoder().decode(it.removePrefix("Basic ").trim()), Charsets.UTF_8) }.getOrNull()
            }
            val userUuid = credentials?.let { auth.login(it) }
            if (userUuid == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val games = transaction {
                Games.select { Games.state eq "WAITING_FOR_PLAYERS" }.map(::gameFromRow)
                    .filter { game -> game.players.none { player -> player.uuid == userUuid } }
            }
            call.respond(HttpStatusCode.OK, games)
        }

        post("/games") {
            val header = call.request.headers[HttpHeaders.Authorization]
            val credentials = header?.takeIf { it.startsWith("Basic ") }?.let {
                runCatching { String(Base64.getDecoder().decode(it.removePrefix("Basic ").trim()), Charsets.UTF_8) }.getOrNull()
            }
            val userUuid = credentials?.let { auth.login(it) }
            if (userUuid == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val request = call.receive<CreateGameRequest>()
            val players = if (request.againstComputer) {
                listOf(PlayerResponse(userUuid, credentials.substringBefore(":"), "X"), PlayerResponse("computer", "Computer", "O"))
            } else {
                listOf(PlayerResponse(userUuid, credentials.substringBefore(":"), "X"))
            }
            val game = GameResponse(
                uuid = UUID.randomUUID().toString(),
                state = if (request.againstComputer) "TURN" else "WAITING_FOR_PLAYERS",
                currentTurn = if (request.againstComputer) userUuid else null,
                winner = null,
                board = List(3) { List(3) { "" } },
                players = players
            )
            transaction {
                Games.insert {
                    it[Games.uuid] = game.uuid
                    it[Games.state] = game.state
                    it[Games.currentTurn] = game.currentTurn
                    it[Games.winner] = game.winner
                    it[Games.board] = boardToString(game.board)
                    it[Games.players] = players.joinToString("|") { p -> "${p.uuid},${p.login},${p.mark}" }
                }
            }
            call.respond(HttpStatusCode.Created, game)
        }

        post("/games/{uuid}/join") {
            val header = call.request.headers[HttpHeaders.Authorization]
            val credentials = header?.takeIf { it.startsWith("Basic ") }?.let {
                runCatching { String(Base64.getDecoder().decode(it.removePrefix("Basic ").trim()), Charsets.UTF_8) }.getOrNull()
            }
            val userUuid = credentials?.let { auth.login(it) }
            if (userUuid == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val gameUuid = call.parameters["uuid"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Game UUID is required"))
                return@post
            }
            val game = transaction { Games.select { Games.uuid eq gameUuid }.singleOrNull()?.let(::gameFromRow) }
            if (game == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Game not found"))
                return@post
            }
            if (game.state != "WAITING_FOR_PLAYERS" || game.players.size != 1) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to "Game is not available to join"))
                return@post
            }
            if (game.players.any { it.uuid == userUuid }) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to "You are already in this game"))
                return@post
            }
            val players = game.players + PlayerResponse(userUuid, credentials.substringBefore(":"), "O")
            val updatedGame = game.copy(state = "TURN", currentTurn = game.players.first().uuid, players = players)
            transaction {
                Games.update({ Games.uuid eq gameUuid }) {
                    it[Games.state] = updatedGame.state
                    it[Games.currentTurn] = updatedGame.currentTurn
                    it[Games.players] = players.joinToString("|") { p -> "${p.uuid},${p.login},${p.mark}" }
                }
            }
            call.respond(HttpStatusCode.OK, updatedGame)
        }

        get("/games/{uuid}") {
            val header = call.request.headers[HttpHeaders.Authorization]
            val credentials = header?.takeIf { it.startsWith("Basic ") }?.let {
                runCatching { String(Base64.getDecoder().decode(it.removePrefix("Basic ").trim()), Charsets.UTF_8) }.getOrNull()
            }
            if (credentials?.let { auth.login(it) } == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val gameUuid = call.parameters["uuid"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Game UUID is required"))
                return@get
            }
            val game = transaction { Games.select { Games.uuid eq gameUuid }.singleOrNull()?.let(::gameFromRow) }
            if (game == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Game not found"))
                return@get
            }
            call.respond(HttpStatusCode.OK, game)
        }

        post("/games/{uuid}/move") {
            val header = call.request.headers[HttpHeaders.Authorization]
            val credentials = header?.takeIf { it.startsWith("Basic ") }?.let {
                runCatching { String(Base64.getDecoder().decode(it.removePrefix("Basic ").trim()), Charsets.UTF_8) }.getOrNull()
            }
            val userUuid = credentials?.let { auth.login(it) }
            if (userUuid == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val gameUuid = call.parameters["uuid"] ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Game UUID is required"))
                return@post
            }
            val move = call.receive<MoveRequest>()
            if (move.row !in 0..2 || move.col !in 0..2) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Row and column must be from 0 to 2"))
                return@post
            }
            val game = transaction { Games.select { Games.uuid eq gameUuid }.singleOrNull()?.let(::gameFromRow) }
            if (game == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Game not found"))
                return@post
            }
            if (game.state != "TURN" || game.currentTurn != userUuid) {
                call.respond(HttpStatusCode.Forbidden, mapOf("message" to "It is not your turn"))
                return@post
            }
            if (game.board[move.row][move.col].isNotEmpty()) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to "Cell is already occupied"))
                return@post
            }
            val player = game.players.firstOrNull { it.uuid == userUuid }
            if (player == null) {
                call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You are not a player in this game"))
                return@post
            }
            val board = game.board.map { it.toMutableList() }.toMutableList()
            board[move.row][move.col] = player.mark
            var stateValue = "TURN"
            var winnerValue: String? = null
            var turnValue: String? = userUuid
            if (hasWinner(board, player.mark)) {
                stateValue = "WIN"; winnerValue = userUuid; turnValue = null
            } else if (!hasEmptyCells(board)) {
                stateValue = "DRAW"; turnValue = null
            } else if (game.players.any { it.uuid == "computer" }) {
                firstEmptyCell(board)?.let { (row, col) -> board[row][col] = "O" }
                if (hasWinner(board, "O")) {
                    stateValue = "WIN"; winnerValue = "computer"; turnValue = null
                } else if (!hasEmptyCells(board)) {
                    stateValue = "DRAW"; turnValue = null
                }
            } else {
                turnValue = game.players.first { it.uuid != userUuid }.uuid
            }
            val updatedGame = game.copy(
                state = stateValue,
                currentTurn = turnValue,
                winner = winnerValue,
                board = board.map { it.toList() }
            )
            transaction {
                Games.update({ Games.uuid eq gameUuid }) {
                    it[Games.state] = updatedGame.state
                    it[Games.currentTurn] = updatedGame.currentTurn
                    it[Games.winner] = updatedGame.winner
                    it[Games.board] = boardToString(updatedGame.board)
                }
            }
            call.respond(HttpStatusCode.OK, updatedGame)
        }
    }
}
