# Quick Integration Guide

## ✅ Completed Setup

The Utilisateurs (User Authentication) module has been fully set up with:

### 📁 Project Structure Created
```
Utilisateurs/
├── model/
│   └── User.kt
├── repository/
│   └── UserRepository.kt
├── viewmodel/
│   └── AuthViewModel.kt
└── view/
    ├── LoginActivity.kt
    ├── SignupActivity.kt
    └── DashboardActivity.kt
```

### 🎨 UI Resources Added
- ✅ `activity_login.xml` - Login screen layout
- ✅ `activity_signup.xml` - Signup screen layout
- ✅ `activity_dashboard.xml` - Dashboard screen layout
- ✅ `colors.xml` - Updated with Smart City theme colors
- ✅ `strings.xml` - All text externalized
- ✅ `bg_logo.xml` - Gradient background drawable

### ⚙️ Configuration Updated
- ✅ **AndroidManifest.xml** - All activities registered
- ✅ **build.gradle.kts** - Dependencies added (ViewModel, LiveData, Activity KTX)
- ✅ **libs.versions.toml** - Lifecycle versions configured

## 🚀 Quick Start

### Option 1: Launch from MainActivity

Add this to your `MainActivity.kt`:

```kotlin
import android.content.Intent
import tn.esprit.smartcity.Utilisateurs.view.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Navigate to login screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
```

### Option 2: Set as Launcher Activity

Modify `AndroidManifest.xml` to make LoginActivity the entry point:

```xml
<!-- Move the intent-filter from MainActivity to LoginActivity -->
<activity
    android:name=".Utilisateurs.view.LoginActivity"
    android:exported="true"
    android:theme="@style/Theme.SmartCity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".MainActivity"
    android:exported="false" />
```

## 🔧 Next Steps

### 1. Sync Project
Run **File → Sync Project with Gradle Files** in Android Studio to download the new dependencies.

### 2. Test the Module
- Run the app
- Click "Sign Up" to create a test account
- Enter: Name, Email, Password
- Accept terms and create account
- Login with the same credentials
- Verify navigation to Dashboard

### 3. Integrate with Your Team's Modules

Replace `DashboardActivity` navigation in `LoginActivity.kt`:

```kotlin
// Current (line ~46 in LoginActivity.kt):
val intent = Intent(this, DashboardActivity::class.java)

// Change to your main screen:
val intent = Intent(this, YourMainActivity::class.java)
```

### 4. Add Session Management (Optional)

To persist login state across app restarts, add SharedPreferences:

```kotlin
// In LoginActivity after successful login
val sharedPrefs = getSharedPreferences("SmartCityPrefs", Context.MODE_PRIVATE)
sharedPrefs.edit().apply {
    putString("userId", result.user.id)
    putString("userName", result.user.fullName)
    putString("userEmail", result.user.email)
    putBoolean("isLoggedIn", true)
    apply()
}

// Check in MainActivity
val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
if (isLoggedIn) {
    // Go to main screen
} else {
    // Go to login
}
```

## 🎨 Customization

### Change Colors
Edit `res/values/colors.xml`:
- `primary_blue` - Main brand color
- `primary_cyan` - Secondary accent
- `background_gradient` - Screen background

### Change Validation Rules
Edit `AuthViewModel.kt`:
- Email validation (line ~33)
- Password minimum length (line ~59)

### Add More User Fields
Extend `model/User.kt`:
```kotlin
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null,  // Add your fields
    val address: String? = null
)
```

## 📱 Testing on Device/Emulator

### Test Account Flow
1. Sign Up: `test@smartcity.com` / `password123`
2. Login with same credentials
3. Verify dashboard appears
4. Click logout
5. Try logging in again

### Common Issues

**Build Error: "Unresolved reference: viewModels"**
- Solution: Sync Gradle files (File → Sync Project)

**Layout Preview Error**
- Solution: Rebuild project (Build → Rebuild Project)

**Activities not found**
- Solution: Check AndroidManifest.xml is updated and sync Gradle

## 📚 Documentation

For detailed documentation, see:
- **README.md** - Complete module documentation
- Inline code comments in all Kotlin files

## 🤝 Team Integration Example

```kotlin
// From another module, navigate to login
val intent = Intent(context, LoginActivity::class.java)
startActivity(intent)

// Access repository from another module
val userRepo = UserRepository.getInstance()
val allUsers = userRepo.getAllUsers()

// Pass user data to other modules
intent.putExtra("USER_ID", user.id)
intent.putExtra("USER_NAME", user.fullName)
```

## ⚠️ Important Notes

1. **In-Memory Storage**: Users are stored in memory only. They're lost when app closes.
2. **Plain Text Passwords**: For educational purposes only. Hash passwords in production.
3. **No Backend**: This is a standalone module. Integrate with your backend later.
4. **Social Login**: Google/Facebook/GitHub buttons are placeholders. Implement if needed.

## 🎓 For School Project

This module is **production-ready for school projects** and includes:
- ✅ Complete MVVM architecture
- ✅ Input validation
- ✅ Material Design 3 UI
- ✅ Error handling
- ✅ Clean code structure
- ✅ No hard-coded strings
- ✅ Proper separation of concerns
- ✅ Easy to integrate with team modules

## 📞 Support

If you encounter issues:
1. Check this guide
2. Read README.md
3. Review inline code comments
4. Sync Gradle and rebuild

---

**Version**: 1.0.0  
**Created**: October 2025  
**Module**: Utilisateurs (User Authentication)
