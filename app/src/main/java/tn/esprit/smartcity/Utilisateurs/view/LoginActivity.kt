package tn.esprit.smartcity.Utilisateurs.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tn.esprit.smartcity.R
import tn.esprit.smartcity.Utilisateurs.viewmodel.AuthResult
import tn.esprit.smartcity.Utilisateurs.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.coroutines.launch

/**
 * LoginActivity handles user login
 */
class LoginActivity : AppCompatActivity() {
    
    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var googleLoginButton: MaterialButton
    private lateinit var facebookLoginButton: MaterialButton
    private lateinit var credentialManager: CredentialManager
    private lateinit var callbackManager: CallbackManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)
        
        initializeViews()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        googleLoginButton = findViewById(R.id.googleLoginButton)
        facebookLoginButton = findViewById(R.id.facebookLoginButton)
        credentialManager = CredentialManager.create(this)
        callbackManager = CallbackManager.Factory.create()
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            clearErrors()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        findViewById<android.widget.TextView>(R.id.goToSignupTextView).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        facebookLoginButton.setOnClickListener {
            signInWithFacebook()
        }
    }
    
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.login_success, result.user.fullName),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Navigate to Dashboard
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_NAME", result.user.fullName)
                    intent.putExtra("USER_EMAIL", result.user.email)
                    startActivity(intent)
                    finish()
                }
                is AuthResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                null -> {
                    // No result yet
                }
            }
        }
    }
    
    private fun clearErrors() {
        emailInputLayout.error = null
        passwordInputLayout.error = null
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.resetLoginResult()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val name = currentUser.displayName ?: ""
            val email = currentUser.email ?: ""
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", name)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
            finish()
        }
    }

    private fun signInWithGoogle() {
        val serverClientId = getString(R.string.default_web_client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val name = user?.displayName ?: ""
                        val email = user?.email ?: ""
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success, name), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.putExtra("USER_NAME", name)
                        intent.putExtra("USER_EMAIL", email)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@LoginActivity, e.message ?: "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message ?: "Google sign-in canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val name = user?.displayName ?: ""
                        val email = user?.email ?: ""
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success, name), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.putExtra("USER_NAME", name)
                        intent.putExtra("USER_EMAIL", email)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@LoginActivity, e.message ?: "Facebook sign-in failed", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "Facebook sign-in canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@LoginActivity, error.message ?: "Facebook sign-in error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
