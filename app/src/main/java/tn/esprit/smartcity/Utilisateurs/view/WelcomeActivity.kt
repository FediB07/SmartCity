                package tn.esprit.smartcity.Utilisateurs.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tn.esprit.smartcity.R
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * WelcomeActivity - First screen users see when opening the app
 * Get Started or Sign In options
 */
class WelcomeActivity : AppCompatActivity() {
    
    private lateinit var getStartedButton: MaterialButton
    private lateinit var signInTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View
    private lateinit var languageButton: ImageButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        initializeViews()
        setupPager()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        getStartedButton = findViewById(R.id.getStartedButton)
        signInTextView = findViewById(R.id.signInTextView)
        viewPager = findViewById(R.id.welcomeViewPager)
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)
        languageButton = findViewById(R.id.languageButton)
    }
    
    private fun setupClickListeners() {
        // Get Started button navigates to Sign Up
        getStartedButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        
        // Sign In text navigates to Login
        signInTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        languageButton.setOnClickListener {
            showLanguagePicker()
        }
    }

    private fun setupPager() {
        val pages = listOf(
            WelcomePage(
                title = getString(R.string.welcome_title),
                subtitle = getString(R.string.welcome_subtitle)
            ),
            WelcomePage(
                title = getString(R.string.welcome_page2_title),
                subtitle = getString(R.string.welcome_page2_subtitle)
            ),
            WelcomePage(
                title = getString(R.string.welcome_page3_title),
                subtitle = getString(R.string.welcome_page3_subtitle)
            )
        )
        viewPager.adapter = WelcomePagerAdapter(pages)
        updateDots(0)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })
    }

    private fun showLanguagePicker() {
        val items = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_french),
            getString(R.string.language_arabic)
        )
        AlertDialog.Builder(this)
            .setTitle(R.string.change_language)
            .setItems(items) { _, which ->
                val tag = when (which) {
                    0 -> "en"
                    1 -> "fr"
                    2 -> "ar"
                    else -> "en"
                }
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                recreate()
            }
            .show()
    }

    private fun updateDots(position: Int) {
        val active = R.drawable.dot_indicator_active
        val inactive = R.drawable.dot_indicator_inactive
        dot1.setBackgroundResource(if (position == 0) active else inactive)
        dot2.setBackgroundResource(if (position == 1) active else inactive)
        dot3.setBackgroundResource(if (position == 2) active else inactive)
    }

    data class WelcomePage(val title: String, val subtitle: String)

    private class WelcomePagerAdapter(
        private val data: List<WelcomePage>
    ) : RecyclerView.Adapter<WelcomePagerAdapter.VH>() {
        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.pageTitleTextView)
            val subtitle: TextView = view.findViewById(R.id.pageSubtitleTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_welcome_page, parent, false)
            return VH(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val page = data[position]
            holder.title.text = page.title
            holder.subtitle.text = page.subtitle
        }
    }
}
