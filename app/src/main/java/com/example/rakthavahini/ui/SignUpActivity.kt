package com.example.rakthavahini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rakthavahini.data.AppDatabase
import com.example.rakthavahini.data.Donor
import com.example.rakthavahini.data.DonorRepository
import com.example.rakthavahini.ui.DonorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.hide()

        val etName = findViewById<EditText>(R.id.et_name)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val spinnerBlood = findViewById<Spinner>(R.id.spinner_blood_group)
        val switchReady = findViewById<Switch>(R.id.switch_ready)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val tvSignIn = findViewById<TextView>(R.id.tv_signin)

        spinnerBlood.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            bloodGroups
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val bloodGroup = spinnerBlood.selectedItem.toString()
            val isReady = switchReady.isChecked

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this,
                    "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save donor to Room database
            val db = AppDatabase.getInstance(this)
            val repo = DonorRepository(db.donorDao())

            CoroutineScope(Dispatchers.IO).launch {
                val donor = Donor(
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    isReadyToDonate = isReady,
                    lastDonationDate = null
                )
                val donorId = repo.addDonor(donor)

                // Save to SharedPreferences
                val prefs = getSharedPreferences("donor_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("name", name)
                    .putString("phone", phone)
                    .putString("password", password)
                    .putString("blood_group", bloodGroup)
                    .putBoolean("is_ready", isReady)
                    .putBoolean("is_logged_in", true)
                    .putInt("donor_id", donorId.toInt())
                    .apply()

                runOnUiThread {
                    Toast.makeText(this@SignUpActivity,
                        "Account created! Welcome $name", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        tvSignIn.setOnClickListener {
            finish() // Go back to sign in
        }
    }
}