package com.devkt.mynotes

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val welcomeText = findViewById<TextView>(R.id.animated_text)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val text1 = findViewById<TextView>(R.id.text1)
        val registerText = findViewById<TextView>(R.id.register_text)
        val owner = findViewById<TextView>(R.id.owner)

        val registerString = intent.getStringExtra("register").toString()
        Log.d("registerString", "$registerString")
        if (registerString == "register") {
            showFields(
                listOf(emailInput, passwordInput, loginButton, text1, registerText, owner)
            )
        } else {
            val fullText = welcomeText.text.toString()
            val ownerText = owner.text.toString()
            startTypingAnimation(welcomeText, fullText) {
                showFieldsSequentially(
                    listOf(emailInput, passwordInput, loginButton, text1, registerText, owner),
                    250
                )
                Handler().postDelayed({
                    startTypingAnimation(owner, ownerText) {}
                }, 1200)
            }
        }
        loginButton.setOnClickListener {
            when {
                emailInput.text.isEmpty() && passwordInput.text.isEmpty() -> {
                    triggerShakeAnimation(emailInput)
                    triggerShakeAnimation(passwordInput)
                }

                emailInput.text.isEmpty() -> triggerShakeAnimation(emailInput)
                passwordInput.text.isEmpty() -> triggerShakeAnimation(passwordInput)
                else -> {
                    val loginEmail = emailInput.text.toString()
                    val loginPassword = passwordInput.text.toString()
                    auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                triggerShakeAnimation(emailInput)
                                triggerShakeAnimation(passwordInput)
                                Toast.makeText(
                                    this,
                                    "Please Enter Correct Details.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun startTypingAnimation(textView: TextView, fullText: String, onComplete: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        var currentIndex = 0
        val typingInterval: Long = 80

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

    private fun showFields(views: List<View>) {
        var currentIndex = 0
        for (currentView in views) {
            if (currentIndex < views.size) {
                views[currentIndex].visibility = View.VISIBLE
                currentIndex++
            }
        }
    }

    private fun triggerShakeAnimation(view: View) {
        val pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 25f, -25f, 25f, 0f)
        val shakeAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX)
        shakeAnimator.duration = 500
        shakeAnimator.start()
    }
}