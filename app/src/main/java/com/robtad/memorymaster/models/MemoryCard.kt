package com.robtad.memorymaster.models

import java.util.HashMap


data class MemoryCard(
    val identifier: HashMap<String, out Any>,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)