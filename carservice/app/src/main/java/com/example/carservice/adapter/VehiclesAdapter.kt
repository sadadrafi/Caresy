package com.example.carservice.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carservice.R
import com.example.carservice.model.Vehicle

class VehiclesAdapter(
    private var vehicles: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateVehicles(newVehicles: List<Vehicle>) {
        this.vehicles = newVehicles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)
        holder.itemView.setOnClickListener { onItemClick(vehicle) }
    }

    override fun getItemCount(): Int = vehicles.size

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val modelTextView: TextView = itemView.findViewById(R.id.tvModel)
        private val licensePlateTextView: TextView = itemView.findViewById(R.id.tvLicensePlate)

        fun bind(vehicle: Vehicle) {
            modelTextView.text = vehicle.model.ifEmpty {
                itemView.context.getString(R.string.unknown_model)
            }
            licensePlateTextView.text = vehicle.licensePlate.ifEmpty {
                itemView.context.getString(R.string.unknown_plate)
            }
        }
    }
}
