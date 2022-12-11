package com.robtad.memorymaster.models

enum class BoardSize (val numCards: Int){
    EASY(numCards = 8),     //4
    MEDIUM(numCards = 16),  //16
    HARD(numCards = 32);    //36

    fun getWidth(): Int { //getWidth returns number of columns
        return when (this){
            EASY -> 2   //2
            MEDIUM -> 4 //4
            HARD -> 4   //6
        }
    }

    fun getHeight(): Int { //getHeight returns number os rows
        return numCards/getWidth()
    }

    fun getNumPairs(): Int { //getNumPairs returns total number of pairs of cards
        return numCards/2
    }
}