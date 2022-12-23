package com.robtad.memorymaster.models

//import com.robtad.memorymaster.utils.DEFAULT_ICONS_GRYFFINDOR
//import com.robtad.memorymaster.utils.DEFAULT_ICONS_HUFFLEPUFF
//import com.robtad.memorymaster.utils.DEFAULT_ICONS_RAVENCLAW
//import com.robtad.memorymaster.utils.DEFAULT_ICONS_SLYTHERIN
import android.content.Context
import android.util.Log
import com.robtad.memorymaster.GameModeActivity
import com.robtad.memorymaster.utils.list1
import com.robtad.memorymaster.utils.list2
import com.robtad.memorymaster.utils.list3
import com.robtad.memorymaster.utils.list4
import java.io.BufferedWriter
import java.io.OutputStreamWriter


class MultiPlayerMemoryGame(private val boardSize: BoardSize, private val context: Context){
    private val TAG = "MultiPlayerMemoryGame"
    private lateinit var gameMode: GameModeActivity
    //var gameMode: Intent = Intent(this@MultiPlayerMemoryGame, GameModeActivity::class.java)
    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null
    private var indexOfCurrentCard: Int? = null
    var playerFlag = 0
    var score1: Float = 0.0F //player1 score
    var score2: Float = 0.0F //player2 score
    private var gameTypeTag = 0 //gameTypeTage is an integer variable to denote if the game is single or multi player
    private var randomizedImages: List<java.util.HashMap<String, out Any>>

    init {
        //How pictures will be selected to be displayed on the board
        val chosenImagesGryffindor: List<HashMap<String, out Any>> = list1.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesGryffindor: List<HashMap<String, out Any>> = (chosenImagesGryffindor + chosenImagesGryffindor).shuffled()

        val chosenImagesHufflepuff: List<HashMap<String, out Any>> = list2.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesHufflepuff: List<HashMap<String, out Any>> = (chosenImagesHufflepuff + chosenImagesHufflepuff).shuffled()

        val chosenImagesRavenclaw: List<HashMap<String, out Any>> = list3.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesRavenclaw: List<HashMap<String, out Any>> = (chosenImagesRavenclaw + chosenImagesRavenclaw).shuffled()

        val chosenImagesSlytherin: List<HashMap<String, out Any>> = list4.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesSlytherin: List<HashMap<String, out Any>> = (chosenImagesSlytherin + chosenImagesSlytherin).shuffled()

        randomizedImages = randomizedImagesGryffindor + randomizedImagesHufflepuff + randomizedImagesRavenclaw + randomizedImagesSlytherin
        randomizedImages = randomizedImages.shuffled()

        cards = randomizedImages.map { MemoryCard(it) }
        //print cards on the board in order
        //file location: View-->Tool Windows-->Device File Explorer-->data-->data-->package name (com.robtad.memorymaster)-->files

        writeToFile("cards_on_board")
    }

    //to print contents of cards on the board: code down
    fun writeToFile(fileName: String) {
        val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))
        var i = 0
        var j = 1
        for (item in randomizedImages) {
            i++
            if(i == 1){
                bufferedWriter.write("---------row $i---------")
                Log.i(TAG, "---------row $i---------")

                bufferedWriter.newLine()
            }
            bufferedWriter.write(item["card"].toString() + " of house " + item["house"])
            bufferedWriter.newLine()
            //write on Logcat
            Log.i(TAG, "${item["card"].toString()} of house ${item["house"]}")

            if(i % boardSize.getWidth() == 0 && i < boardSize.numCards){
                j++
                bufferedWriter.newLine()
                bufferedWriter.write("---------row $j---------")
                Log.i(TAG, "---------row $j---------")
                bufferedWriter.newLine()
            }

        }
        bufferedWriter.close()
    }

    //print cards that are displayed on the board: code Up


    fun flipCard(position: Int) : Boolean{
        numCardFlips++
        val card: MemoryCard = cards[position]
        //Logic: flipping cards previously flipped on the click of the third card
        //Three cases:
        //case1: 0 card previously flipped over >>> restore cards + flip over the selected card
        //case1: 1 card previously flipped over >>> flip over the selected card + check if the images match
        //case2: 2 cards previously flipped over >>> restore cards + flip over the selected card
        //Note: case1 and case2 are the same
        var foundMatch = false
        if(indexOfSingleSelectedCard == null){
            //0 or 2 cards previously flipped over
            restoreCards()
            indexOfSingleSelectedCard = position

        }else{
            //exactly one card previously flipped over
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }


        card.isFaceUp = !card.isFaceUp //flipping over the card when user clicks the card
        return foundMatch
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
                score1 += scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)
            }
            else{//playerFlag % 2 == 0 ---> turn of the second player
                score2 += scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)
            }
            //score += scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)

            indexOfCurrentCard = null
        }

        //return score
    }

    private fun scoreCalculator(position1: Int, position2: Int, gameType: Int): Float {

        //Find a way to check the houses of the cards that are not matched
        var point: Float = 0.0F
        var house1 = cards[position1].identifier["house"]
        var house2 = cards[position2].identifier["house"]
        var housePoint1: Float = cards[position1].identifier["housePoint"].toString().toFloat()
        var housePoint2: Float = cards[position2].identifier["housePoint"].toString().toFloat()
        var cardPoint1: Float = cards[position1].identifier["cardPoint"].toString().toFloat()
        var cardPoint2: Float = cards[position2].identifier["cardPoint"].toString().toFloat()



        if(cards[position1].identifier != cards[position2].identifier){
            if (house1 == house2){ //if cards do not match but from the house
                point -= ((cardPoint1 + cardPoint2)/ housePoint1 )
            }else{ //if cards do not match and not from the same house
                point -= (((cardPoint1 + cardPoint2)/2) * housePoint1 * housePoint2 )
            }
            //return score
        }else{ //if cards match. (i.e same card from the same house)
            point += (2*cardPoint1*housePoint1)
        }

        return point
    }
    //////// GAME SCORE LOGIC UP HERE


    private fun checkForMatch(position1: Int, position2: Int): Boolean {

        //Find a way to check the houses of the cards that are not matched

        if(cards[position1].identifier != cards[position2].identifier){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++


        return true
    }



    private fun restoreCards() {
        for (card : MemoryCard in cards){
            if(!card.isMatched){ //restore the cards that are not matched
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        //if numPairsFound == total numPairs then it is a win
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips/2
    }


}
