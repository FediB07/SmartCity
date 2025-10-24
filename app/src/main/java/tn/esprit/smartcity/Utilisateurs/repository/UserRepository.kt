package tn.esprit.smartcity.Utilisateurs.repository

import tn.esprit.smartcity.Utilisateurs.model.User

/**
 * UserRepository manages user data in memory
 * This is a singleton repository for managing user authentication
 */
class UserRepository private constructor() {
    
    private val users = mutableListOf<User>()
    
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        
        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository().also { instance = it }
            }
        }
    }
    
    /**
     * Register a new user
     * @param user The user to register
     * @return true if registration successful, false if email already exists
     */
    fun register(user: User): Boolean {
        // Check if email already exists
        if (users.any { it.email.equals(user.email, ignoreCase = true) }) {
            return false
        }
        
        users.add(user)
        return true
    }
    
    /**
     * Login with email (legacy stub)
     * User model no longer stores passwords; this repo is kept only for compile compatibility.
     * @return User if an account with the email exists, otherwise null
     */
    fun login(email: String, password: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }
    
    /**
     * Check if an email is already registered
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    fun emailExists(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }
    
    /**
     * Get all registered users (for debugging purposes)
     */
    fun getAllUsers(): List<User> = users.toList()
}
