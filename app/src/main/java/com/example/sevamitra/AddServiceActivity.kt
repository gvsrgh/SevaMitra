package com.example.sevamitra

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddServiceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var etTitle: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etArea: TextInputEditText
    private lateinit var etPriceRange: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var cbAllowCall: CheckBox
    private lateinit var cbAllowWhatsapp: CheckBox
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        etTitle = findViewById(R.id.etTitle)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etArea = findViewById(R.id.etArea)
        etPriceRange = findViewById(R.id.etPriceRange)
        etPhone = findViewById(R.id.etPhone)
        etDescription = findViewById(R.id.etDescription)
        cbAllowCall = findViewById(R.id.cbAllowCall)
        cbAllowWhatsapp = findViewById(R.id.cbAllowWhatsapp)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Setup category spinner
        val categories = arrayOf("Plumber", "Electrician", "Carpenter", "AC", "Painter")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnSubmit.setOnClickListener {
            addService()
        }
    }

    private fun addService() {
        val title = etTitle.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val area = etArea.text.toString().trim()
        val priceRange = etPriceRange.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val allowCall = cbAllowCall.isChecked
        val allowWhatsapp = cbAllowWhatsapp.isChecked

        if (title.isEmpty()) {
            etTitle.error = "Title required"
            return
        }

        if (area.isEmpty()) {
            etArea.error = "Area required"
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Phone required"
            return
        }

        val userId = auth.currentUser?.uid ?: return

        btnSubmit.isEnabled = false

        // Get provider name from Firestore
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val providerName = document.getString("name") ?: "Unknown"

                val service = Service(
                    providerId = userId,
                    providerName = providerName,
                    category = category,
                    title = title,
                    area = area,
                    priceRange = priceRange,
                    rating = 0f,
                    phone = phone,
                    allowCall = allowCall,
                    allowWhatsapp = allowWhatsapp,
                    description = description,
                    createdAt = System.currentTimeMillis()
                )

                firestore.collection("services")
                    .add(service)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Service added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add service: ${e.message}", Toast.LENGTH_SHORT).show()
                        btnSubmit.isEnabled = true
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
                btnSubmit.isEnabled = true
            }
    }
}
