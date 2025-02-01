package com.example.carservice.ui.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.carservice.databinding.FragmentCancelSuccessBinding

@Suppress("DEPRECATION")
class CancelSuccessFragment : Fragment() {

    private var _binding: FragmentCancelSuccessBinding? = null
    private val binding get() = _binding!!

    private val args: CancelSuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan informasi booking yang dibatalkan
        binding.textViewServiceName.text = args.serviceName
        binding.textViewServiceDate.text = args.serviceDate
        binding.textViewServiceTime.text = args.serviceTime

        // Tambahkan tombol untuk kembali ke halaman utama
        binding.buttonBackToHome.setOnClickListener {
            // Navigasi ke halaman utama (main page)
            val action = CancelSuccessFragmentDirections
                .actionCancelSuccessFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
