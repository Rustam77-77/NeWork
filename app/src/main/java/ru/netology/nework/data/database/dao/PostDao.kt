package ru.netology.nework.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.data.database.entities.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY published DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Long): Flow<PostEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deleteById(postId: Long)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}