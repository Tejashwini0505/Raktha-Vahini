package com.example.rakthavahini.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Donor::class, DonationRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun donorDao(): DonorDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rakta_vahini_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}