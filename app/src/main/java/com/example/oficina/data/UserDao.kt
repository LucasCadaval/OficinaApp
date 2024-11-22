package com.example.oficina.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.oficina.models.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table LIMIT 1")
    suspend fun getLoggedInUser(): User?

    @Query("DELETE FROM user_table")
    suspend fun clearUser()
}
