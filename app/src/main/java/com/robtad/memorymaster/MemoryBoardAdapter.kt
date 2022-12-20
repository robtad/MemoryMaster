package com.robtad.memorymaster

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.robtad.memorymaster.models.BoardSize
import com.robtad.memorymaster.models.MemoryCard
import kotlin.math.roundToInt

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
    companion object{ //companion objects are where constants are defined in kotlin.
                      // Its members can be accessed directly through the containing class
    private const val MARGIN_SIZE = 10
    private const val TAG = "MemoryBoardAdapter"

    }

    interface CardClickListener{

        fun onCardClicked(position: Int)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val cardWidth: Float = parent.width.toFloat()/boardSize.getWidth() - (2*MARGIN_SIZE) //parent is the recyclerview
        val cardHeight: Float = parent.height.toFloat()/boardSize.getHeight() - (2*MARGIN_SIZE)
        val cardSideLength: Float = min(cardWidth,cardHeight)

        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        //square and non square cards
        if(boardSize.getWidth() == boardSize.getHeight()){
            layoutParams.width = cardWidth.roundToInt() // the memory cards will be rectangle
            layoutParams.height = cardHeight.roundToInt()
        }
        else{
            layoutParams.width = cardSideLength.roundToInt() // the memory cards will be square
            layoutParams.height = cardSideLength.roundToInt()
        }

        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)//putting margin on all 4 sides

        return  ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards //total number of cards

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memoryCard: MemoryCard = cards[position]
            //val bitmap = memoryCard.identifier["image"]
            if (memoryCard.isFaceUp)
                imageButton.setImageBitmap(memoryCard.identifier["bitmap"] as Bitmap?)
            else
                imageButton.setImageResource(R.drawable.backface as Int)

            //gray out the images that are matched to give user visual indication: next 3 lines
            imageButton.alpha = if(memoryCard.isMatched) .4f else 1.0f
            val colorStateList = if(memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)

            imageButton.setOnClickListener{
                Log.i(TAG, "Clicked on position $position") //Log.i --> i = info
                cardClickListener.onCardClicked(position)
            }
        }
    }


}
