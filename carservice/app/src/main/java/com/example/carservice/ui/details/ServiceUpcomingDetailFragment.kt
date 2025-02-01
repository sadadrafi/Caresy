package com.example.carservice.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.carservice.R
import com.example.carservice.databinding.FragmentServiceUpcomingDetailBinding

class ServiceUpcomingDetailFragment : Fragment() {

    private var _binding: FragmentServiceUpcomingDetailBinding? = null
    private val binding get() = _binding!!

    // Menggunakan Safe Args untuk menerima data
    private val args: ServiceUpcomingDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceUpcomingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menampilkan data yang diterima ke UI
        binding.apply {
            textViewBookingId.text = args.bookingId
            textViewServiceName.text = args.serviceName
            textViewSelectedDate.text = args.serviceDate
            textViewSelectedTime.text = args.serviceTime
            textViewVehicle.text = args.vehicle
            textViewServiceDuration.text = args.serviceDuration
            textViewPrice.text = args.servicePrice
            textViewSelectedPaymentMethod.text = args.paymentMethod

            if (args.status == "In Progress") {
                buttonCancelBooking.visibility = View.GONE
            }

            buttonReturnToHome.setOnClickListener {
                findNavController().navigate(R.id.action_serviceUpcomingDetailFragment_to_homeFragment)
            }

            buttonCancelBooking.setOnClickListener {
                val action = ServiceUpcomingDetailFragmentDirections
                .actionServiceUpcomingDetailFragmentToCancelBookingFragment(
                    bookingId = args.bookingId,
                    serviceName = args.serviceName,
                    serviceDate = args.serviceDate,
                    serviceTime = args.serviceTime
                )
                findNavController().navigate(action)}
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
