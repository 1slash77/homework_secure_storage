package com.otus.securehomework.presentation.splash

import android.annotation.SuppressLint
import androidx.biometric.BiometricManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.otus.securehomework.R
import com.otus.securehomework.data.source.local.UserPreferences
import com.otus.securehomework.presentation.auth.AuthActivity
import com.otus.securehomework.presentation.home.HomeActivity
import com.otus.securehomework.presentation.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        userPreferences.accessToken.asLiveData().observe(this) { token ->
            if (token == null) {
                startNewActivity(AuthActivity::class.java)
            } else {
                handleAccessToken()
            }
        }
    }

    private fun handleAccessToken() {
        val canAuthCode = BiometricManager.from(this)
            .canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
        if (canAuthCode == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricPrompt(
                this,
                ContextCompat.getMainExecutor(this),
                BiometricAuthCallback()
            ).authenticate(BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication")
                .setSubtitle("Confirm your identity")
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .build())
        } else {
            startNewActivity(AuthActivity::class.java)
            showToast("Failed to use Biometric, err: $canAuthCode")
        }
    }

    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    inner class BiometricAuthCallback: BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            showToast("Authentication successful")
            startNewActivity(HomeActivity::class.java)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            showToast("Authentication error: $errString")
            startNewActivity(AuthActivity::class.java)
        }

        override fun onAuthenticationFailed() {
            showToast("Authentication failed")
            startNewActivity(AuthActivity::class.java)
        }
    }
}