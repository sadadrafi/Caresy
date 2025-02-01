package com.example.carservice.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.carservice.databinding.FragmentHistoryDetailBinding
import com.example.carservice.ui.booking.Booking
import com.google.firebase.firestore.FirebaseFirestore

class HistoryDetailFragment : Fragment() {

    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = HistoryDetailFragmentArgs.fromBundle(requireArguments())
        val bookingId = args.bookingId
        loadBookingDetails(bookingId)
    }

    private fun loadBookingDetails(bookingId: String) {
        db.collection("bookings")
            .document(bookingId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val booking = document.toObject(Booking::class.java)
                    if (booking != null) {
                        displayBookingDetails(booking)
                    }
                } else {
                    binding.tvError.text = "Booking not found."
                    binding.tvError.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                binding.tvError.text = "Failed to load booking details."
                binding.tvError.visibility = View.VISIBLE
            }
    }

    private fun displayBookingDetails(booking: Booking) {
        binding.apply {
            tvServiceName.text = booking.serviceName
            tvServiceDate.text = booking.serviceDate
            tvServiceTime.text = booking.serviceTime
            tvVehicle.text = booking.vehicle
            tvServicePrice.text = booking.servicePrice
            tvServiceDuration.text = booking.serviceDuration
            tvSelectedPaymentMethod.text = booking.paymentMethod

            tvError.visibility = View.GONE
            detailsContainer.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
