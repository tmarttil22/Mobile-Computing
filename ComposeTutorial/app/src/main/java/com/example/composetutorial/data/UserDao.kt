package com.example.composetutorial.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture (picture: Picture)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage (message: Msg)

    @Query("Select * FROM User ORDER BY uid DESC LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("Select * FROM Picture ORDER BY uid DESC LIMIT 1")
    fun getPicture(): Flow<Picture?>

    @Query("Select * FROM Message ORDER BY uid DESC LIMIT 1")
    fun getMessage(): Flow<Msg?>

    @Update
    suspend fun updateUser (user: User)

    @Update
    suspend fun updatePicture (picture: Picture)

    @Delete
    suspend fun delete (user: User)

    @Query("Select * FROM User")
    fun getAll(): List<User>

    @Query("Select * FROM Message")
    fun getAllMessages(): Flow<List<Msg>>
}