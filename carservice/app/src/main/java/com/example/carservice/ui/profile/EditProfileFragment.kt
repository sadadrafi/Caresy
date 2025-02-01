package com.example.carservice.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carservice.R
import com.example.carservice.model.User
import com.example.carservice.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    // Inisialisasi UserRepository
    private val userRepository = UserRepository()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Inisialisasi elemen UI
        val usernameInput = view.findViewById<EditText>(R.id.editText_username)
        val saveButton = view.findViewById<Button>(R.id.button_save)
        val cancelButton = view.findViewById<Button>(R.id.button_cancel)

        // Ambil userId dari FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid // Gunakan UID sebagai userId

        if (userId != null) {
            // Memuat data pengguna dari Firestore
            userRepository.getUserData(userId) { user ->
                user?.let {
                    // Isi input dengan data pengguna
                    usernameInput.setText(it.username)
                }
            }

            // Menyimpan perubahan data pengguna
            saveButton.setOnClickListener {
                val username = usernameInput.text.toString().trim()

                if (username.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill in the username", Toast.LENGTH_SHORT).show()
                } else {
                    // Membuat objek User dengan username yang baru
                    val updatedUser = User(username = username)

                    // Menyimpan data yang sudah diperbarui ke Firestore
                    saveUserData(userId, updatedUser)
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }

        // Navigasi kembali saat tombol "Cancel" ditekan
        cancelButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    /**
     * Fungsi untuk menyimpan data pengguna yang sudah diperbarui ke Firestore
     */
    private fun saveUserData(userId: String, updatedUser: User) {
        firestore.collection("users").document(userId)
            .update(
                "username", updatedUser.username
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
                // Navigasi kembali setelah data berhasil disimpan
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
