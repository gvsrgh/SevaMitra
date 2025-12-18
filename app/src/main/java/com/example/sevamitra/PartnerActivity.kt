package com.example.sevamitra

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PartnerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddService: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        btnAddService = findViewById(R.id.btnAddService)
        btnLogout = findViewById(R.id.btnLogout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAddService.setOnClickListener {
            startActivity(Intent(this, AddServiceActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadMyServices()
    }

    private fun loadMyServices() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("services")
            .whereEqualTo("providerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val services = documents.mapNotNull { doc ->
                    doc.toObject(Service::class.java).copy(id = doc.id)
                }
                displayServices(services)
            }
            .addOnFailureListener {
                displayServices(emptyList())
            }
    }

    private fun displayServices(services: List<Service>) {
        val adapter = ServiceAdapter(services) { service ->
            val intent = Intent(this, ServiceDetailActivity::class.java)
            intent.putExtra("SERVICE_ID", service.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
