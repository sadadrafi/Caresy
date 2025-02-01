package com.example.carservice.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carservice.adapter.UpcomingAppointmentsAdapter
import com.example.carservice.databinding.FragmentHomeBinding
import com.example.carservice.ui.booking.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UpcomingAppointmentsAdapter
    private val appointmentList = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        adapter = UpcomingAppointmentsAdapter(appointmentList) { booking ->
            navigateToServiceUpcomingDetail(booking)
        }
        binding.rvUpcomingAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingAppointments.adapter = adapter

        // Load appointments
        fetchUpcomingAppointments()
    }

    private fun fetchUpcomingAppointments() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        Log.d("HomeFragment", "User email: $userEmail")


        if (userEmail == null) {
            // User not logged in
            return
        }

        db.collection("bookings")
            .whereEqualTo("userId", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                appointmentList.clear()
                for (doc in documents) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id // Include Firestore ID
                    booking.status = doc.getString("status") ?: "No Status"
                    booking.serviceDuration = doc.getString("serviceDuration") ?: "No Duration"
                    booking.paymentMethod = doc.getString("paymentMethod") ?: "No Payment Method"

                    // Filter: Only add bookings that are not "Finished"
                    if (booking.status != "Finished" && booking.status != "Cancelled") {
                        appointmentList.add(booking)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error fetching appointments: ", exception)
            }
    }

    private fun navigateToServiceUpcomingDetail(booking: Booking) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToServiceUpcomingDetailFragment(
                bookingId = booking.bookingId,
                serviceName = booking.serviceName,
                serviceDate = booking.serviceDate,
                serviceTime = booking.serviceTime,
                vehicle = booking.vehicle,
                username = booking.username,
                paymentMethod = booking.paymentMethod,
                serviceDuration = booking.serviceDuration,
                servicePrice = booking.servicePrice,
                serviceDescription = booking.serviceDescription,
                serviceId = booking.serviceId,
                status = booking.status ?: "No Status"
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
