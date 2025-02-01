package com.example.carservice.ui.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.carservice.databinding.FragmentServiceDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ServiceDetailFragment : Fragment() {

    private var _binding: FragmentServiceDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ServiceDetailFragmentArgs.fromBundle(requireArguments())
        val serviceId = args.serviceId

        bindServiceDetails(args)
        fetchAvailabilityData(serviceId)
        setupContinueButton(args)
    }

    private fun bindServiceDetails(args: ServiceDetailFragmentArgs) {
        binding.apply {
            tvServiceName.text = args.serviceName
            tvServiceDescription.text = args.serviceDescription
            tvServicePrice.text = args.servicePrice
            tvTimeLabel.text = args.serviceDuration
        }
    }

    private fun fetchAvailabilityData(serviceId: String) {
        db.collection("availability")
            .whereEqualTo("service_id", serviceId)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { documents ->
                val dateMap = organizeAvailabilityData(documents)
                populateDateSpinner(dateMap)
            }
            .addOnFailureListener { exception ->
                showToast("Error: ${exception.message}")
            }
    }

    private fun organizeAvailabilityData(documents: Iterable<DocumentSnapshot>): MutableMap<String, MutableList<String>> {
        val dateMap = mutableMapOf<String, MutableList<String>>()
        documents.forEach { doc ->
            val date = doc.getString("date") ?: "Unknown Date"
            val timeSlot = doc.getString("timeSlot") ?: "Unknown Time"
            dateMap.getOrPut(date) { mutableListOf() }.add(timeSlot)
        }
        return dateMap
    }

    private fun populateDateSpinner(dateMap: Map<String, List<String>>) {
        val dates = dateMap.keys.toList()
        val dateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dates).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerDate.adapter = dateAdapter

        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val serviceDate = dates[position]
                populateTimeSpinner(dateMap[serviceDate] ?: emptyList())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun populateTimeSpinner(timeSlots: List<String>) {
        val timeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeSlots).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerTime.adapter = timeAdapter
    }

    private fun setupContinueButton(args: ServiceDetailFragmentArgs) {
        binding.btnContinue.setOnClickListener {
            val serviceDate = binding.spinnerDate.selectedItem?.toString()
            val serviceTime = binding.spinnerTime.selectedItem?.toString()

            if (serviceDate != null && serviceTime != null) {
                navigateToBookingConfirmation(args, serviceDate, serviceTime)
            } else {
                showToast("Please select both date and time")
            }
        }
    }

    private fun navigateToBookingConfirmation(args: ServiceDetailFragmentArgs, serviceDate: String, serviceTime: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail == null) {
            showToast("User not logged in")
            return
        }

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    showToast("User data not found")
                    return@addOnSuccessListener
                }

                val username = documents.first().getString("username") ?: "Unknown User"

                val action = ServiceDetailFragmentDirections.actionServiceDetailFragmentToBookingConfirmationFragment(
                    userId = userEmail,
                    username = username,
                    serviceName = args.serviceName,
                    serviceDate = serviceDate,
                    serviceTime = serviceTime,
                    servicePrice = args.servicePrice,
                    serviceDuration = args.serviceDuration,
                    vehicle = args.vehicle,
                    serviceId = args.serviceId
                )
                findNavController().navigate(action)
            }
            .addOnFailureListener {
                showToast("Failed to fetch user data")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
