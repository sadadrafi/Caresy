package com.example.carservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.carservice.R
import com.example.carservice.ui.booking.Booking

class UpcomingAppointmentsAdapter(
    private val appointments: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<UpcomingAppointmentsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus) // Added for status

        fun bind(booking: Booking) {
            tvServiceName.text = booking.serviceName
            tvDateTime.text = "${booking.serviceDate} at ${booking.serviceTime}"
            tvStatus.text = booking.status

            // Set background color based on booking status
            val statusColor = when (booking.status) {
                "Booked" -> ContextCompat.getColor(itemView.context, R.color.status_booked)
                "In Progress" -> ContextCompat.getColor(itemView.context, R.color.status_in_progress)
                "Finished" -> ContextCompat.getColor(itemView.context, R.color.status_finished)
                else -> ContextCompat.getColor(itemView.context, R.color.status_booked) // Default
            }
            tvStatus.setBackgroundColor(statusColor)

            // Set click listener
            itemView.setOnClickListener { onItemClick(booking) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size
}
