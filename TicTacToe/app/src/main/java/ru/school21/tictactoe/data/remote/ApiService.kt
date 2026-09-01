package ru.school21.tictactoe.data.remote

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import ru.school21.tictactoe.data.remote.dto.*

interface ApiService {

    @POST("auth/signup")
    fun signUp(@Body request: SignUpRequestDto): Completable

    @POST("auth/signin")
    fun signIn(@Header("Authorization") authorization: String): Single<SignInResponseDto>

    @GET("games")
    fun getGames(): Single<List<GameDto>>

    @POST("games")
    fun createGame(@Body request: CreateGameRequestDto): Single<GameDto>

    @GET("games/{id}")
    fun getGame(@Path("id") gameId: String): Single<GameDto>

    @POST("games/{id}/join")
    fun joinGame(@Path("id") gameId: String): Single<GameDto>

    @POST("games/{id}/move")
    fun makeMove(@Path("id") gameId: String, @Body request: MoveRequestDto): Single<GameDto>
    @GET("users/{id}")
    fun getUser(@Path("id") userId: String): Single<UserDto>
}