package com.example.composetutorial.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User (
    @PrimaryKey (autoGenerate = true) val uid: Int = 0,
    val username: String
)

@Entity(tableName = "Picture")
data class Picture (
    @PrimaryKey (autoGenerate = true) val uid: Int = 0,
    val profileImage: String
)

@Entity(tableName = "Message")
data class Msg (
    @PrimaryKey (autoGenerate = true) val uid: Int = 0,
    val msg: String
)