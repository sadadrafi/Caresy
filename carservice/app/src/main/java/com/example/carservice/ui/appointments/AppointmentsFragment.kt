package com.example.carservice.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carservice.adapter.UpcomingAppointmentsAdapter
import com.example.carservice.databinding.FragmentAppointmentsBinding
import com.example.carservice.ui.booking.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentsFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UpcomingAppointmentsAdapter
    private val finishedAppointments = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        adapter = UpcomingAppointmentsAdapter(finishedAppointments) { booking ->
            navigateToHistoryDetail(booking)
        }
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        // Load finished appointments
        fetchFinishedAppointments()
    }

    private fun fetchFinishedAppointments() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        Log.d("AppointmentsFragment", "User email: $userEmail")

        if (userEmail == null) {
            // User not logged in
            return
        }

        db.collection("bookings")
            .whereEqualTo("userId", userEmail)
            .whereIn("status", listOf("Finished", "Cancelled"))
            .get()
            .addOnSuccessListener { documents ->
                finishedAppointments.clear()
                for (doc in documents) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id
                    finishedAppointments.add(booking)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("AppointmentsFragment", "Error fetching appointments: ", exception)
            }
    }

    private fun navigateToHistoryDetail(booking: Booking) {
        val action = AppointmentsFragmentDirections
            .actionAppointmentsFragmentToHistoryDetailFragment(
                bookingId = booking.bookingId
            )
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}