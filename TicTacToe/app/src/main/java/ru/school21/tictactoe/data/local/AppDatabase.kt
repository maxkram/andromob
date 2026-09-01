package ru.school21.tictactoe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.school21.tictactoe.data.local.dao.CurrentUserDao
import ru.school21.tictactoe.data.local.dao.GameDao
import ru.school21.tictactoe.data.local.dao.UserDao
import ru.school21.tictactoe.data.local.entity.CurrentUserEntity
import ru.school21.tictactoe.data.local.entity.GameEntity
import ru.school21.tictactoe.data.local.entity.UserEntity
import javax.inject.Singleton

@Database(
    entities = [
        UserEntity::class,
        GameEntity::class,
        CurrentUserEntity::class
    ],
    version = 2,
    exportSchema = false
)
@Singleton
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun gameDao(): GameDao
    abstract fun currentUserDao(): CurrentUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tictactoe_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}