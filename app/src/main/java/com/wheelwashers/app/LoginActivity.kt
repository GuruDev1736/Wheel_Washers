package com.wheelwashers.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wheelwashers.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Hide the action bar
        supportActionBar?.hide()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

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
            password.isEmpty() -> {
                binding.etPassword.error = getString(R.string.error_empty_password)
                binding.etPassword.requestFocus()
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = getString(R.string.error_password_length)
                binding.etPassword.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Show loading
        setLoading(true)

        // Sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    // If sign in fails, display a message to the user
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Logging in..." else getString(R.string.login)
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
