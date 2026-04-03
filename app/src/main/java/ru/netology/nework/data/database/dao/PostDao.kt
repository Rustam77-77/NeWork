package ru.netology.nework.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.data.database.entities.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY published DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Update
    suspend fun update(post: PostEntity)

    @Delete
    suspend fun delete(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}