package ru.school21.tictactoe.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.school21.tictactoe.data.local.entity.GameEntity

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: GameEntity): Completable

    @Update
    fun update(game: GameEntity): Completable

    @Delete
    fun delete(game: GameEntity): Completable

    @Query("SELECT * FROM games WHERE id = :id")
    fun getById(id: String): Single<GameEntity?>

    @Query("SELECT * FROM games")
    fun getAll(): Single<List<GameEntity>>

    @Query("DELETE FROM games")
    fun deleteAll(): Completable

    @Query("SELECT EXISTS(SELECT 1 FROM games WHERE id = :id)")
    fun existsById(id: String): Single<Boolean>
}