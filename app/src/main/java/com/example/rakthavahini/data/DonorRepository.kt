package com.example.rakthavahini.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DonorRepository(private val dao: DonorDao) {

    // GET ALL DONORS

    fun getAllDonors(): Flow<List<Donor>> =
        dao.getAllDonors()

    // GET ELIGIBLE DONORS

    fun getEligibleDonors(
        bloodGroup: String
    ): Flow<List<Donor>> {

        val now = System.currentTimeMillis()

        return dao.getEligibleDonors(
            bloodGroup,
            now
        )
    }

    // ADD DONOR

    suspend fun addDonor(
        donor: Donor
    ): Long = dao.insertDonor(donor)

    // UPDATE DONOR

    suspend fun updateDonor(
        donor: Donor
    ) = dao.updateDonor(donor)

    // LOG DONATION

    suspend fun logDonation(
        donorId: Int,
        hospitalName: String
    ) {

        val now =
            System.currentTimeMillis()

        // INSERT DONATION HISTORY

        dao.insertDonationRecord(

            DonationRecord(
                donorId = donorId,
                date = now,
                hospitalName = hospitalName
            )
        )

        // UPDATE LAST DONATION DATE

        dao.getDonorById(donorId)?.let { donor ->

            val formattedDate =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date())

            dao.updateDonor(

                donor.copy(
                    lastDonationDate = formattedDate
                )
            )
        }
    }

    // DONATION HISTORY

    fun getDonationHistory(
        donorId: Int
    ): Flow<List<DonationRecord>> =

        dao.getDonationHistory(donorId)
}