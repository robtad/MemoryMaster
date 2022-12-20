package com.robtad.memorymaster

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.robtad.memorymaster.models.BoardSize
import com.robtad.memorymaster.models.SinglePlayerMemoryGame
//import com.robtad.memorymaster.utils.NonActivityClass
import kotlinx.coroutines.*
import kotlinx.coroutines.Deferred
import java.io.Serializable
//import com.robtad.memorymaster.models.MemoryGame
import kotlin.properties.Delegates


class GameModeActivity() : AppCompatActivity() {
    private lateinit var buttonSingle: Button
    private  lateinit var buttonMulti: Button
    private lateinit var buttonStart: Button
    private lateinit var buttonLogout: Button
    private lateinit var img: ImageView
    private val TAG = "GameModeActivity"
    private lateinit var clRoot: ConstraintLayout
    private var boardSize: BoardSize = BoardSize.EASY

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    /*
    var DEFAULT_ICONS_GRYFFINDOR = mutableListOf<HashMap<String, Any>>()
    var DEFAULT_ICONS_HUFFLEPUFF = mutableListOf<HashMap<String, Any>>()
    var DEFAULT_ICONS_RAVENCLAW = mutableListOf<HashMap<String, Any>>()
    var DEFAULT_ICONS_SLYTHERIN = mutableListOf<HashMap<String, Any>>()

     */
    //
    object ListsHolder {
        var DEFAULT_ICONS_GRYFFINDOR = mutableListOf<HashMap<String, Any>>()
        var DEFAULT_ICONS_HUFFLEPUFF = mutableListOf<HashMap<String, Any>>()
        var DEFAULT_ICONS_RAVENCLAW = mutableListOf<HashMap<String, Any>>()
        var DEFAULT_ICONS_SLYTHERIN = mutableListOf<HashMap<String, Any>>()
    }


    //


    var deferred = CompletableDeferred<String>()
    var gameMode = 0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstpage)

        buttonSingle = findViewById(R.id.button11)
        buttonMulti = findViewById(R.id.button12)
        buttonStart = findViewById(R.id.button13)
        buttonLogout = findViewById(R.id.buttonLogout)
        firebaseAuth = FirebaseAuth.getInstance()

        buttonStart.isEnabled = false

        buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this, "Sign Out Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }

        buttonSingle.setOnClickListener {
            gameMode = 1
            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info
            if(!buttonMulti.isEnabled){
                buttonMulti.isEnabled = true
            }
            if(!buttonStart.isEnabled){
                buttonStart.isEnabled = true
            }
            buttonSingle.isEnabled = false
            buttonSingle.setBackgroundColor(Color.GRAY)

        }
        buttonMulti.setOnClickListener {
            gameMode = 2
            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info
            if(!buttonSingle.isEnabled){
                buttonSingle.isEnabled = true
            }
            if(!buttonStart.isEnabled){
                buttonStart.isEnabled = true
            }
            buttonMulti.isEnabled = false
            buttonMulti.setBackgroundColor(Color.GRAY)

        }
        buttonStart.setOnClickListener{
            if(gameMode == 1){
                startActivity(Intent(this , SinglePlayerActivity::class.java))
            }else if(gameMode == 2){
                startActivity(Intent(this , MultiPlayerActivity::class.java))
            }
            else{
                showSnackbar(it)
                //Snackbar.make(clRoot, "Please select the game mode", Snackbar.LENGTH_LONG).show()
            }
            buttonStart.isEnabled = false
            buttonStart.setBackgroundColor(Color.GRAY)

            val handler = Handler()
            val runnable = Runnable {
                buttonStart.isEnabled = true
                buttonStart.setBackgroundColor(Color.BLUE) // Restore the original color
            }
            handler.postDelayed(runnable, 2000) // Re-enable the button after 5 seconds

        }

        // DB
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                fetchData()
            }
            buttonStart.isEnabled = true

            Log.d(TAG, "Result: $result")
            img = findViewById(R.id.imageView)
            img.setImageBitmap(ListsHolder.DEFAULT_ICONS_GRYFFINDOR[5]["bitmap"] as Bitmap?)
        }
        // END DB

//    Log.i(TAG, "From Gryffindorlist =  ${DEFAULT_ICONS_GRYFFINDOR[0]}") //Log.i --> i = info


    }
/*
    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }

 */
fun showSnackbar(view: View) {

    val snackbar = Snackbar.make(view, "Please select the game mode", Snackbar.LENGTH_LONG)

    snackbar.show()
}
    private fun decoderBase64(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private suspend fun fetchData(): String {

        println("pullData")
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("cards")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                //val value = dataSnapshot.children
                for (cardSnapshot in dataSnapshot.child("gryffindor").children) {
                    // Get the value of the child as a hash map
                    val card = cardSnapshot.value as HashMap<String, Any>
                    Log.d("TAG", "Value is: ${card.getValue("card")}")

                    // Add the hash map to the list
                    val image = card["image"].toString()
                    card.put("bitmap", decoderBase64(image))
                    //                    card["bitmap"] = decoderBase64(card["image"].toString())
                    ListsHolder.DEFAULT_ICONS_GRYFFINDOR.add(card)
                    //Log.i(TAG, "From Gryffindorlist qwerty =  ${ListsHolder.DEFAULT_ICONS_GRYFFINDOR[0]}") //Log.i --> i = info

                }
                for (cardSnapshot in dataSnapshot.child("hufflepuff").children) {
                    // Get the value of the child as a hash map
                    val card = cardSnapshot.value as HashMap<String, Any>
                    // Add the hash map to the list
                    val image = card["image"].toString()
                    card.put("bitmap", decoderBase64(image))
                    //                    card["bitmap"] = decoderBase64(card["image"].toString())
                    ListsHolder.DEFAULT_ICONS_HUFFLEPUFF.add(card)
//                    Log.d("TAG", "Value is: ${card.getValue("card")}")
                }
                for (cardSnapshot in dataSnapshot.child("ravenclaw").children) {
                    // Get the value of the child as a hash map
                    val card = cardSnapshot.value as HashMap<String, Any>
                    val image = card["image"].toString()
                    card.put("bitmap", decoderBase64(image))
                    // Add the hash map to the list
//                    card["bitmap"] = decoderBase64(card["image"].toString())
                    ListsHolder.DEFAULT_ICONS_RAVENCLAW.add(card)

                //                    Log.d("TAG", "Value is: ${card.getValue("card")}")
                }
                for (cardSnapshot in dataSnapshot.child("slytherin").children) {
                    // Get the value of the child as a hash map
                    val card = cardSnapshot.value as HashMap<String, Any>
                    // Add the hash map to the list
                    val image = card["image"].toString()
                    card.put("bitmap", decoderBase64(image))
                    ListsHolder.DEFAULT_ICONS_SLYTHERIN.add(card)
//                    Log.d("TAG", "Value is: ${card["housePoint"]}")
                }
                deferred.complete("Data fetched")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
        return deferred.await()
    }



}
