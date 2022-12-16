package com.robtad.memorymaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.robtad.memorymaster.models.MemoryGame
import kotlin.properties.Delegates


class GameModeActivity : AppCompatActivity() {
    private lateinit var buttonSingle: Button
    private  lateinit var buttonMulti: Button
    private val TAG = "GameModeActivity"

    var gameMode = 0
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstpage)

        buttonSingle = findViewById(R.id.button11)
        buttonMulti = findViewById(R.id.button12)

        buttonSingle.setOnClickListener {
            gameMode = 1
            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info

            startActivity(Intent(this , SinglePlayerActivity::class.java))
        }
        buttonMulti.setOnClickListener {
            gameMode = 2

            Log.i(TAG, "GAME MODE =  $gameMode") //Log.i --> i = info
            startActivity(Intent(this , MultiPlayerActivity::class.java))
        }
    }
/*
    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }

 */

    fun getGameTag(): Int { //getNumPairs returns total number of pairs of cards
        return gameMode
    }
}
