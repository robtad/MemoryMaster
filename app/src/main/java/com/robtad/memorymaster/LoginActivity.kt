package com.robtad.memorymaster

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.firebase.auth.FirebaseAuth
import com.robtad.memorymaster.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Reset Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val email = view.findViewById<EditText>(R.id.emailEt)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                resetMyPass(email)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            //val intent = Intent(this, LoginMainActivity::class.java)
                            val intent = Intent(this, GameModeActivity::class.java)

                            startActivity(intent)
                        }else{
                            Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
            }else{
                Toast.makeText(this, "Empty Fields Are NOT Allowed!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetMyPass(email : EditText) {
        if (email.text.toString().isEmpty()) {
            Toast.makeText(this, "You haven't written your Email", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            Toast.makeText(this, "It seems like not valid email address :)", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}