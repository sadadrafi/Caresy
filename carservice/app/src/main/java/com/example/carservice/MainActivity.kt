package com.example.carservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.carservice.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup BottomNavigationView
        val navView: BottomNavigationView = binding.navView

        // Setup NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Konfigurasi AppBar dengan Top-Level Destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, // Halaman Home
                R.id.navigation_services, // Halaman Services
                R.id.navigation_appointments, // Halaman Appointments
                R.id.navigation_profile // Halaman Profile
            )
        )

        // Setup ActionBar dan NavigationView
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // Tangani tombol back pada toolbar
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
