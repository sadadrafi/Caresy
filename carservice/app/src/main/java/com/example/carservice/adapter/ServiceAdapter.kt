package com.example.carservice.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carservice.R
import com.example.carservice.ui.services.Service

class ServiceAdapter(
    private val context: Context,
    private val services: List<Service>,
    private val onItemClicked: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceName: TextView = view.findViewById(R.id.serviceName)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescription)
        val servicePrice: TextView = view.findViewById(R.id.servicePrice)
        val serviceIcon: ImageView = view.findViewById(R.id.serviceIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        Log.d("Adapter", "Binding data: $service")

        holder.serviceName.text = service.name
        holder.serviceDescription.text = service.description
        holder.servicePrice.text = service.price

        // Pasang listener untuk klik item
        holder.itemView.setOnClickListener {
            onItemClicked(service)
        }
    }
    override fun getItemCount(): Int {
        Log.d("Adapter", "Item count: ${services.size}")
        return services.size
    }

}
