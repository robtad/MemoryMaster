package com.robtad.memorymaster

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.robtad.memorymaster.models.MemoryGame
import kotlin.properties.Delegates


class GameModeActivity : AppCompatActivity() {
    private lateinit var buttonSingle: Button
    private  lateinit var buttonMulti: Button
    private lateinit var buttonStart: Button
    private val TAG = "GameModeActivity"
    private lateinit var clRoot: ConstraintLayout

    var gameMode = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstpage)

        buttonSingle = findViewById(R.id.button11)
        buttonMulti = findViewById(R.id.button12)
        buttonStart = findViewById(R.id.button13)

        buttonSingle.setOnClickListener {
            gameMode = 1
            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info
            buttonSingle.isEnabled = false
            buttonSingle.setBackgroundColor(Color.GRAY)

            val handler = Handler()
            val runnable = Runnable {
                buttonSingle.isEnabled = true
                buttonSingle.setBackgroundColor(Color.BLUE) // Restore the original color
            }
            handler.postDelayed(runnable, 3000) // Re-enable the button after 5 seconds
            //startActivity(Intent(this , SinglePlayerActivity::class.java))
        }
        buttonMulti.setOnClickListener {
            gameMode = 2
            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info
            buttonMulti.isEnabled = false
            buttonMulti.setBackgroundColor(Color.GRAY)

            val handler = Handler()
            val runnable = Runnable {
                buttonMulti.isEnabled = true
                buttonMulti.setBackgroundColor(Color.BLUE) // Restore the original color
            }
            handler.postDelayed(runnable, 3000) // Re-enable the button after 5 seconds
            //startActivity(Intent(this , MultiPlayerActivity::class.java))
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
            handler.postDelayed(runnable, 3000) // Re-enable the button after 5 seconds
        }
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


}
