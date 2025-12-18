package com.example.sevamitra

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private val services: List<Service>,
    private val onServiceClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServiceTitle: TextView = itemView.findViewById(R.id.tvServiceTitle)
        val tvProviderName: TextView = itemView.findViewById(R.id.tvProviderName)
        val tvServiceArea: TextView = itemView.findViewById(R.id.tvServiceArea)
        val tvPriceRange: TextView = itemView.findViewById(R.id.tvPriceRange)

        fun bind(service: Service) {
            tvServiceTitle.text = service.title
            tvProviderName.text = service.providerName
            tvServiceArea.text = "Area: ${service.area}"
            tvPriceRange.text = "Price: ${service.priceRange}"

            itemView.setOnClickListener {
                onServiceClick(service)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size
}
