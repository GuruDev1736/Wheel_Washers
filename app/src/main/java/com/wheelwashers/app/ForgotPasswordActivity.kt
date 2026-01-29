package com.wheelwashers.app

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wheelwashers.app.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    private var emailSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Hide the action bar
        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Send reset link button
        binding.btnSendResetLink.setOnClickListener {
            if (validateEmail()) {
                sendPasswordResetEmail()
            }
        }

        // Back to login text (on initial form)
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }

        // Back to login button (shown after email sent)
        binding.btnBackToLogin.setOnClickListener {
            finish()
        }

        // Resend email button
        binding.tvResendEmail.setOnClickListener {
            if (validateEmail()) {
                sendPasswordResetEmail()
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()

        return when {
            email.isEmpty() -> {
                binding.etEmail.error = getString(R.string.error_empty_email)
                binding.etEmail.requestFocus()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = getString(R.string.error_invalid_email)
                binding.etEmail.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.etEmail.text.toString().trim()

        // Show loading
        setLoading(true)

        // Send password reset email
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    emailSent = true
                    showSuccessState()
                    Toast.makeText(
                        this,
                        "Password reset email sent successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showSuccessState() {
        // Hide the initial form
        binding.layoutInitialForm.visibility = android.view.View.GONE

        // Show success state
        binding.layoutSuccessState.visibility = android.view.View.VISIBLE

        // Display the email address
        binding.tvEmailSent.text = "We've sent a password reset link to\n${binding.etEmail.text.toString().trim()}"
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSendResetLink.isEnabled = !isLoading
        binding.btnSendResetLink.text = if (isLoading) "Sending..." else "Send Reset Link"
        binding.etEmail.isEnabled = !isLoading
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
