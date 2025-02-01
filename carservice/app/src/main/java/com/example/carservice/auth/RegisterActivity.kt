package com.example.carservice.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carservice.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Aktivitas untuk menangani proses registrasi pengguna dengan email verifikasi menggunakan OTP Link.
 */
class RegisterActivity : AppCompatActivity() {

    // Inisialisasi Firebase Authentication dan Firestore
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        // Referensi ke elemen UI
        val emailInput = findViewById<EditText>(R.id.etEmail)
        val usernameInput = findViewById<EditText>(R.id.etUsername)  // Menambahkan username input
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.etConfirmPassword)
        val sendCodeButton = findViewById<Button>(R.id.btnSendOtp)
        val loginPrompt = findViewById<TextView>(R.id.tvLoginPrompt)
        val showPasswordCheckBox = findViewById<CheckBox>(R.id.cbShowPassword)

        // Toggle visibilitas password
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmPasswordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                confirmPasswordInput.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            passwordInput.setSelection(passwordInput.text.length)
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }

        // Tombol untuk registrasi
        sendCodeButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()  // Ambil username dari input
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registrasi pengguna
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Tambahkan data pengguna ke Firestore termasuk username
                        val userData = hashMapOf(
                            "email" to email,
                            "username" to username,  // Menyimpan username
                            "role" to "user" // Peran default sebagai "user"
                        )

                        firestore.collection("users").document(user!!.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // Kirim email verifikasi
                                user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Verification email sent. Please check your email.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Arahkan pengguna ke LoginActivity
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Failed to send verification email: ${emailTask.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to add user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Tombol untuk kembali ke Login
        loginPrompt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
