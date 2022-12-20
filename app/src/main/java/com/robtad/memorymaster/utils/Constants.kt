package com.robtad.memorymaster.utils

import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Base64
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.robtad.memorymaster.GameModeActivity

import com.robtad.memorymaster.R
import java.io.ByteArrayOutputStream

/*
val DEFAULT_ICONS_GRYFFINDOR = listOf(
    hashMapOf("image" to R.drawable.ic_g0, "house" to "Gryffindor", "card" to "AD", "housePoint" to 2, "cardPoint" to 20),
    hashMapOf("image" to R.drawable.ic_g1, "house" to "Gryffindor", "card" to "SB", "housePoint" to 2, "cardPoint" to 18),
    hashMapOf("image" to R.drawable.ic_g2, "house" to "Gryffindor", "card" to "AW", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_g3, "house" to "Gryffindor", "card" to "LP", "housePoint" to 2, "cardPoint" to 12),
    hashMapOf("image" to R.drawable.ic_g4, "house" to "Gryffindor", "card" to "HP", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_g5, "house" to "Gryffindor", "card" to "HG", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_g6, "house" to "Gryffindor", "card" to "MM", "housePoint" to 2, "cardPoint" to 13),
    hashMapOf("image" to R.drawable.ic_g7, "house" to "Gryffindor", "card" to "PP", "housePoint" to 2, "cardPoint" to 5),
    hashMapOf("image" to R.drawable.ic_g8, "house" to "Gryffindor", "card" to "RL", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_g9, "house" to "Gryffindor", "card" to "RW", "housePoint" to 2, "cardPoint" to 8),
    hashMapOf("image" to R.drawable.ic_g10, "house" to "Gryffindor", "card" to "RH", "housePoint" to 2, "cardPoint" to 12)

    )
val DEFAULT_ICONS_HUFFLEPUFF = listOf(
    hashMapOf("image" to R.drawable.ic_h0, "house" to "Hufflepuff", "card" to "cd", "housePoint" to 1, "cardPoint" to 18),
    hashMapOf("image" to R.drawable.ic_h1, "house" to "Hufflepuff", "card" to "TL", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_h2, "house" to "Hufflepuff", "card" to "EM", "housePoint" to 1, "cardPoint" to 5),
    hashMapOf("image" to R.drawable.ic_h3, "house" to "Hufflepuff", "card" to "FF", "housePoint" to 1, "cardPoint" to 12),
    hashMapOf("image" to R.drawable.ic_h4, "house" to "Hufflepuff", "card" to "HA", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_h5, "house" to "Hufflepuff", "card" to "HH", "housePoint" to 1, "cardPoint" to 20),
    hashMapOf("image" to R.drawable.ic_h6, "house" to "Hufflepuff", "card" to "LE", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_h7, "house" to "Hufflepuff", "card" to "NS", "housePoint" to 1, "cardPoint" to 18),
    hashMapOf("image" to R.drawable.ic_h8, "house" to "Hufflepuff", "card" to "NT", "housePoint" to 1, "cardPoint" to 14),
    hashMapOf("image" to R.drawable.ic_h9, "house" to "Hufflepuff", "card" to "PS", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_h10, "house" to "Hufflepuff", "card" to "SK", "housePoint" to 1, "cardPoint" to 12)

)
val DEFAULT_ICONS_RAVENCLAW = listOf(
    hashMapOf("image" to R.drawable.ic_r0, "house" to "Ravenclaw", "card" to "CC", "housePoint" to 1, "cardPoint" to 11),
    hashMapOf("image" to R.drawable.ic_r1, "house" to "Ravenclaw", "card" to "ST", "housePoint" to 1, "cardPoint" to 14),
    hashMapOf("image" to R.drawable.ic_r2, "house" to "Ravenclaw", "card" to "FF", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_r3, "house" to "Ravenclaw", "card" to "GO", "housePoint" to 1, "cardPoint" to 15),
    hashMapOf("image" to R.drawable.ic_r4, "house" to "Ravenclaw", "card" to "GL", "housePoint" to 1, "cardPoint" to 13),
    hashMapOf("image" to R.drawable.ic_r5, "house" to "Ravenclaw", "card" to "LL", "housePoint" to 1, "cardPoint" to 9),
    hashMapOf("image" to R.drawable.ic_r6, "house" to "Ravenclaw", "card" to "MB", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_r7, "house" to "Ravenclaw", "card" to "MW", "housePoint" to 1, "cardPoint" to 5),
    hashMapOf("image" to R.drawable.ic_r8, "house" to "Ravenclaw", "card" to "PP", "housePoint" to 1, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_r9, "house" to "Ravenclaw", "card" to "QQ", "housePoint" to 1, "cardPoint" to 15),
    hashMapOf("image" to R.drawable.ic_r10, "house" to "Ravenclaw", "card" to "RR", "housePoint" to 1, "cardPoint" to 20)

)
val DEFAULT_ICONS_SLYTHERIN = listOf(
    hashMapOf("image" to R.drawable.ic_s0, "house" to "Slytherin", "card" to "AT", "housePoint" to 2, "cardPoint" to 16),
    hashMapOf("image" to R.drawable.ic_s1, "house" to "Slytherin", "card" to "TR", "housePoint" to 2, "cardPoint" to 20),
    hashMapOf("image" to R.drawable.ic_s2, "house" to "Slytherin", "card" to "BL", "housePoint" to 2, "cardPoint" to 13),
    hashMapOf("image" to R.drawable.ic_s3, "house" to "Slytherin", "card" to "DU", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_s4, "house" to "Slytherin", "card" to "DM", "housePoint" to 2, "cardPoint" to 5),
    hashMapOf("image" to R.drawable.ic_s5, "house" to "Slytherin", "card" to "ER", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_s6, "house" to "Slytherin", "card" to "HS", "housePoint" to 2, "cardPoint" to 12),
    hashMapOf("image" to R.drawable.ic_s7, "house" to "Slytherin", "card" to "LL", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_s8, "house" to "Slytherin", "card" to "LM", "housePoint" to 2, "cardPoint" to 12),
    hashMapOf("image" to R.drawable.ic_s9, "house" to "Slytherin", "card" to "NM", "housePoint" to 2, "cardPoint" to 10),
    hashMapOf("image" to R.drawable.ic_s10, "house" to "Slytherin", "card" to "SS", "housePoint" to 2, "cardPoint" to 18)

)
 */

val list1 = GameModeActivity.ListsHolder.DEFAULT_ICONS_GRYFFINDOR
val list2 = GameModeActivity.ListsHolder.DEFAULT_ICONS_HUFFLEPUFF
val list3 = GameModeActivity.ListsHolder.DEFAULT_ICONS_RAVENCLAW
val list4 = GameModeActivity.ListsHolder.DEFAULT_ICONS_SLYTHERIN
