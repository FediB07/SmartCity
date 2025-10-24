# Smart City - User Authentication Module (Utilisateurs)

## Overview
This module provides complete user authentication functionality for the Smart City Android application, implemented using MVVM (Model-View-ViewModel) architecture with Kotlin.

## Project Structure

```
Utilisateurs/
├── model/
│   └── User.kt                    # User data class
├── repository/
│   └── UserRepository.kt          # In-memory user data management
├── viewmodel/
│   └── AuthViewModel.kt           # Authentication business logic
├── view/
│   ├── LoginActivity.kt           # Login screen
│   ├── SignupActivity.kt          # Registration screen
│   └── DashboardActivity.kt       # Placeholder dashboard
└── README.md                      # This file
```

## Features

### 1. User Login (`LoginActivity`)
- Email and password input fields
- Input validation (email format, required fields)
- Error handling with toast messages
- "Remember me" checkbox
- "Forgot password" link (placeholder)
- Social login buttons (Google, Facebook, GitHub - placeholders)
- Navigation to Dashboard on success
- Material Design UI matching app branding

### 2. User Registration (`SignupActivity`)
- Full name, email, and password input fields
- Comprehensive input validation:
  - Non-empty fields
  - Valid email format
  - Password minimum length (6 characters)
  - Email uniqueness check
- Terms & Conditions checkbox
- Social signup buttons (placeholders)
- Navigation back to Login on success

### 3. Dashboard (`DashboardActivity`)
- Welcome message with user's name
- User email display
- Logout functionality
- Placeholder for integration with other team modules

## Architecture Components

### Model Layer
**`User.kt`**
```kotlin
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String
)
```

### Repository Layer
**`UserRepository.kt`**
- Singleton pattern for application-wide access
- In-memory storage (suitable for development/testing)
- Methods:
  - `register(user: User): Boolean` - Register new user
  - `login(email: String, password: String): User?` - Authenticate user
  - `emailExists(email: String): Boolean` - Check email availability

### ViewModel Layer
**`AuthViewModel.kt`**
- Extends AndroidX ViewModel
- Uses LiveData for reactive updates
- Input validation logic
- Communication bridge between View and Repository
- Methods:
  - `login(email: String, password: String)`
  - `signup(fullName: String, email: String, password: String)`
  - `resetLoginResult()` / `resetSignupResult()`

### View Layer
Activities use Material Design 3 components with:
- TextInputLayout for form fields
- MaterialButton for actions
- MaterialCardView for content containers
- Proper error handling and user feedback

## Resources

### Colors (`res/values/colors.xml`)
- `primary_blue` (#1E88E5) - Primary brand color
- `primary_cyan` (#00BCD4) - Secondary brand color
- `background_gradient` (#E8F4F8) - Screen background
- `text_primary` (#212121) - Primary text
- `text_secondary` (#757575) - Secondary text
- `icon_tint` (#9E9E9E) - Icon colors
- `error_color` (#F44336) - Error messages

### Strings (`res/values/strings.xml`)
All UI text is externalized to `strings.xml` for:
- Easy localization
- Consistent messaging
- Maintenance

### Layouts
- `activity_login.xml` - Login screen with tabbed interface
- `activity_signup.xml` - Registration screen with tabbed interface
- `activity_dashboard.xml` - Post-login dashboard placeholder

### Drawables
- `bg_logo.xml` - Gradient background for app logo

## Integration Guide

### 1. Add to AndroidManifest.xml
```xml
<activity
    android:name="tn.esprit.smartcity.Utilisateurs.view.LoginActivity"
    android:exported="true"
    android:theme="@style/Theme.SmartCity">
    <!-- Optional: Set as launcher activity -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name="tn.esprit.smartcity.Utilisateurs.view.SignupActivity"
    android:exported="false"
    android:theme="@style/Theme.SmartCity" />

<activity
    android:name="tn.esprit.smartcity.Utilisateurs.view.DashboardActivity"
    android:exported="false"
    android:theme="@style/Theme.SmartCity" />
```

### 2. Required Dependencies (build.gradle)
```gradle
dependencies {
    // AndroidX Core
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // Material Design 3
    implementation 'com.google.android.material:material:1.11.0'
    
    // Lifecycle & ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Activity KTX
    implementation 'androidx.activity:activity-ktx:1.8.2'
}
```

### 3. Usage from Other Modules

**Starting Login:**
```kotlin
val intent = Intent(context, LoginActivity::class.java)
startActivity(intent)
```

**Accessing User Repository:**
```kotlin
val repository = UserRepository.getInstance()
val user = repository.login(email, password)
```

**Checking Authentication State:**
```kotlin
// The module uses in-memory storage
// For persistent authentication, integrate with SharedPreferences or DataStore
```

### 4. Customization Points

**Replace In-Memory Storage:**
- Modify `UserRepository.kt` to use Room, Retrofit, or Firebase
- Keep the same interface for minimal disruption

**Extend User Model:**
```kotlin
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    // Add your fields:
    val phoneNumber: String? = null,
    val address: String? = null
)
```

**Customize Navigation:**
- Replace `DashboardActivity` with your main application screen
- Update the intent in `LoginActivity.kt`

## Validation Rules

### Email
- Must not be empty
- Must match standard email pattern
- Must be unique (for signup)

### Password
- Must not be empty
- Minimum 6 characters (for signup)

### Full Name
- Must not be empty (for signup)

## Security Considerations

⚠️ **For School Project Use Only** ⚠️

This module stores passwords in plain text for educational purposes. For production:
1. Hash passwords (use BCrypt, Argon2, etc.)
2. Implement HTTPS for API calls
3. Add token-based authentication (JWT, OAuth)
4. Use secure storage (Android Keystore)
5. Implement rate limiting and account lockout
6. Add multi-factor authentication

## Testing

### Manual Testing Flow
1. Launch `LoginActivity`
2. Click "Sign Up" to create account
3. Enter full name, email, and password
4. Accept terms and click "Create Account"
5. Return to login screen
6. Enter same credentials
7. Click "Sign In"
8. Verify navigation to Dashboard
9. Click "Logout" to return to Login

### Test Credentials
Create test users through the signup flow. The repository persists data only during the app session.

## Future Enhancements

- [ ] Persistent storage (Room database)
- [ ] Password reset functionality
- [ ] Email verification
- [ ] Social authentication integration
- [ ] Biometric authentication
- [ ] Profile management
- [ ] Session management
- [ ] Remember me functionality
- [ ] Password strength indicator
- [ ] Form auto-fill support

## Team Integration

### Accessing User Data in Other Modules
```kotlin
// Get the singleton instance
val userRepository = UserRepository.getInstance()

// Check if user is logged in (implement your own session management)
val currentUser = getCurrentLoggedInUser() // You'll need to implement this

// Share user data across modules
intent.putExtra("USER_ID", user.id)
intent.putExtra("USER_NAME", user.fullName)
```

### Module Communication
This module is designed to be independent. To integrate:
1. Replace `DashboardActivity` with your main activity
2. Implement session management (SharedPreferences/DataStore)
3. Share user data through intents or a shared ViewModel

## Support

For issues or questions about this module, contact the Utilisateurs team.

## Version
**v1.0.0** - Initial Release
- Complete MVVM authentication flow
- Material Design 3 UI
- In-memory user management
- Input validation and error handling
