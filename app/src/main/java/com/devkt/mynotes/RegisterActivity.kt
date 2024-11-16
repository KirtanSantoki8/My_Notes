package com.devkt.mynotes

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val owner = findViewById<TextView>(R.id.owner)
        val startText = findViewById<TextView>(R.id.textView)
        val nameInput = findViewById<TextView>(R.id.nameInput)
        val emailInput = findViewById<TextView>(R.id.emailInput)
        val passwordInput = findViewById<TextView>(R.id.passwordInput)
        val registerButton = findViewById<TextView>(R.id.registerButton)
        val text1 = findViewById<TextView>(R.id.text1)
        val loginText = findViewById<TextView>(R.id.login_text)

        val fullText = startText.text.toString()
        val ownerText = owner.text.toString()
        startTypingAnimation(startText, fullText) {
            showFieldsSequentially(
                listOf(nameInput, emailInput, passwordInput, registerButton, text1, loginText, owner),
                250
            )
            Handler().postDelayed({
                startTypingAnimation(owner, ownerText){}
            }, 1450)
        }

        registerButton.setOnClickListener {
            when {
                nameInput.text.isEmpty() && emailInput.text.isEmpty() && passwordInput.text.isEmpty() -> {
                    triggerShakeAnimation(nameInput)
                    triggerShakeAnimation(emailInput)
                    triggerShakeAnimation(passwordInput)
                }
                nameInput.text.isEmpty() && emailInput.text.isEmpty() -> {
                    triggerShakeAnimation(nameInput)
                    triggerShakeAnimation(emailInput)
                }
                nameInput.text.isEmpty() && passwordInput.text.isEmpty() -> {
                    triggerShakeAnimation(nameInput)
                    triggerShakeAnimation(passwordInput)
                }
                emailInput.text.isEmpty() && passwordInput.text.isEmpty() -> {
                    triggerShakeAnimation(emailInput)
                    triggerShakeAnimation(passwordInput)
                }
                nameInput.text.isEmpty() -> triggerShakeAnimation(nameInput)
                emailInput.text.isEmpty() -> triggerShakeAnimation(emailInput)
                passwordInput.text.isEmpty() -> triggerShakeAnimation(passwordInput)
                else -> {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.putExtra("register", "register")
                    startActivity(intent)
                    finish()
                }
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.putExtra("register", "register")
            startActivity(intent)
            finish()
        }
    }

    private fun startTypingAnimation(textView: TextView, fullText: String, onComplete: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        var currentIndex = 0
        val typingInterval: Long = 100

        val typingRunnable = object : Runnable {
            override fun run() {
                if (currentIndex < fullText.length) {
                    textView.text = fullText.substring(0, currentIndex + 1)
                    currentIndex++
                    handler.postDelayed(this, typingInterval)
                } else {
                    onComplete()
                }
            }
        }
        handler.post(typingRunnable)
    }

    private fun showFieldsSequentially(views: List<View>, delay: Long) {
        val handler = Handler(Looper.getMainLooper())
        var currentIndex = 0
        val showRunnable = object : Runnable {
            override fun run() {
                if (currentIndex < views.size) {
                    views[currentIndex].visibility = View.VISIBLE
                    currentIndex++
                    handler.postDelayed(this, delay)
                }
            }
        }

        handler.post(showRunnable)
    }

    // Shake Animation for Invalid Inputs
    private fun triggerShakeAnimation(view: View) {
        val pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 25f, -25f, 25f, 0f)
        val shakeAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX)
        shakeAnimator.duration = 500
        shakeAnimator.start()
    }
}