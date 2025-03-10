package com.example.composetutorial.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Picture::class, Msg::class], version = 3)
abstract class UserDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object {
        private var Instance: UserDatabase? = null

        fun getDataBase(context: Context): UserDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, UserDatabase::class.java,
                    "userDatabase")
                        .fallbackToDestructiveMigration()
                        .build().also {Instance = it}
            }
        }
    }
}
