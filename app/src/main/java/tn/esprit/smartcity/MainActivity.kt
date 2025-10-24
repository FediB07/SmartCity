package tn.esprit.smartcity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tn.esprit.smartcity.Utilisateurs.view.WelcomeActivity
import tn.esprit.smartcity.Utilisateurs.view.DashboardActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val next = if (currentUser != null) {
            Intent(this, DashboardActivity::class.java).apply {
                putExtra("USER_NAME", currentUser.displayName ?: "")
                putExtra("USER_EMAIL", currentUser.email ?: "")
            }
        } else {
            Intent(this, WelcomeActivity::class.java)
        }
        startActivity(next)
        finish()
    }
}