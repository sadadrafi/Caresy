package com.example.carservice.ui.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.carservice.databinding.FragmentCancelBookingBinding
import com.google.firebase.firestore.FirebaseFirestore

class CancelBookingFragment : Fragment() {

    private var _binding: FragmentCancelBookingBinding? = null
    private val binding get() = _binding!!

    // Menggunakan Safe Args untuk menerima data
    private val args: CancelBookingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan booking ID (atau gunakan untuk logika lain)
        val bookingId = args.bookingId
        binding.textViewServiceName.text = args.serviceName
        binding.textViewServiceDate.text = args.serviceDate
        binding.textViewServiceTime.text = args.serviceTime

        // Tombol untuk mengubah status booking
        binding.buttonConfirmCancel.setOnClickListener {
            cancelBooking(bookingId)
        }
    }

    private fun cancelBooking(bookingId: String) {
        val db = FirebaseFirestore.getInstance()

        // Ubah status booking menjadi "Cancelled" di koleksi "bookings"
        db.collection("bookings").document(bookingId)
            .update("status", "Cancelled")
            .addOnSuccessListener {
                // Setelah status berhasil diubah, kembalikan isAvailable di koleksi "availability"
                val availabilityQuery = db.collection("availability")
                    .whereEqualTo("date", args.serviceDate) // Cocokkan tanggal
                    .whereEqualTo("timeSlot", args.serviceTime) // Cocokkan slot waktu

                availabilityQuery.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            Toast.makeText(
                                requireContext(),
                                "Tidak dapat menemukan data ketersediaan.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        }

                        for (document in querySnapshot) {
                            // Perbarui field isAvailable menjadi true
                            document.reference.update("isAvailable", true)
                                .addOnSuccessListener {
                                    // Navigasi ke CancelSuccessFragment jika pembaruan berhasil
                                    val action = CancelBookingFragmentDirections
                                        .actionCancelBookingFragmentToCancelSuccessFragment(
                                            serviceName = args.serviceName,
                                            serviceDate = args.serviceDate,
                                            serviceTime = args.serviceTime
                                        )
                                    findNavController().navigate(action)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Gagal memperbarui ketersediaan.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Gagal memuat data ketersediaan.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal membatalkan booking.", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
