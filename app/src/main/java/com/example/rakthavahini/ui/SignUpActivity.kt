package com.example.rakthavahini

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.rakthavahini.data.AppDatabase
import com.example.rakthavahini.data.Donor
import com.example.rakthavahini.data.DonorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class SignUpActivity : AppCompatActivity() {

    private val bloodGroups =
        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        // VIEWS

        val etName = findViewById<EditText>(R.id.et_name)

        val etPhoneEmail =
            findViewById<EditText>(R.id.et_phone_email)

        val etLastDonation =
            findViewById<EditText>(R.id.et_last_donation)

        val etPassword =
            findViewById<EditText>(R.id.et_password)

        val etConfirmPassword =
            findViewById<EditText>(R.id.et_confirm_password)

        val spinnerBlood =
            findViewById<Spinner>(R.id.spinner_blood_group)

        val switchReady =
            findViewById<Switch>(R.id.switch_ready)

        val btnSignUp =
            findViewById<Button>(R.id.btn_signup)

        val tvSignIn =
            findViewById<TextView>(R.id.tv_signin)

        // BLOOD GROUP SPINNER

        spinnerBlood.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            bloodGroups
        ).also {

            it.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        // DATE PICKER

        etLastDonation.setOnClickListener {

            showDatePicker(etLastDonation)
        }

        // SIGNUP BUTTON

        btnSignUp.setOnClickListener {

            val name =
                etName.text.toString().trim()

            val phoneEmail =
                etPhoneEmail.text.toString().trim()

            val lastDonation =
                etLastDonation.text.toString().trim()

            val password =
                etPassword.text.toString().trim()

            val confirmPassword =
                etConfirmPassword.text.toString().trim()

            val bloodGroup =
                spinnerBlood.selectedItem.toString()

            val isReady =
                switchReady.isChecked()

            // EMPTY CHECK

            if (name.isEmpty()) {

                etName.error = "Enter full name"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (phoneEmail.isEmpty()) {

                etPhoneEmail.error =
                    "Enter phone or email"

                etPhoneEmail.requestFocus()
                return@setOnClickListener
            }

            if (lastDonation.isEmpty()) {

                etLastDonation.error =
                    "Select donation date"

                etLastDonation.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {

                etPassword.error =
                    "Enter password"

                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {

                etConfirmPassword.error =
                    "Confirm password"

                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // PASSWORD LENGTH

            if (password.length < 6) {

                etPassword.error =
                    "Minimum 6 characters required"

                etPassword.requestFocus()
                return@setOnClickListener
            }

            // PASSWORD MATCH

            if (password != confirmPassword) {

                etConfirmPassword.error =
                    "Passwords do not match"

                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // EMAIL OR PHONE VALIDATION

            val isEmail =
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(phoneEmail)
                    .matches()

            val isPhone =
                phoneEmail.matches(Regex("[0-9]{10}"))

            if (!isEmail && !isPhone) {

                etPhoneEmail.error =
                    "Enter valid email or 10-digit phone"

                etPhoneEmail.requestFocus()
                return@setOnClickListener
            }

            // ROOM DATABASE

            val db =
                AppDatabase.getInstance(this)

            val repo =
                DonorRepository(db.donorDao())

            CoroutineScope(Dispatchers.IO).launch {

                val donor = Donor(

                    name = name,

                    phone = phoneEmail,

                    bloodGroup = bloodGroup,

                    isReadyToDonate = isReady,

                    lastDonationDate = lastDonation
                )

                val donorId =
                    repo.addDonor(donor)

                // SHARED PREFERENCES

                val prefs =
                    getSharedPreferences(
                        "donor_prefs",
                        Context.MODE_PRIVATE
                    )

                prefs.edit()

                    .putString("name", name)

                    .putString("phone", phoneEmail)

                    .putString("password", password)

                    .putString("blood_group", bloodGroup)

                    .putString(
                        "last_donation",
                        lastDonation
                    )

                    .putBoolean(
                        "is_ready",
                        isReady
                    )

                    .putBoolean(
                        "is_logged_in",
                        true
                    )

                    .putInt(
                        "donor_id",
                        donorId.toInt()
                    )

                    .apply()

                runOnUiThread {

                    Toast.makeText(
                        this@SignUpActivity,
                        "Account Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@SignUpActivity,
                            MainActivity::class.java
                        )
                    )

                    finish()
                }
            }
        }

        // SIGN IN TEXT

        tvSignIn.setOnClickListener {

            finish()
        }
    }

    // DATE PICKER

    private fun showDatePicker(editText: EditText) {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)

        val month = calendar.get(Calendar.MONTH)

        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val date =
                        "$selectedDay/${selectedMonth + 1}/$selectedYear"

                    editText.setText(date)

                },
                year,
                month,
                day
            )

        datePickerDialog.show()
    }
}