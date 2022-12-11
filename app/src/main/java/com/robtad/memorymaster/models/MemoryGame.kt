package com.robtad.memorymaster.models

import com.robtad.memorymaster.utils.DEFAULT_ICONS_GRYFFINDOR
import com.robtad.memorymaster.utils.DEFAULT_ICONS_HUFFLEPUFF
import com.robtad.memorymaster.utils.DEFAULT_ICONS_RAVENCLAW
import com.robtad.memorymaster.utils.DEFAULT_ICONS_SLYTHERIN

class MemoryGame(private val boardSize: BoardSize){

    val cards: List<MemoryCard>
    var numPairsFound = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        //How pictures will be selected to be displayed on the board
        val chosenImagesGryffindor: List<Int> = DEFAULT_ICONS_GRYFFINDOR.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesGryffindor: List<Int> = (chosenImagesGryffindor + chosenImagesGryffindor).shuffled()

        val chosenImagesHufflepuff: List<Int> = DEFAULT_ICONS_HUFFLEPUFF.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesHufflepuff: List<Int> = (chosenImagesHufflepuff + chosenImagesHufflepuff).shuffled()

        val chosenImagesRavenclaw: List<Int> = DEFAULT_ICONS_RAVENCLAW.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesRavenclaw: List<Int> = (chosenImagesRavenclaw + chosenImagesRavenclaw).shuffled()

        val chosenImagesSlytherin: List<Int> = DEFAULT_ICONS_SLYTHERIN.shuffled().take(boardSize.getNumPairs()/4)
        val randomizedImagesSlytherin: List<Int> = (chosenImagesSlytherin + chosenImagesSlytherin).shuffled()

        var randomizedImages = randomizedImagesGryffindor + randomizedImagesHufflepuff + randomizedImagesRavenclaw + randomizedImagesSlytherin
        randomizedImages = randomizedImages.shuffled()

        cards = randomizedImages.map { MemoryCard(it) }
    }


    fun flipCard(position: Int) : Boolean{
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


}
