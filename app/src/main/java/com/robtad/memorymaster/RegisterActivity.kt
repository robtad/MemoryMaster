package com.robtad.memorymaster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.robtad.memorymaster.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("users")

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val name = binding.nameEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if (pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                val user = firebaseAuth.currentUser
                                val userDb = databaseReference?.child(user?.uid!!)

                                userDb?.child("id")?.setValue(user?.uid)
                                userDb?.child("name")?.setValue(name)
                                userDb?.child("email")?.setValue(email)
                                userDb?.child("password")?.setValue(pass)

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { nameUpdated ->
                                        if (nameUpdated.isSuccessful) {
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this, nameUpdated.exception!!.message.toString(), Toast.LENGTH_SHORT).show()                                        }
                                    }
                            }else{
                                Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "Password is NOT matching!!!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty Fields Are NOT Allowed!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, LoginMainActivity::class.java)
            startActivity(intent)
        }
    }
}