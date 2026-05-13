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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DonorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = DonorRepository(db.donorDao())

    val allDonors: LiveData<List<Donor>> = repo.getAllDonors().asLiveData()

    private val _selectedBloodGroup = MutableStateFlow("A+")

    val eligibleDonors: LiveData<List<Donor>> = _selectedBloodGroup
        .flatMapLatest { bloodGroup ->
            repo.getEligibleDonors(bloodGroup)
        }.asLiveData()

    // Store saved donor id in memory
    private val _savedDonorId = MutableLiveData<Int?>()
    val savedDonorId: LiveData<Int?> = _savedDonorId

    fun setBloodGroupFilter(group: String) {
        _selectedBloodGroup.value = group
    }

    fun addDonor(donor: Donor) = viewModelScope.launch {
        val id = repo.addDonor(donor)
        _savedDonorId.postValue(id.toInt())
    }

    fun updateDonor(donor: Donor) = viewModelScope.launch {
        repo.updateDonor(donor)
    }

    fun logDonation(donorId: Int, hospital: String) = viewModelScope.launch {
        repo.logDonation(donorId, hospital)
    }

    fun getDonationHistory(donorId: Int) =
        repo.getDonationHistory(donorId).asLiveData()
}