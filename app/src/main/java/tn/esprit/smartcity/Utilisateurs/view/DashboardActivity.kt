package tn.esprit.smartcity.Utilisateurs.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import tn.esprit.smartcity.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

/**
 * DashboardActivity - Placeholder dashboard after successful login
 */
class DashboardActivity : AppCompatActivity() {
    
    private lateinit var welcomeTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        
        initializeViews()
        displayUserInfo()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView)
        emailTextView = findViewById(R.id.emailTextView)
        logoutButton = findViewById(R.id.logoutButton)
    }
    
    private fun displayUserInfo() {
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        
        welcomeTextView.text = getString(R.string.welcome_message, userName)
        emailTextView.text = userEmail
    }
    
    private fun setupClickListeners() {
        logoutButton.setOnClickListener {
            // Sign out from Firebase and navigate back to login
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
