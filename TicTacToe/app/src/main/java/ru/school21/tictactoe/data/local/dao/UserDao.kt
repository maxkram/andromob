package ru.school21.tictactoe.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.school21.tictactoe.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity): Completable

    @Update
    fun update(user: UserEntity): Completable

    @Delete
    fun delete(user: UserEntity): Completable

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: String): Single<UserEntity?>

    @Query("SELECT * FROM users WHERE login = :login")
    fun getByLogin(login: String): Single<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAll(): Single<List<UserEntity>>

    @Query("DELETE FROM users")
    fun deleteAll(): Completable

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :id)")
    fun existsById(id: String): Single<Boolean>
}