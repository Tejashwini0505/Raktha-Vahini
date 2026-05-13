package com.example.rakthavahini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_records")
data class DonationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorId: Int,
    val date: Long,
    val hospitalName: String
)