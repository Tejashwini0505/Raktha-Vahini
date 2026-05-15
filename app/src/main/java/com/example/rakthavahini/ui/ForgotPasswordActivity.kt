package com.example.rakthavahini

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        // VIEWS

        val etEmailPhone =
            findViewById<EditText>(R.id.et_email_phone)

        val etNewPassword =
            findViewById<EditText>(R.id.et_new_password)

        val etConfirmPassword =
            findViewById<EditText>(R.id.et_confirm_password)

        val btnReset =
            findViewById<Button>(R.id.btn_reset_password)

        val tvBack =
            findViewById<TextView>(R.id.tv_back_login)

        // RESET BUTTON

        btnReset.setOnClickListener {

            val emailPhone =
                etEmailPhone.text.toString().trim()

            val newPassword =
                etNewPassword.text.toString().trim()

            val confirmPassword =
                etConfirmPassword.text.toString().trim()

            // EMPTY CHECK

            if (emailPhone.isEmpty()) {

                etEmailPhone.error =
                    "Enter email or phone"

                etEmailPhone.requestFocus()

                return@setOnClickListener
            }

            if (newPassword.isEmpty()) {

                etNewPassword.error =
                    "Enter new password"

                etNewPassword.requestFocus()

                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {

                etConfirmPassword.error =
                    "Confirm password"

                etConfirmPassword.requestFocus()

                return@setOnClickListener
            }

            // PASSWORD LENGTH

            if (newPassword.length < 6) {

                etNewPassword.error =
                    "Minimum 6 characters required"

                etNewPassword.requestFocus()

                return@setOnClickListener
            }

            // PASSWORD MATCH

            if (newPassword != confirmPassword) {

                etConfirmPassword.error =
                    "Passwords do not match"

                etConfirmPassword.requestFocus()

                return@setOnClickListener
            }

            // EMAIL / PHONE VALIDATION

            val isEmail =
                Patterns.EMAIL_ADDRESS
                    .matcher(emailPhone)
                    .matches()

            val isPhone =
                emailPhone.matches(
                    Regex("[0-9]{10}")
                )

            if (!isEmail && !isPhone) {

                etEmailPhone.error =
                    "Enter valid email or phone"

                etEmailPhone.requestFocus()

                return@setOnClickListener
            }

            // SHARED PREFERENCES

            val prefs =
                getSharedPreferences(
                    "donor_prefs",
                    Context.MODE_PRIVATE
                )

            val savedPhone =
                prefs.getString(
                    "phone",
                    ""
                )

            // CHECK ACCOUNT

            if (emailPhone != savedPhone) {

                Toast.makeText(
                    this,
                    "Account not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // UPDATE PASSWORD

            prefs.edit()
                .putString(
                    "password",
                    newPassword
                )
                .apply()

            Toast.makeText(
                this,
                "Password Reset Successful",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }

        // BACK TO LOGIN

        tvBack.setOnClickListener {

            finish()
        }
    }
}