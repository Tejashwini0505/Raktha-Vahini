package com.example.rakthavahini.data

import kotlinx.coroutines.flow.Flow

class DonorRepository(private val dao: DonorDao) {

    fun getAllDonors(): Flow<List<Donor>> = dao.getAllDonors()

    fun getEligibleDonors(bloodGroup: String): Flow<List<Donor>> {
        val now = System.currentTimeMillis()
        return dao.getEligibleDonors(bloodGroup, now)
    }

    suspend fun addDonor(donor: Donor): Long = dao.insertDonor(donor)

    suspend fun updateDonor(donor: Donor) = dao.updateDonor(donor)

    suspend fun logDonation(donorId: Int, hospitalName: String) {
        val now = System.currentTimeMillis()
        dao.insertDonationRecord(
            DonationRecord(donorId = donorId, date = now, hospitalName = hospitalName)
        )
        dao.getDonorById(donorId)?.let { donor ->
            dao.updateDonor(donor.copy(lastDonationDate = now))
        }
    }

    fun getDonationHistory(donorId: Int): Flow<List<DonationRecord>> =
        dao.getDonationHistory(donorId)
}