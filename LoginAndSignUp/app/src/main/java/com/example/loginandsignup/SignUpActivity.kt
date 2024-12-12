package com.example.loginandsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.loginandsignup.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Binding layout setup
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
    val intent = Intent(this, SignInActivity::class.java)
    startActivity(intent)
    finish()
}
        // Button click listener to create a new user
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            // Basic validation for empty fields and matching passwords
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Firebase method to create user
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Show success message and navigate to SignInActivity
                            Toast.makeText(this, "Registration successful! Redirecting to login...", Toast.LENGTH_SHORT).show()
                            Log.d("SignUpActivity", "User registered successfully")

                            // Redirect to SignInActivity
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Show error message if signup fails
                            val errorMessage = task.exception?.message ?: "Registration failed"
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("SignUpActivity", "Registration error: $errorMessage")
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
