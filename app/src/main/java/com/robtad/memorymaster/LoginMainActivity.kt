package com.robtad.memorymaster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.robtad.memorymaster.databinding.ActivityMainLoginBinding

class LoginMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            firebaseAuth.currentUser?.let {
                binding.textView4.text = it.displayName
            }
        }

        binding.button2.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this, "Sign Out Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
    }
}