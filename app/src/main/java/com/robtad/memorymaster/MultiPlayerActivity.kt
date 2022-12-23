package com.robtad.memorymaster

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robtad.memorymaster.models.BoardSize
import com.robtad.memorymaster.models.MultiPlayerMemoryGame
import kotlinx.coroutines.NonCancellable.cancel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class MultiPlayerActivity : AppCompatActivity()
{
    companion object{
        private const val TAG = "MultiPlayerActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView  //lateinit (late initialization) coz the variables will be set onCreate method later
    private lateinit var tvNumMoves: TextView
    private  lateinit var tvNumPairs: TextView

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit  var memoryGame: MultiPlayerMemoryGame

    private var boardSize: BoardSize = BoardSize.EASY
    //score related
    private var indexOfCurrentCard: Int? = null
    var playerFlag = 0
    var score1: Float = 0.0F //player1 score
    var score2: Float = 0.0F //player2 score
    //for the countdown
    var gameTime: Long = 60000;
    var countDownTimer: CountDownTimer? = null
    var remainingSecond:Long = 0
    private lateinit var timerText: MenuItem
    //for background music
    var backgroundSoundTrack = MediaPlayer()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        //timerText = findViewById(R.id.mi_count_down)
        setupBoard()

    }


    //for refresh button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        //////
        var timerText: MenuItem = menu!!.findItem(R.id.mi_count_down)
        //
        countDownTimer = object : CountDownTimer(gameTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft: String = "Time: " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))
                remainingSecond = millisUntilFinished
                timerText.title = secondsLeft
                gameTime = millisUntilFinished
                if(memoryGame.haveWonGame()){
                    countDownTimer?.cancel()
                    timerText.title = secondsLeft
                }

            }

            override fun onFinish() {
                timerText.title = "TimeOver"
                Snackbar.make(clRoot, "Game over!", Snackbar.LENGTH_LONG).show()

                setBackgroundMusic("time_over")
                Handler().postDelayed({
                    //setupBoard()
                    stopBackgroundMusic()
                    goToGameModeActivity()
                }, 3800)

                //setupBoard()
            }

        }.start()


        return true
    }


    private fun pauseCountDown() {
        countDownTimer?.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //refreshing the board
            R.id.mi_refresh ->{
                //setup the game again
                //warn users if they are okay to loose their progress before refreshing the game, using alert dialog
                if(memoryGame.getNumMoves()>0 && !memoryGame.haveWonGame()){
                    showAlertDialog("You guys quitting your game?", null, View.OnClickListener { setupBoard() })
                }else{
                    setupBoard()
                }
                return true
            }
            //choosing level of difficulty: the larger the board size the more difficult the game is
            R.id.mi_new_size ->{
                showNewSizeDialog()
                return true
            }
            //Going back to Game mode selection page
            R.id.mi_back_arrow -> {
                //stopBackgroundMusic()
                countDownTimer?.cancel()
                stopBackgroundMusic()
                startActivity(Intent(this , GameModeActivity::class.java))
                return true
            }
            R.id.mi_pauseStart->{
                stopStartBackgroundMusic()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        //inflating (showing the dialog_board_size layout)
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        //selecting one of the three radio group buttons (buttons holding the size of the board)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        //making the current board size be selected automatically
        when (boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        //making the current board size be selected automatically

        showAlertDialog("Choose new Size", boardSizeView, View.OnClickListener {
            //set a new value for the board size
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD  // if R.id.rbHard -> BoardSize.Hard
            }
            setupBoard() //starting the game after specifying the difficulty level (board size)
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok"){_,_ ->
                positiveClickListener.onClick(null)
                countDownTimer?.start()
            }.show()
    }
    private fun goToGameModeActivity(){
        startActivity(Intent(this , GameModeActivity::class.java))
    }
    private fun getRawUriString(filename: String): String {
        return "android.resource://$packageName/raw/$filename"
        //return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator.toString() + packageName.toString() + "/raw/" + filename)
    }
    private fun setBackgroundMusic(musicName:String){
        if(backgroundSoundTrack.isPlaying){
            backgroundSoundTrack.stop()
            backgroundSoundTrack.reset()
        }
        val uriPath = getRawUriString(musicName)
        val uri = Uri.parse(uriPath)
        backgroundSoundTrack.setDataSource(this, uri)
        backgroundSoundTrack.prepare()
        backgroundSoundTrack.start()
    }

    private fun stopBackgroundMusic(){
        if(backgroundSoundTrack.isPlaying){
            backgroundSoundTrack.stop()
            backgroundSoundTrack.reset()
        }
    }

    private fun stopStartBackgroundMusic(){
        if(backgroundSoundTrack.isPlaying){
            backgroundSoundTrack.stop()
            backgroundSoundTrack.reset()
        }else{
            setBackgroundMusic("background_music")
        }
    }
    private fun setupBoard() {
        //resetting the text view at the bottom (moves and score) depending on the board size

        //start background music
        setBackgroundMusic("background_music")

        //starting the timer
        countDownTimer?.start()


        when (boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "EASY"
                tvNumPairs.text = "Score: 0"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "MEDIUM"
                tvNumPairs.text = "Score: 0"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "HARD"
                tvNumPairs.text = "Score: 0"
            }
        }

        //resetting the text view at the bottom (moves and score) depending on the board size


        memoryGame = MultiPlayerMemoryGame(boardSize, this)

        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                //Log.i(TAG,"Card clicked $position")
                updateGameWithFlip(position)
            }

        }) // 8 number of the memory cards: will change later
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true) //not must, but important for efficiency: makes the size of the recycler view determined on the app boot up
        rvBoard.layoutManager = GridLayoutManager(this,boardSize.getWidth())

    }

    ///////// GAME SCORE LOGIC HERE
    fun displayScore(position: Int)  {


        if(indexOfCurrentCard == null){
            //0 or 2 cards previously flipped over
            indexOfCurrentCard = position

        }else{
            //exactly one card previously flipped over
            //foundMatch = checkForMatch(indexOfCurrentCard!!, position)
            playerFlag++
            if (playerFlag % 2 != 0){// ---> turn of the first player
                score1 += scoreCalculator(indexOfCurrentCard!!, position)
            }
            else{//playerFlag % 2 == 0 ---> turn of the second player
                score2 += scoreCalculator(indexOfCurrentCard!!, position)
            }
            //score += scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)

            indexOfCurrentCard = null
        }

        //return score
    }

    private fun scoreCalculator(position1: Int, position2: Int): Float {

        //Find a way to check the houses of the cards that are not matched
        var point: Float = 0.0F
        val house1 = memoryGame.cards[position1].identifier["house"]
        val house2 = memoryGame.cards[position2].identifier["house"]
        val housePoint1: Float = memoryGame.cards[position1].identifier["housePoint"].toString().toFloat()
        val housePoint2: Float = memoryGame.cards[position2].identifier["housePoint"].toString().toFloat()
        val cardPoint1: Float = memoryGame.cards[position1].identifier["cardPoint"].toString().toFloat()
        val cardPoint2: Float = memoryGame.cards[position2].identifier["cardPoint"].toString().toFloat()
        val cardName: String = memoryGame.cards[position1].identifier["card"].toString()
        val cardName2: String = memoryGame.cards[position2].identifier["card"].toString()

        //val cardName = memoryGame.cards[position1].identifier["card"]




        if(memoryGame.cards[position1].identifier != memoryGame.cards[position2].identifier){
            if (house1 == house2){ //if cards do not match but from the house
                point -= ((cardPoint1 + cardPoint2)/ housePoint1 )
            }else{ //if cards do not match and not from the same house
                point -= (((cardPoint1 + cardPoint2)/2) * housePoint1 * housePoint2 )
            }
            //return score
        }else{ //if cards match. (i.e same card from the same house)
            point += (2*cardPoint1*housePoint1)
            //alert with background music
            //If Cedric Diggory is matched play "sth in the way"

            if(cardName == "CD" || cardName2 == "CD"){
                setBackgroundMusic("sth_in_the_way")
                Handler().postDelayed({
                    //wait till the above track finishes
                    setBackgroundMusic("background_music")
                    //stopBackgroundMusic()
                }, 8000)
            }


            else if(!memoryGame.haveWonGame()){
                setBackgroundMusic("cards_matched")
                Handler().postDelayed({
                    //wait till the above track finishes
                    setBackgroundMusic("background_music")
                }, 3800)
            }

        }

        return point
    }
    //////// GAME SCORE LOGIC UP HERE



    private fun updateGameWithFlip(position: Int) {
        //Error handling
        // user should not be able to flip the already matched cards and should not be able to flip over a card twice
        //check for win

        if(memoryGame.haveWonGame()){
            //Alert user
            pauseCountDown()
            //timerText.title = remainingSecond.toString()
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            //Alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        //Flipping over the card on the valid move

        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Found a Match! Num pairs found: ${memoryGame.numPairsFound} ")
            //tvNumPairs.text = "Score: ${memoryGame.score}"
            if(memoryGame.haveWonGame()){
                setBackgroundMusic("game_won")
                Handler().postDelayed({
                    stopBackgroundMusic()
                }, 6000)
                if(memoryGame.score1 > memoryGame.score2){
                    Snackbar.make(clRoot, "Player1 Won! Congratulations!", Snackbar.LENGTH_LONG).show()


                }else if (memoryGame.score2 > memoryGame.score1){
                    Snackbar.make(clRoot, "Player2 Won! Congratulations!", Snackbar.LENGTH_LONG).show()

                }else {
                    Snackbar.make(clRoot, "It's a draw!", Snackbar.LENGTH_LONG).show()
                }
            }

        }
        //the following part executes after passing the above error checks so they are the right moves
        //displaying game score on every move
        memoryGame.displayScore(position)//this calculates game score after each move
        displayScore(position)//this calculates game score after each move

        Log.i(TAG, "MOVE SCORE =  ${memoryGame.score1}, ${memoryGame.score2} ")
        tvNumMoves.text = "Score1: $score1"
        tvNumPairs.text = "Score2: $score2"


        //showing number of moves
        //tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }


}