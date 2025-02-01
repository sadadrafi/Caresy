package com.example.carservice.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.carservice.R
import com.example.carservice.databinding.FragmentBookingConfirmationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingConfirmationFragment : Fragment() {

    private var _binding: FragmentBookingConfirmationBinding? = null
    private val binding get() = _binding!!
    private val args: BookingConfirmationFragmentArgs by navArgs()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingConfirmationBinding.inflate(inflater, container, false)

        // Mendapatkan serviceId dari args
        val serviceId = args.serviceId

        // Menampilkan detail pemesanan
        binding.tvUsername.text = "Booked by: ${args.username}"
        binding.tvServiceName.text = args.serviceName
        binding.tvServicePrice.text = args.servicePrice
        binding.tvSelectedDateTime.text = "${args.serviceDate} at ${args.serviceTime}"
        binding.tvServiceDuration.text = args.serviceDuration

        // Tombol untuk memilih kendaraan
        binding.btnSelectVehicle.setOnClickListener {
            fetchUserVehicles()
        }

        // Tombol untuk membatalkan pemesanan
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // Tombol untuk mengonfirmasi pemesanan
        binding.btnConfirm.setOnClickListener {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email

            val paymentMethod = when (binding.radioGroupPayment.checkedRadioButtonId) {
                R.id.rbCIMB -> "CIMB"
                R.id.rbTnG -> "TnG"
                R.id.rbCOD -> "Bayar di Tempat"
                else -> null
            }

            val vehicle = binding.tvVehicle.text.toString()

            if (vehicle.isBlank() || vehicle == "Select a vehicle") {
                Toast.makeText(requireContext(), "Please select a vehicle", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (paymentMethod == null) {
                Toast.makeText(requireContext(), "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan pemesanan ke Firestore
            saveBooking(paymentMethod, vehicle, serviceId)

            // Navigasi ke BookingSuccessFragment
            val action = userEmail?.let { email ->
                BookingConfirmationFragmentDirections
                    .actionBookingConfirmationFragmentToBookingSuccessFragment(
                        userId = email,
                        username = args.username,
                        confirmationServiceName = args.serviceName,
                        confirmationServicePrice = args.servicePrice,
                        confirmationSelectedDate = args.serviceDate,
                        confirmationSelectedTime = args.serviceTime,
                        vehicle = vehicle,
                        confirmationPaymentMethod = paymentMethod,
                        confirmationServiceDuration = args.serviceDuration,
                        confirmationMessage = "Thank you for your booking!"
                    )
            }

            if (action != null) {
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    private fun fetchUserVehicles() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No vehicles found for this user", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val userId = documents.first().id
                db.collection("users")
                    .document(userId)
                    .collection("vehicles")
                    .get()
                    .addOnSuccessListener { vehicleDocs ->
                        val vehicleList = mutableListOf<String>()
                        for (doc in vehicleDocs) {
                            val vehicle = doc.getString("brand") + " - " +
                                    doc.getString("model") + " (" +
                                    doc.getString("licensePlate") + ")"
                            vehicleList.add(vehicle)
                        }
                        showVehicleSelectionDialog(vehicleList)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch vehicles", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showVehicleSelectionDialog(vehicleList: List<String>) {
        val vehicleArray = vehicleList.toTypedArray()
        var selectedVehicle: String? = null

        val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Select Vehicle")
        dialogBuilder.setSingleChoiceItems(vehicleArray, -1) { _, which ->
            selectedVehicle = vehicleArray[which]
        }
        dialogBuilder.setPositiveButton("OK") { _, _ ->
            if (selectedVehicle != null) {
                binding.tvVehicle.text = selectedVehicle
            } else {
                Toast.makeText(requireContext(), "No vehicle selected", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Cancel", null)
        dialogBuilder.show()
    }

    private fun saveBooking(paymentMethod: String, vehicle: String, serviceId: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                updateAvailability(args.serviceDate, args.serviceTime, serviceId)
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val username = documents.first().getString("username") ?: "Unknown User"

                val bookingData = mapOf(
                    "serviceName" to args.serviceName,
                    "servicePrice" to args.servicePrice,
                    "serviceDate" to args.serviceDate,
                    "serviceTime" to args.serviceTime,
                    "paymentMethod" to paymentMethod,
                    "vehicle" to vehicle,
                    "username" to username,
                    "userId" to userEmail,
                    "status" to "Booked",
                    "serviceDuration" to args.serviceDuration,
                    "service_id" to args.serviceId
                )

                db.collection("bookings")
                    .add(bookingData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Booking confirmed and saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save booking", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAvailability(serviceDate: String, serviceTime: String, serviceId: String) {
        db.collection("availability")
            .whereEqualTo("date", serviceDate)
            .whereEqualTo("timeSlot", serviceTime)
            .whereEqualTo("service_id", serviceId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(
                        requireContext(),
                        "No matching availability found for $serviceDate at $serviceTime",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    for (document in documents) {
                        db.collection("availability").document(document.id)
                            .update("isAvailable", false)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Availability updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to update availability: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch availability data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
