package com.wheelwashers.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wheelwashers.app.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Hide the action bar
        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                performSignup()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Go back to login activity
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        return when {
            fullName.isEmpty() -> {
                binding.etFullName.error = getString(R.string.error_empty_name)
                binding.etFullName.requestFocus()
                false
            }
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
            phone.isEmpty() -> {
                binding.etPhone.error = getString(R.string.error_empty_phone)
                binding.etPhone.requestFocus()
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
            confirmPassword != password -> {
                binding.etConfirmPassword.error = getString(R.string.error_password_mismatch)
                binding.etConfirmPassword.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun performSignup() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Show loading
        setLoading(true)

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, save user data to Realtime Database
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToDatabase(userId, fullName, email, phone)
                    } else {
                        setLoading(false)
                        Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If sign up fails, display a message to the user
                    setLoading(false)
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToDatabase(userId: String, fullName: String, email: String, phone: String) {
        val userRef = database.getReference("users").child(userId)

        val userData = hashMapOf(
            "uid" to userId,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "createdAt" to System.currentTimeMillis(),
            "userType" to "customer" // default user type
        )

        userRef.setValue(userData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(
                    this,
                    "Failed to save user data: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSignUp.isEnabled = !isLoading
        binding.btnSignUp.text = if (isLoading) "Creating Account..." else getString(R.string.sign_up)
        binding.etFullName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPhone.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
