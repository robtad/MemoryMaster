package com.robtad.memorymaster

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
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
import com.robtad.memorymaster.models.SinglePlayerMemoryGame
import java.io.File
import java.util.concurrent.TimeUnit


class SinglePlayerActivity : AppCompatActivity()
{



    private val TAG = "SinglePlayerActivity"


    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView  //lateinit (late initialization) coz the variables will be set onCreate method later
    private lateinit var tvNumMoves: TextView
    private  lateinit var tvNumPairs: TextView

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit  var memoryGame: SinglePlayerMemoryGame

    private var boardSize: BoardSize = BoardSize.EASY
    //
    private var indexOfCurrentCard: Int? = null

    //for the countdown
    var remainingSecond: Float = 0f
    val gameTime: Long = 10000
    private val gameTimeSeconds: Float = gameTime.toFloat()/1000
    var countDownTimer: CountDownTimer? = null
    //var remainingSecond:Long = 0
    private lateinit var timerText: MenuItem

    //score
    private var score: Float = 0.0F

    //for background music
    var backgroundSoundTrack = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        //setting up the board
        setupBoard()

    }


    //for refresh button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        var timerText: MenuItem = menu!!.findItem(R.id.mi_count_down)
        //
        countDownTimer = object : CountDownTimer(gameTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft: String = "Time: " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))
                remainingSecond = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toFloat()
                timerText.title = secondsLeft
                //gameTime = millisUntilFinished
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
                    goToGameModeActivity()
                }, 5000)

                //setupBoard()
            }

        }.start()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //refreshing the board
            R.id.mi_refresh ->{
                //setup the game again
                //warn users if they are okay to loose their progress before refreshing the game, using alert dialog
                if(memoryGame.getNumMoves()>0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game?", null, View.OnClickListener { setupBoard() })
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
                startActivity(Intent(this , GameModeActivity::class.java))
                return true
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

    private fun setupBoard() {
        //start background music
        setBackgroundMusic("background_music")

        //starting the timer
        countDownTimer?.start()

        //resetting the text view at the bottom (moves and score) depending on the board size
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


        memoryGame = SinglePlayerMemoryGame(boardSize)

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

///
    fun displayScore(position: Int)  {

        if(indexOfCurrentCard == null){
            //0 or 2 cards previously flipped over
            indexOfCurrentCard = position

        }else{
            //exactly one card previously flipped over
            //foundMatch = checkForMatch(indexOfCurrentCard!!, position)
            score += scoreCalculator(indexOfCurrentCard!!, position)
            //scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)
            Log.i(TAG, "ScoreSPA =  ${score}") //Log.i --> i = info

            indexOfCurrentCard = null
        }

        //return score
    }



    private fun scoreCalculator(position1: Int, position2: Int): Float {

        //Find a way to check the houses of the cards that are not matched
        //var array = FloatArray(2) //the first element of array represents the if conditions below: first if 1, second else 2, last else 3
        var point: Float = 0.0F
        val house1 = memoryGame.cards[position1].identifier["house"]
        val house2 = memoryGame.cards[position2].identifier["house"]
        val housePoint1: Float = memoryGame.cards[position1].identifier["housePoint"].toString().toFloat()
        val housePoint2: Float = memoryGame.cards[position2].identifier["housePoint"].toString().toFloat()
        val cardPoint1: Float = memoryGame.cards[position1].identifier["cardPoint"].toString().toFloat()
        val cardPoint2: Float = memoryGame.cards[position2].identifier["cardPoint"].toString().toFloat()


        if(memoryGame.cards[position1].identifier != memoryGame.cards[position2].identifier){
            if (house1 == house2){ //if cards do not match but from the same house
                point -= ((cardPoint1 + cardPoint2)/ housePoint1 ) * ((gameTimeSeconds-remainingSecond)/10)

                return point
                //secondsLeft
                //Log.i(TAG, "RemainingSecond =  ${spActivity.getRemainingSeconds()}") //Log.i --> i = info

            }else{ //if cards do not match and not from the same house
                point -= (((cardPoint1 + cardPoint2)/2) * housePoint1 * housePoint2 ) *  ((gameTimeSeconds-remainingSecond)/10)

                return point
                //Log.i(TAG, "RemainingSeconds =  ${spActivity.getRemainingSeconds()}") //Log.i --> i = info

            }
            //return score
        }else{ //if cards match. (i.e same card from the same house)
            point += (2*cardPoint1*housePoint1)*(remainingSecond/10)
            if(!memoryGame.haveWonGame()){
                setBackgroundMusic("cards_matched")
                Handler().postDelayed({
                    //wait till the above track finishes
                    setBackgroundMusic("background_music")
                }, 3800)
            }

            return point
            //Log.i(TAG, "RemainingSeconds =  ${spActivity.getRemainingSeconds()}") //Log.i --> i = info

        }

        //return array
    }
    //////// GAME SCORE LOGIC UP HERE


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun updateGameWithFlip(position: Int) {
        //Error handling
        // user should not be able to flip the already matched cards and should not be able to flip over a card twice
        //check for win

        if(memoryGame.haveWonGame()){
            //Alert user
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
                Snackbar.make(clRoot, "You won! Congratulations!", Snackbar.LENGTH_LONG).show()
                setBackgroundMusic("game_won")

            }
        }
        //the following part executes after passing the above error checks so they are the right moves
        //displaying game score on every move
        memoryGame.displayScore(position)//this calculates game score after each move
        displayScore(position)//this calculates game score after each move
        //Log.i(TAG, "SCORE =  ${memoryGame.score} ")
        score = String.format("%.2f", score).toFloat()

        tvNumPairs.text = "Score: $score"
        //tvNumPairs.text = "Score: ${memoryGame.score}"

        Log.i(TAG, "Seconds Left =  $remainingSecond sec ")
        Log.i(TAG, "Seconds Elapsed =  ${(gameTimeSeconds-remainingSecond)/10} sec ")
        //showing number of moves
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }


}