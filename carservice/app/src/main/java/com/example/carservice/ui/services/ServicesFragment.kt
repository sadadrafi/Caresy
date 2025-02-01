package com.example.carservice.ui.services

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carservice.R
import com.example.carservice.adapter.ServiceAdapter
import com.example.carservice.databinding.FragmentServicesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carservice.ui.services.toService

class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    // Declare adapters and data lists
    private lateinit var maintenanceAdapter: ServiceAdapter
    private lateinit var repairAdapter: ServiceAdapter
    private lateinit var inspectionAdapter: ServiceAdapter

    private val maintenanceServices = mutableListOf<Service>()
    private val repairServices = mutableListOf<Service>()
    private val inspectionServices = mutableListOf<Service>()

    companion object {
        const val CATEGORY_MAINTENANCE = "Maintenance"
        const val CATEGORY_REPAIR = "Repair"
        const val CATEGORY_INSPECTION = "Inspection"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Buat adapter dengan callback navigasi
        maintenanceAdapter = ServiceAdapter(requireContext(), maintenanceServices) { service ->
            navigateToServiceDetail(service)
        }
        repairAdapter = ServiceAdapter(requireContext(), repairServices) { service ->
            navigateToServiceDetail(service)
        }
        inspectionAdapter = ServiceAdapter(requireContext(), inspectionServices) { service ->
            navigateToServiceDetail(service)
        }

        binding.recyclerViewMaintenance.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = maintenanceAdapter
        }
        binding.recyclerViewRepair.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repairAdapter
        }
        binding.recyclerViewInspection.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inspectionAdapter
        }

        // Load services from Firestore
        loadServicesByCategory(CATEGORY_MAINTENANCE, maintenanceServices)
        loadServicesByCategory(CATEGORY_REPAIR, repairServices)
        loadServicesByCategory(CATEGORY_INSPECTION, inspectionServices)

        // Reference the ScrollView
        val scrollView: NestedScrollView = root.findViewById(R.id.scrollView)

        // Reference TextViews for each category
        val maintenanceTextView = binding.titleMaintenanceServices
        val repairTextView = binding.titleRepairServices
        val inspectionTextView = binding.titleInspectionsServices

        // Set click listeners for each category
        binding.maintenanceCategory.setOnClickListener {
            scrollView.smoothScrollTo(0, maintenanceTextView.top)
        }

        binding.repairCategory.setOnClickListener {
            scrollView.smoothScrollTo(0, repairTextView.top)
        }

        binding.inspectionCategory.setOnClickListener {
            scrollView.smoothScrollTo(0, inspectionTextView.top)
        }

        return root
    }

    private fun navigateToServiceDetail(service: Service) {
        val action = ServicesFragmentDirections.actionServiceFragmentToServiceDetailFragment(
            bookingId = service.bookingId,
            serviceId = service.serviceId,
            serviceName = service.name,
            serviceDescription = service.description,
            servicePrice = service.price,
            serviceDuration = service.duration,
            serviceDate = service.selectedDate,
            serviceTime = service.selectedTime,
            vehicle = service.vehicle,
            username = service.username,
            paymentMethod = service.paymentMethod
        )

        findNavController().navigate(action)
    }

    private fun loadServicesByCategory(category: String, serviceList: MutableList<Service>) {
        Log.d("Firestore", "Fetching data for category: $category")

        firestore.collection("services")
            .whereEqualTo("category", category)
            .whereEqualTo("availability", true) // Tambahkan filter untuk availability
            .get()
            .addOnSuccessListener { documents ->
                Log.d("Firestore", "Documents fetched for $category: ${documents.size()}")

                serviceList.clear()
                for (document in documents) {
                    Log.d("Firestore", "Document ID: ${document.id}, Data: ${document.data}")
                    val service = document.toService()
                    Log.d("Firestore", "Parsed Service: $service")
                    serviceList.add(service)
                }

                // Notify adapters
                when (category) {
                    CATEGORY_MAINTENANCE -> maintenanceAdapter.notifyDataSetChanged()
                    CATEGORY_REPAIR -> repairAdapter.notifyDataSetChanged()
                    CATEGORY_INSPECTION -> inspectionAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data for $category", exception)
                Toast.makeText(requireContext(), "Error loading $category services", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
