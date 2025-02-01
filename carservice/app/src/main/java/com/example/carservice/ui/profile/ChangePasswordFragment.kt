package com.example.carservice.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carservice.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.buttonSavePassword.setOnClickListener {
            val currentPassword = binding.editTextCurrentPassword.text.toString().trim()
            val newPassword = binding.editTextNewPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmNewPassword.text.toString().trim()

            // Validasi input pengguna
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reauthenticate pengguna dengan password saat ini
            val user = auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Update password
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        requireActivity().onBackPressedDispatcher.onBackPressed()
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.buttonCancelPassword.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
