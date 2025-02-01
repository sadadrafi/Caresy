package com.example.carservice.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carservice.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Aktivitas untuk menangani proses recovery password.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    // Inisialisasi Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val backButton = findViewById<ImageView>(R.id.ivBackButton)
        val emailInput = findViewById<EditText>(R.id.etRecoveryEmail)
        val sendCodeButton = findViewById<Button>(R.id.btnSendCode)

        // Kembali ke halaman sebelumnya
        backButton.setOnClickListener {
            finish() // Tutup aktivitas ini
        }

        // Kirim email pemulihan
        sendCodeButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim email reset password melalui Firebase Authentication
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Recovery email sent! Check your inbox.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Arahkan ke halaman login
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
