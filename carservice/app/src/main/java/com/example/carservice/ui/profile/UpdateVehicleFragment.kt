package com.example.carservice.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.carservice.databinding.FragmentUpdateVehicleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateVehicleFragment : Fragment() {

    private var _binding: FragmentUpdateVehicleBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // Menggunakan Safe Args untuk menerima data kendaraan
    private val args: UpdateVehicleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vehicleId = args.vehicleId // Mendapatkan vehicleId dari Safe Args
        Log.d("UpdateVehicleFragment", "Vehicle ID: $vehicleId")

        // Load data kendaraan berdasarkan vehicleId dari Firestore
        loadVehicleData(vehicleId)

        // Simpan data kendaraan setelah diubah
        binding.btnSave.setOnClickListener {
            saveUpdatedVehicle(vehicleId)
        }

        // Kembali ke fragment sebelumnya
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // Hapus data kendaraan
        binding.btnDelete.setOnClickListener {
            deleteVehicle(vehicleId)
        }
    }

    private fun loadVehicleData(vehicleId: String) {
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .collection("vehicles").document(vehicleId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val vehicle = document.data
                        vehicle?.let {
                            binding.etBrand.setText(it["brand"]?.toString() ?: "")
                            binding.etModel.setText(it["model"]?.toString() ?: "")
                            binding.etYear.setText(it["year"]?.toString() ?: "")
                            binding.etNumber.setText(it["licensePlate"]?.toString() ?: "")
                        } ?: run {
                            Toast.makeText(requireContext(), "No vehicle data found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Vehicle data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UpdateVehicleFragment", "Error loading vehicle data", e)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUpdatedVehicle(vehicleId: String) {
        val brand = binding.etBrand.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val year = binding.etYear.text.toString().trim()
        val licensePlate = binding.etNumber.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || year.isEmpty() || licensePlate.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedVehicleData = hashMapOf(
            "brand" to brand,
            "model" to model,
            "year" to year,
            "licensePlate" to licensePlate
        )

        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .collection("vehicles").document(vehicleId)
                .set(updatedVehicleData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Vehicle details updated successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.e("UpdateVehicleFragment", "Error updating vehicle data", e)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Fungsi untuk menghapus data kendaraan
    private fun deleteVehicle(vehicleId: String) {
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .collection("vehicles").document(vehicleId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Vehicle deleted successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .addOnFailureListener { e ->
                    Log.e("UpdateVehicleFragment", "Error deleting vehicle data", e)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
