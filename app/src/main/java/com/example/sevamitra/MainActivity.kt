package com.example.sevamitra

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // Not logged in, go to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Logged in, check role
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") ?: "SEEKER"
                        navigateToRoleActivity(role)
                    } else {
                        // No user data, go to login
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    // Error, go to login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }

    private fun navigateToRoleActivity(role: String) {
        val intent = when (role) {
            "SEEKER" -> Intent(this, SeekerActivity::class.java)
            "PARTNER" -> Intent(this, PartnerActivity::class.java)
            else -> Intent(this, SeekerActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
