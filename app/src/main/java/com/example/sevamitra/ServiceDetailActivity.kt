package com.example.sevamitra

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ServiceDetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var btnBack: Button
    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvProviderName: TextView
    private lateinit var tvArea: TextView
    private lateinit var tvPriceRange: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnCall: Button
    private lateinit var btnWhatsApp: Button

    private var service: Service? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        firestore = FirebaseFirestore.getInstance()

        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvCategory = findViewById(R.id.tvCategory)
        tvProviderName = findViewById(R.id.tvProviderName)
        tvArea = findViewById(R.id.tvArea)
        tvPriceRange = findViewById(R.id.tvPriceRange)
        tvPhone = findViewById(R.id.tvPhone)
        tvDescription = findViewById(R.id.tvDescription)
        btnCall = findViewById(R.id.btnCall)
        btnWhatsApp = findViewById(R.id.btnWhatsApp)

        btnBack.setOnClickListener {
            finish()
        }

        val serviceId = intent.getStringExtra("SERVICE_ID")
        if (serviceId != null) {
            loadServiceDetails(serviceId)
        } else {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadServiceDetails(serviceId: String) {
        firestore.collection("services").document(serviceId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    service = document.toObject(Service::class.java)?.copy(id = document.id)
                    service?.let { displayService(it) }
                } else {
                    Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load service: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun displayService(service: Service) {
        tvTitle.text = service.title
        tvCategory.text = service.category
        tvProviderName.text = service.providerName
        tvArea.text = "Area: ${service.area}"
        tvPriceRange.text = "Price: ${service.priceRange}"
        tvPhone.text = "Phone: ${service.phone}"
        tvDescription.text = service.description

        // Show/hide buttons based on permissions
        btnCall.visibility = if (service.allowCall) View.VISIBLE else View.GONE
        btnWhatsApp.visibility = if (service.allowWhatsapp) View.VISIBLE else View.GONE

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${service.phone}")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Cannot open dialer", Toast.LENGTH_SHORT).show()
            }
        }

        btnWhatsApp.setOnClickListener {
            val phoneNumber = service.phone.replace("[^0-9]".toRegex(), "")
            val url = "https://wa.me/91$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
