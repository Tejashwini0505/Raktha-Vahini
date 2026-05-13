package com.example.rakthavahini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val bloodGroup: String,
    val phone: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastDonationDate: Long? = null,
    val isReadyToDonate: Boolean = true
)