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
import com.example.carservice.databinding.FragmentBookingSuccessBinding

class BookingSuccessFragment : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!
    private val args: BookingSuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display booking details
        binding.apply {
            tvServiceName.text = args.confirmationServiceName
            tvVehicle.text = args.vehicle
            tvSelectedDateTime.text = "${args.confirmationSelectedDate} at ${args.confirmationSelectedTime}"
            tvServicePrice.text = args.confirmationServicePrice
        }

        // Set button actions
        setupButtonActions()
    }

    private fun setupButtonActions() {
        binding.apply {
            btnBookAnother.setOnClickListener {
                findNavController().navigate(R.id.action_bookingSuccessFragment_to_servicesFragment)
            }

            btnViewBookingDetails.setOnClickListener {
                findNavController().navigate(R.id.action_bookingSuccessFragment_to_homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
