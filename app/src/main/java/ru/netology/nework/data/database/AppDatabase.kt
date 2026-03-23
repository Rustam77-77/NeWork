package ru.netology.nework.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import ru.netology.nework.data.database.dao.*
import ru.netology.nework.data.database.entities.*

@Database(
    entities = [
        PostEntity::class,
        EventEntity::class,
        UserEntity::class,
        JobEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nework_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}