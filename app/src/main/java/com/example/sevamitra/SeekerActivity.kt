package com.example.sevamitra

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SeekerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnProfile: Button
    private lateinit var btnBack: Button

    private val categories = listOf("All", "Plumber", "Electrician", "Carpenter", "AC", "Painter")
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seeker)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerView)
        btnProfile = findViewById(R.id.btnProfile)
        btnBack = findViewById(R.id.btnBack)

        recyclerView.layoutManager = LinearLayoutManager(this)

        setupTabs()

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }

        loadServices("All")
    }

    private fun setupTabs() {
        categories.forEach { category ->
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentCategory = categories[it.position]
                    loadServices(currentCategory)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadServices(category: String) {
        var query: Query = firestore.collection("services")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        if (category != "All") {
            query = query.whereEqualTo("category", category)
        }

        query.get()
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
