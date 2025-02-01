package com.example.carservice.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carservice.MainActivity
import com.example.carservice.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Aktivitas untuk menangani proses login pengguna.
 */
class LoginActivity : AppCompatActivity() {

    // Inisialisasi Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerPrompt = findViewById<TextView>(R.id.btnRegister)
        val forgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // Tombol untuk login
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses login menggunakan Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Cek jika email telah diverifikasi
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                            // Arahkan ke halaman utama (MainActivity)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Please verify your email before logging in.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Navigasi ke halaman registrasi
        registerPrompt.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Navigasi ke halaman lupa password
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
