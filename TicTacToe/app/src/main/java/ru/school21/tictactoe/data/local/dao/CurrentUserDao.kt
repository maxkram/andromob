package ru.school21.tictactoe.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.school21.tictactoe.data.local.entity.CurrentUserEntity

@Dao
interface CurrentUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: CurrentUserEntity): Completable

    @Update
    fun update(user: CurrentUserEntity): Completable

    @Delete
    fun delete(user: CurrentUserEntity): Completable

    @Query("SELECT * FROM current_user LIMIT 1")
    fun getCurrent(): Single<CurrentUserEntity?>

    @Query("DELETE FROM current_user")
    fun deleteAll(): Completable

    @Query("SELECT EXISTS(SELECT 1 FROM current_user)")
    fun hasCurrent(): Single<Boolean>
}