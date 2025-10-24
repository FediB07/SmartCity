package tn.esprit.smartcity.Utilisateurs.view

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
import android.content.Intent
import kotlinx.coroutines.launch
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider

/**
 * SignupActivity handles user registration
 */
class SignupActivity : AppCompatActivity() {
    
    private val viewModel: AuthViewModel by viewModels()
    
    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var createAccountButton: MaterialButton
    private lateinit var googleSignupButton: MaterialButton
    private lateinit var facebookSignupButton: MaterialButton
    private lateinit var credentialManager: CredentialManager
    private lateinit var callbackManager: CallbackManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_new)
        
        initializeViews()
        setupClickListeners()
        observeViewModel()
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
                        val isNew = authResult.additionalUserInfo?.isNewUser == true
                        if (isNew) {
                            Toast.makeText(this@SignupActivity, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                            intent.putExtra("USER_NAME", name)
                            intent.putExtra("USER_EMAIL", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, getString(R.string.existing_account_signed_in), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@SignupActivity, e.message ?: "Facebook sign-in failed", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancel() {
                Toast.makeText(this@SignupActivity, "Facebook sign-in canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@SignupActivity, error.message ?: "Facebook sign-in error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun initializeViews() {
        fullNameInputLayout = findViewById(R.id.fullNameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        createAccountButton = findViewById(R.id.createAccountButton)
        googleSignupButton = findViewById(R.id.googleSignupButton)
        facebookSignupButton = findViewById(R.id.facebookSignupButton)
        credentialManager = CredentialManager.create(this)
        callbackManager = CallbackManager.Factory.create()
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
    
    private fun setupClickListeners() {
        createAccountButton.setOnClickListener {
            clearErrors()
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            
            viewModel.signup(fullName, email, password)
        }

        googleSignupButton.setOnClickListener {
            signInWithGoogle()
        }

        facebookSignupButton.setOnClickListener {
            signInWithFacebook()
        }

        findViewById<android.widget.TextView>(R.id.goToLoginTextView).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun observeViewModel() {
        viewModel.signupResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.signup_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Go back to login
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
                val result = credentialManager.getCredential(this@SignupActivity, request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val name = user?.displayName ?: ""
                        val email = user?.email ?: ""
                        val isNew = authResult.additionalUserInfo?.isNewUser == true
                        if (isNew) {
                            Toast.makeText(this@SignupActivity, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                            intent.putExtra("USER_NAME", name)
                            intent.putExtra("USER_EMAIL", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, getString(R.string.existing_account_signed_in), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@SignupActivity, e.message ?: "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this@SignupActivity, e.message ?: "Google sign-in canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearErrors() {
        fullNameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.resetSignupResult()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
