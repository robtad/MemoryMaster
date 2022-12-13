package com.robtad.memorymaster.models

import com.robtad.memorymaster.utils.DEFAULT_ICONS_GRYFFINDOR
import com.robtad.memorymaster.utils.DEFAULT_ICONS_HUFFLEPUFF
import com.robtad.memorymaster.utils.DEFAULT_ICONS_RAVENCLAW
import com.robtad.memorymaster.utils.DEFAULT_ICONS_SLYTHERIN
import java.util.HashMap

class MemoryGame(private val boardSize: BoardSize){

    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null
    private var indexOfCurrentCard: Int? = null
    var score: Float = 0.0F
    private var gameTypeTag = 0 //gameTypeTage is an integer variable to denote if the game is single or multi player

    init {
        //How pictures will be selected to be displayed on the board
        val chosenImagesGryffindor: List<HashMap<String, out Any>> = DEFAULT_ICONS_GRYFFINDOR.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesGryffindor: List<HashMap<String, out Any>> = (chosenImagesGryffindor + chosenImagesGryffindor).shuffled()

        val chosenImagesHufflepuff: List<HashMap<String, out Any>> = DEFAULT_ICONS_HUFFLEPUFF.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesHufflepuff: List<HashMap<String, out Any>> = (chosenImagesHufflepuff + chosenImagesHufflepuff).shuffled()

        val chosenImagesRavenclaw: List<HashMap<String, out Any>> = DEFAULT_ICONS_RAVENCLAW.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesRavenclaw: List<HashMap<String, out Any>> = (chosenImagesRavenclaw + chosenImagesRavenclaw).shuffled()

        val chosenImagesSlytherin: List<HashMap<String, out Any>> = DEFAULT_ICONS_SLYTHERIN.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesSlytherin: List<HashMap<String, out Any>> = (chosenImagesSlytherin + chosenImagesSlytherin).shuffled()

        var randomizedImages = randomizedImagesGryffindor + randomizedImagesHufflepuff + randomizedImagesRavenclaw + randomizedImagesSlytherin
        randomizedImages = randomizedImages.shuffled()

        cards = randomizedImages.map { MemoryCard(it) }
    }


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
            score += scoreCalculator(indexOfCurrentCard!!, position, gameTypeTag)

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
