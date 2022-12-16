package com.robtad.memorymaster

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import java.util.concurrent.TimeUnit

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
    var gameTime: Long = 10000;


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        setupBoard()

    }


    //for refresh button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        //////
        var timerText: MenuItem = menu!!.findItem(R.id.mi_count_down)
        //
        var countDownTimer = object : CountDownTimer(gameTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft: String = "Time: " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))

                timerText.title = secondsLeft
                gameTime = millisUntilFinished

            }

            override fun onFinish() {
                timerText.title = "TimeOver"
                Snackbar.make(clRoot, "Game over!", Snackbar.LENGTH_LONG).show()
                //startActivity(Intent(this , GameModeActivity::class.java))

                goToGameModePage()

                //setupBoard()
            }

        }.start()


        return true
    }

    private fun goToGameModePage() {

        startActivity(Intent(this , GameModeActivity::class.java))

    }

    /*
    long timer = 10000;
    public Boolean onCreateOptionsMenu(Menu menu)
    {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu, menu);
    
           final MenuItem  counter = menu.findItem(R.id.counter);
            new CountDownTimer(timer, 1000) {
    
              public void onTick(long millisUntilFinished) {
                 long millis = millisUntilFinished;
                 String  hms = (TimeUnit.MILLISECONDS.toHours(millis))+":"+(TimeUnit.MILLISECONDS.toMinutes(millis) -TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))+":"+ (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    
                 counter.setTitle(hms);
                 timer = millis;
    
                }
    
            public void onFinish() {
                 counter.setTitle("done!");
                }
             }.start();
    
             return  true;
    
     }
     */
/*
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var timer: MenuItem = menu!!.findItem(R.id.mi_count_down)
        timer.setTitle("00:00")
        return true
    }

 */
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
            }.show()
    }


    private fun setupBoard() {

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


        memoryGame = MultiPlayerMemoryGame(boardSize)

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
        Log.i(TAG, "MOVE SCORE =  ${memoryGame.score1}, ${memoryGame.score2} ")
        tvNumMoves.text = "Score1: ${memoryGame.score1}"
        tvNumPairs.text = "Score2: ${memoryGame.score2}"


        //showing number of moves
        //tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }


}