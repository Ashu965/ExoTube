package com.example.vedioplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager

class VideoPlayerAdapter(val requestManager: RequestManager,val itemClick : (String) -> Unit) : RecyclerView.Adapter<VideoPlayerViewHolder>() {

    var mediaObjects = listOf<Vedio>()
    fun setVedios(mediaObjects: List<Vedio>){
        this.mediaObjects= mediaObjects
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPlayerViewHolder {
          val view = LayoutInflater.from(parent.context).inflate(R.layout.video_list_item,parent,false)
          return VideoPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoPlayerViewHolder, position: Int) {
        holder.onBind(mediaObjects[position],requestManager)
        holder.mediaContainer.setOnClickListener{
             itemClick(mediaObjects[position].vedioUrl)
        }
    }


    override fun getItemCount(): Int {
        return mediaObjects.size
    }

}