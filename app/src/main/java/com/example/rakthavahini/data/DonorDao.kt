package com.example.rakthavahini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor): Long

    @Update
    suspend fun updateDonor(donor: Donor)

    @Query("SELECT * FROM donors")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: Int): Donor?

    @Query("""
        SELECT * FROM donors 
        WHERE bloodGroup = :bloodGroup 
        AND isReadyToDonate = 1
        AND (lastDonationDate IS NULL OR 
             ((:currentTime - lastDonationDate) / 86400000) >= 90)
    """)
    fun getEligibleDonors(bloodGroup: String, currentTime: Long): Flow<List<Donor>>

    @Insert
    suspend fun insertDonationRecord(record: DonationRecord): Long

    @Query("SELECT * FROM donation_records WHERE donorId = :donorId ORDER BY date DESC")
    fun getDonationHistory(donorId: Int): Flow<List<DonationRecord>>
}