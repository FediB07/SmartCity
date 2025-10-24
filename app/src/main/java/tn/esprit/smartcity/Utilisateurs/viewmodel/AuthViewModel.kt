package tn.esprit.smartcity.Utilisateurs.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tn.esprit.smartcity.Utilisateurs.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * AuthViewModel handles authentication business logic
 * Communicates between View and Repository
 */
class AuthViewModel : ViewModel() {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    // LiveData for login result
    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult
    
    // LiveData for signup result
    private val _signupResult = MutableLiveData<AuthResult>()
    val signupResult: LiveData<AuthResult> = _signupResult
    
    /**
     * Attempt to login with email and password
     */
    fun login(email: String, password: String) {
        // Validate inputs
        when {
            email.isBlank() -> {
                _loginResult.value = AuthResult.Error("Email is required")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _loginResult.value = AuthResult.Error("Invalid email format")
                return
            }
            password.isBlank() -> {
                _loginResult.value = AuthResult.Error("Password is required")
                return
            }
        }
        
        // Firebase login
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val fbUser = result.user
                val displayName = fbUser?.displayName ?: ""
                val u = User(fullName = displayName, email = fbUser?.email ?: email)
                _loginResult.value = AuthResult.Success(u)
            }
            .addOnFailureListener { e ->
                _loginResult.value = AuthResult.Error(e.message ?: "Login failed")
            }
    }
    
    /**
     * Attempt to register a new user
     */
    fun signup(fullName: String, email: String, password: String) {
        // Validate inputs
        when {
            fullName.isBlank() -> {
                _signupResult.value = AuthResult.Error("Full name is required")
                return
            }
            email.isBlank() -> {
                _signupResult.value = AuthResult.Error("Email is required")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _signupResult.value = AuthResult.Error("Invalid email format")
                return
            }
            password.isBlank() -> {
                _signupResult.value = AuthResult.Error("Password is required")
                return
            }
            password.length < 6 -> {
                _signupResult.value = AuthResult.Error("Password must be at least 6 characters")
                return
            }
        }
        
        // Firebase email/password registration
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                result.user?.updateProfile(profileUpdates)
                val u = User(fullName = fullName, email = email)
                _signupResult.value = AuthResult.Success(u)
            }
            .addOnFailureListener { e ->
                _signupResult.value = AuthResult.Error(e.message ?: "Signup failed")
            }
    }
    
    /**
     * Reset login result
     */
    fun resetLoginResult() {
        _loginResult.value = null
    }
    
    /**
     * Reset signup result
     */
    fun resetSignupResult() {
        _signupResult.value = null
    }
}

/**
 * Sealed class representing authentication results
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
