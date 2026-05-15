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

    private val bloodGroups = listOf(
        "A+",
        "A-",
        "B+",
        "B-",
        "AB+",
        "AB-",
        "O+",
        "O-"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        // VIEWS

        val etName =
            view.findViewById<EditText>(R.id.et_name)

        val etPhone =
            view.findViewById<EditText>(R.id.et_phone)

        val spinnerBlood =
            view.findViewById<Spinner>(R.id.spinner_blood_group)

        val switchReady =
            view.findViewById<Switch>(R.id.switch_ready)

        val btnSave =
            view.findViewById<Button>(R.id.btn_save)

        // SPINNER

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            bloodGroups
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerBlood.adapter = adapter

        // SHARED PREFS

        val prefs =
            requireContext().getSharedPreferences(
                "donor_prefs",
                Context.MODE_PRIVATE
            )

        // LOAD DATA

        etName.setText(
            prefs.getString("name", "")
        )

        etPhone.setText(
            prefs.getString("phone", "")
        )

        val savedGroup =
            prefs.getString(
                "blood_group",
                "A+"
            ) ?: "A+"

        spinnerBlood.setSelection(
            bloodGroups.indexOf(savedGroup)
        )

        switchReady.isChecked =
            prefs.getBoolean(
                "is_ready",
                true
            )

        // SAVE BUTTON

        btnSave.setOnClickListener {

            val name =
                etName.text.toString().trim()

            val phone =
                etPhone.text.toString().trim()

            val bloodGroup =
                spinnerBlood.selectedItem.toString()

            val isReady =
                switchReady.isChecked

            // VALIDATION

            if (name.isEmpty()) {

                etName.error =
                    "Enter full name"

                return@setOnClickListener
            }

            if (phone.isEmpty()) {

                etPhone.error =
                    "Enter phone number"

                return@setOnClickListener
            }

            if (
                phone.length != 10 ||
                !phone.matches(Regex("[0-9]+"))
            ) {

                etPhone.error =
                    "Enter valid 10-digit number"

                return@setOnClickListener
            }

            val donorId =
                prefs.getInt(
                    "donor_id",
                    -1
                )

            val donor = Donor(

                id =
                    if (donorId == -1)
                        0
                    else
                        donorId,

                name = name,

                phone = phone,

                bloodGroup = bloodGroup,

                isReadyToDonate = isReady,

                lastDonationDate =
                    prefs.getString(
                        "last_donation",
                        null
                    )
            )

            // INSERT OR UPDATE

            if (donorId == -1) {

                viewModel.addDonor(donor)

            } else {

                viewModel.updateDonor(donor)
            }

            // SAVE PREFS

            prefs.edit()

                .putString(
                    "name",
                    name
                )

                .putString(
                    "phone",
                    phone
                )

                .putString(
                    "blood_group",
                    bloodGroup
                )

                .putBoolean(
                    "is_ready",
                    isReady
                )

                .apply()

            Toast.makeText(
                requireContext(),
                "Profile Updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}