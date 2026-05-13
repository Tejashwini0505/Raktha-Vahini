package com.example.rakthavahini.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rakthavahini.R
import com.example.rakthavahini.data.Donor

class ProfileFragment : Fragment() {

    private val viewModel: DonorViewModel by activityViewModels()
    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etName = view.findViewById<EditText>(R.id.et_name)
        val etPhone = view.findViewById<EditText>(R.id.et_phone)
        val spinnerBlood = view.findViewById<Spinner>(R.id.spinner_blood_group)
        val switchReady = view.findViewById<Switch>(R.id.switch_ready)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        spinnerBlood.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            bloodGroups
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Load existing profile
        val prefs = requireContext()
            .getSharedPreferences("donor_prefs", Context.MODE_PRIVATE)
        etName.setText(prefs.getString("name", ""))
        etPhone.setText(prefs.getString("phone", ""))
        val savedGroup = prefs.getString("blood_group", "A+") ?: "A+"
        val groupIndex = bloodGroups.indexOf(savedGroup)
        spinnerBlood.setSelection(if (groupIndex >= 0) groupIndex else 0)
        switchReady.isChecked = prefs.getBoolean("is_ready", true)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val bloodGroup = spinnerBlood.selectedItem.toString()
            val isReady = switchReady.isChecked

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please fill in name and phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val existingId = prefs.getInt("donor_id", -1)

            if (existingId == -1) {
                // First time — insert new donor
                val donor = Donor(
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    isReadyToDonate = isReady,
                    lastDonationDate = null
                )
                viewModel.addDonor(donor)

                // Observe the returned ID and save it
                viewModel.savedDonorId.observe(viewLifecycleOwner) { id ->
                    if (id != null && id != -1) {
                        prefs.edit()
                            .putString("name", name)
                            .putString("phone", phone)
                            .putString("blood_group", bloodGroup)
                            .putBoolean("is_ready", isReady)
                            .putInt("donor_id", id)
                            .apply()
                    }
                }
            } else {
                // Already exists — update same donor, never create a new one
                val donor = Donor(
                    id = existingId,
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    isReadyToDonate = isReady,
                    lastDonationDate = prefs.getLong("last_donation", -1L)
                        .takeIf { it != -1L }
                )
                viewModel.updateDonor(donor)

                // Update SharedPreferences
                prefs.edit()
                    .putString("name", name)
                    .putString("phone", phone)
                    .putString("blood_group", bloodGroup)
                    .putBoolean("is_ready", isReady)
                    .apply()
            }

            Toast.makeText(requireContext(),
                "Profile saved!", Toast.LENGTH_SHORT).show()
        }
    }
}