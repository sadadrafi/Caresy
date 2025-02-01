package com.example.carservice.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carservice.databinding.FragmentAddVehicleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val brand = binding.etBrand.text.toString().trim()
            val model = binding.etModel.text.toString().trim()
            val year = binding.etYear.text.toString().trim()
            val licensePlate = binding.etNumber.text.toString().trim()

            if (brand.isEmpty() || model.isEmpty() || year.isEmpty() || licensePlate.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val vehicleData = hashMapOf(
                "brand" to brand,
                "model" to model,
                "year" to year,
                "licensePlate" to licensePlate
            )

            currentUser?.uid?.let { userId ->
                firestore.collection("users").document(userId)
                    .collection("vehicles")
                    .add(vehicleData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
