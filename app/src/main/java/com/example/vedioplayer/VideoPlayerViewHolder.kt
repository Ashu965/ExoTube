package com.example.vedioplayer

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class VideoPlayerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    lateinit var requestManager: RequestManager
    val title = itemView.findViewById<TextView>(R.id.title)
    val thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
    val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
    val mediaContainer = itemView.findViewById<FrameLayout>(R.id.media_container)
    val parent = itemView

    fun onBind(mediaObject: Vedio, requestManager: RequestManager) {
        this.requestManager = requestManager
        parent.setTag(this)
        title.setText(mediaObject.title)
        this.requestManager.load(mediaObject.imageUrl).into(thumbnail)

    }
}




