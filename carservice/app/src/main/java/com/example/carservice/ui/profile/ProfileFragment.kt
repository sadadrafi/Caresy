package com.example.carservice.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carservice.R
import com.example.carservice.adapter.VehiclesAdapter
import com.example.carservice.auth.LoginActivity
import com.example.carservice.databinding.FragmentProfileBinding
import com.example.carservice.model.User
import com.example.carservice.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var vehiclesAdapter: VehiclesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchUserProfileData()
        fetchVehicleData()

        // Tombol untuk menambahkan kendaraan baru
        binding.btnAddVehicle.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addVehicleFragment)
        }

        // Tombol untuk mengedit profil
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Tombol untuk mengganti kata sandi
        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        // Tombol untuk logout
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        vehiclesAdapter = VehiclesAdapter(emptyList()) { selectedVehicle ->
            // Navigasi ke halaman update vehicle dengan Safe Args
            val action = ProfileFragmentDirections
                .actionProfileFragmentToUpdateVehicleFragment(selectedVehicle.id)
            findNavController().navigate(action)
        }

        binding.rvVehicles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vehiclesAdapter
        }
    }

    private fun fetchUserProfileData() {
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Mengambil data dengan menggunakan User data class
                        val user = document.toObject(User::class.java)

                        // Menampilkan data dari object User
                        binding.tvUsername.text = user?.username ?: "Username not available"
                        binding.tvEmail.text = user?.email ?: "Email not available"
                    } else {
                        binding.tvUsername.text = "Username not found"
                        binding.tvEmail.text = "Email not available"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching user profile data", e)
                    binding.tvUsername.text = "Error fetching username"
                    binding.tvEmail.text = "Error fetching email"
                }
        } ?: run {
            binding.tvUsername.text = "No user"
            binding.tvEmail.text = "No email"
        }
    }


    private fun fetchVehicleData() {
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).collection("vehicles")
                .get()
                .addOnSuccessListener { snapshot ->
                    val vehicleList = snapshot.documents.mapNotNull  { document ->
                        Vehicle(
                            id = document.id, // Simpan ID dokumen
                            brand = document.getString("brand") ?: "",
                            model = document.getString("model") ?: "",
                            year = document.getString("year") ?: "",
                            licensePlate = document.getString("licensePlate") ?: ""
                        )
                    }
                    vehiclesAdapter.updateVehicles(vehicleList)
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching vehicles", e)
                }
        } ?: run {
            Log.e("FirestoreError", "User ID not available")
        }
    }

    override fun onResume() {
        super.onResume()
        // Memuat ulang data kendaraan saat kembali ke fragment ini
        fetchVehicleData()
        fetchUserProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
