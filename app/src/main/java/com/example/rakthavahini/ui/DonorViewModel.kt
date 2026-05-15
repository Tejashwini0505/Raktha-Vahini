package com.example.rakthavahini.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rakthavahini.data.AppDatabase
import com.example.rakthavahini.data.Donor
import com.example.rakthavahini.data.DonorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class DonorViewModel(
    application: Application
) : AndroidViewModel(application) {

    // DATABASE

    private val db =
        AppDatabase.getInstance(application)

    private val repo =
        DonorRepository(db.donorDao())

    // ALL DONORS

    val allDonors: LiveData<List<Donor>> =
        repo.getAllDonors().asLiveData()

    // BLOOD GROUP FILTER

    private val _selectedBloodGroup =
        MutableStateFlow("A+")

    // ELIGIBLE DONORS

    val eligibleDonors: LiveData<List<Donor>> =
        _selectedBloodGroup
            .flatMapLatest { bloodGroup ->

                repo.getEligibleDonors(
                    bloodGroup
                )
            }
            .asLiveData()

    // SAVED DONOR ID

    private val _savedDonorId =
        MutableLiveData<Int?>()

    val savedDonorId:
            LiveData<Int?> =
        _savedDonorId

    // SET FILTER

    fun setBloodGroupFilter(
        group: String
    ) {

        _selectedBloodGroup.value =
            group
    }

    // ADD DONOR

    fun addDonor(
        donor: Donor
    ) = viewModelScope.launch {

        val id =
            repo.addDonor(donor)

        _savedDonorId.postValue(
            id.toInt()
        )
    }

    // UPDATE DONOR

    fun updateDonor(
        donor: Donor
    ) = viewModelScope.launch {

        repo.updateDonor(donor)
    }

    // LOG DONATION

    fun logDonation(
        donorId: Int,
        hospital: String
    ) = viewModelScope.launch {

        repo.logDonation(
            donorId,
            hospital
        )
    }

    // DONATION HISTORY

    fun getDonationHistory(
        donorId: Int
    ) = repo.getDonationHistory(
        donorId
    ).asLiveData()
}