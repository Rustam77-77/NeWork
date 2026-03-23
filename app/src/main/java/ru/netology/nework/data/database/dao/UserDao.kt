package ru.netology.nework.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Long)
}