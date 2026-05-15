package com.example.rakthavahini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText

    private lateinit var btnSignIn: Button

    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var btnGoogle: LinearLayout

    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView

    private var isPhoneSelected = true
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signin)

        supportActionBar?.hide()

        // INITIALIZE VIEWS

        etPhone =
            findViewById(R.id.et_phone)

        etPassword =
            findViewById(R.id.et_password)

        btnSignIn =
            findViewById(R.id.btn_signin)

        tvSignUp =
            findViewById(R.id.tv_signup)

        tvForgotPassword =
            findViewById(R.id.tv_forgot_password)

        btnGoogle =
            findViewById(R.id.btn_google)

        tvEmail =
            findViewById(R.id.tv_email)

        tvPhone =
            findViewById(R.id.tv_phone)

        // DEFAULT MODE

        setPhoneMode()

        // EMAIL TOGGLE

        tvEmail.setOnClickListener {

            setEmailMode()
        }

        // PHONE TOGGLE

        tvPhone.setOnClickListener {

            setPhoneMode()
        }

        // PASSWORD SHOW / HIDE

        etPassword.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                val drawableEnd = 2

                if (
                    event.rawX >=
                    (
                            etPassword.right -
                                    etPassword.compoundDrawables[drawableEnd]
                                        .bounds.width()
                            )
                ) {

                    if (isPasswordVisible) {

                        // HIDE PASSWORD

                        etPassword.inputType =
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD

                        isPasswordVisible = false

                    } else {

                        // SHOW PASSWORD

                        etPassword.inputType =
                            InputType.TYPE_CLASS_TEXT or
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                        isPasswordVisible = true
                    }

                    etPassword.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.ic_lock_lock,
                        0,
                        android.R.drawable.ic_menu_view,
                        0
                    )

                    etPassword.setSelection(
                        etPassword.text.length
                    )

                    return@setOnTouchListener true
                }
            }

            false
        }

        // SIGN IN BUTTON

        btnSignIn.setOnClickListener {

            validateAndLogin()
        }

        // GOOGLE SIGN IN

        btnGoogle.setOnClickListener {

            Toast.makeText(
                this,
                "Google Sign In Coming Soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // FORGOT PASSWORD

        tvForgotPassword.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
        }

        // SIGN UP

        tvSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
    }

    // PHONE MODE

    private fun setPhoneMode() {

        isPhoneSelected = true

        tvPhone.setBackgroundResource(
            R.drawable.bg_toggle_selected
        )

        tvEmail.setBackgroundResource(
            android.R.color.transparent
        )

        tvPhone.setTextColor(
            resources.getColor(R.color.red)
        )

        tvEmail.setTextColor(
            resources.getColor(
                android.R.color.darker_gray
            )
        )

        etPhone.text.clear()

        etPhone.hint =
            "Enter phone number"

        etPhone.inputType =
            InputType.TYPE_CLASS_PHONE

        etPhone.setCompoundDrawablesWithIntrinsicBounds(
            android.R.drawable.ic_menu_call,
            0,
            0,
            0
        )
    }

    // EMAIL MODE

    private fun setEmailMode() {

        isPhoneSelected = false

        tvEmail.setBackgroundResource(
            R.drawable.bg_toggle_selected
        )

        tvPhone.setBackgroundResource(
            android.R.color.transparent
        )

        tvEmail.setTextColor(
            resources.getColor(R.color.red)
        )

        tvPhone.setTextColor(
            resources.getColor(
                android.R.color.darker_gray
            )
        )

        etPhone.text.clear()

        etPhone.hint =
            "Enter email"

        etPhone.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        etPhone.setCompoundDrawablesWithIntrinsicBounds(
            android.R.drawable.ic_dialog_email,
            0,
            0,
            0
        )
    }

    // VALIDATE + LOGIN

    private fun validateAndLogin() {

        val input =
            etPhone.text.toString().trim()

        val password =
            etPassword.text.toString().trim()

        // EMPTY FIELD CHECK

        if (input.isEmpty()) {

            etPhone.error =
                "This field is required"

            etPhone.requestFocus()

            return
        }

        if (password.isEmpty()) {

            etPassword.error =
                "Password required"

            etPassword.requestFocus()

            return
        }

        // PASSWORD LENGTH

        if (password.length < 6) {

            etPassword.error =
                "Minimum 6 characters required"

            etPassword.requestFocus()

            return
        }

        // EMAIL VALIDATION

        if (!isPhoneSelected) {

            val isValidEmail =
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(input)
                    .matches()

            if (!isValidEmail) {

                etPhone.error =
                    "Enter valid email address"

                etPhone.requestFocus()

                return
            }
        }

        // PHONE VALIDATION

        if (isPhoneSelected) {

            if (
                input.length != 10 ||
                !input.matches(Regex("[0-9]+"))
            ) {

                etPhone.error =
                    "Enter valid 10-digit number"

                etPhone.requestFocus()

                return
            }
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

        val savedPassword =
            prefs.getString(
                "password",
                ""
            )

        // LOGIN CHECK

        if (
            input == savedPhone &&
            password == savedPassword
        ) {

            prefs.edit()

                .putBoolean(
                    "is_logged_in",
                    true
                )

                .apply()

            Toast.makeText(
                this,
                "Login Successful",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()

        } else {

            Toast.makeText(
                this,
                "Invalid Credentials",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}