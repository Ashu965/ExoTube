package com.example.vedioplayer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoPlayerRecyclerView(
    context: Context,attributeSet: AttributeSet
)  :  RecyclerView(context,attributeSet) {

    val TAG = "videoRecycleView"
    enum class VolumeState { ON, OFF }

    private var mediaObjects: List<Vedio> = ArrayList()
    lateinit var thumbnail: ImageView
    lateinit var volumeControl: ImageView
    private lateinit var frameLayout: FrameLayout
    var videoSurfaceView: PlayerView
    var videoPlayer: SimpleExoPlayer
    lateinit var progressBar : ProgressBar
    private lateinit var requestManager : RequestManager

    var viewHolderParent : View? = null

    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var playPosition =-1
    private var isVideoViewAdded = false

    lateinit var volumeState: VolumeState

    init {
        videoSurfaceDefaultHeight = resources.displayMetrics.widthPixels
        screenDefaultHeight = resources.displayMetrics.heightPixels

        videoSurfaceView = PlayerView(context)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        videoPlayer = SimpleExoPlayer.Builder(context).build()
        videoSurfaceView.useController = false
        videoSurfaceView.player = videoPlayer

        addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: called.");
                    if (::thumbnail.isInitialized)
                        thumbnail.visibility = VISIBLE
                }
                //When the end of List is Reached
                if (!canScrollVertically(1)){
                    Log.i(TAG,"Cannot scrool vertically called")
                    playVideo(true)}
                else {
                    playVideo(false)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })

        /*addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener{
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if(viewHolderParent!=null && viewHolderParent!!.equals(view))
                    resetVedioView()
            }

        })*/

        //Controlling playback for the Exoplayer
        videoPlayer.addListener(object : Player.Listener{

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when(playbackState){
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0)
                    }
                    Player.STATE_BUFFERING -> {
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if(::progressBar.isInitialized)
                            progressBar.visibility = VISIBLE}
                    Player.STATE_READY -> {
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if(::progressBar.isInitialized) progressBar.visibility = INVISIBLE

                       if(!isVideoViewAdded) addVedioView()
                    }
                }

            }
        })

    }

    fun playVideo(isEndOfList: Boolean) {
        var targetPosition = 0 // position which gona have vedio
        if (!isEndOfList) {
            Log.d(TAG, "list length" + mediaObjects.size)
            var startPosition =
                (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            var endPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            Log.d(TAG, "first visible: " + startPosition)
            Log.d(TAG, "last visible: " + endPosition)
            if (endPosition - startPosition > 1)
                endPosition = startPosition + 1

            if (startPosition < 0 || endPosition < 0)
                return

            if (endPosition != startPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)

                targetPosition = if (endPositionVideoHeight < startPositionVideoHeight) {
                    startPosition
                } else {
                    endPosition
                }
            } else {
                targetPosition = startPosition
            }
        } else {
            targetPosition = mediaObjects.size - 1
        }
        Log.d(TAG, "playVideo: target position: " + targetPosition);
          if (targetPosition == playPosition)
            return;

        playPosition = targetPosition

        if(videoSurfaceView==null)
            return

        videoSurfaceView.visibility = INVISIBLE
        if(::progressBar.isInitialized)
        progressBar.visibility= INVISIBLE
        if(isVideoViewAdded)
        removeVideoView(videoSurfaceView)

        val currentPosition = targetPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val child = getChildAt(currentPosition)
        if(child==null)
            return

       val holder =  child.getTag() as VideoPlayerViewHolder
        if(holder==null) {
            playPosition = -1
            return
        }

        viewHolderParent = holder.itemView
        thumbnail = holder.thumbnail
        progressBar = holder.progressBar
        //volumeControl = holder.volumeControl
        requestManager = holder.requestManager
        frameLayout = holder.mediaContainer
        
        //addVedioView()
        videoSurfaceView.player = videoPlayer
            //videoHolderParent.setOnClickListener(vedioViewClickListener)

        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context,"rvVedioPlayer"))
        val mediaUrl = mediaObjects[targetPosition].vedioUrl
        if(mediaUrl != null){
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                             .createMediaSource(Uri.parse(mediaUrl))
            videoPlayer.setMediaSource(videoSource)
            videoPlayer.prepare()
            videoPlayer.playWhenReady = true
        }
    }

    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at =
            playPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: $at")
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location) // give view top-left corner coordinates
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    fun addVedioView(){
       frameLayout.addView(videoSurfaceView)
       isVideoViewAdded = true
      //  videoSurfaceView.requestFocus()
       videoSurfaceView.visibility = VISIBLE
       videoSurfaceView.alpha= 1f
       thumbnail.visibility = INVISIBLE
    }

    fun resetVedioView(){
        if(isVideoViewAdded){
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView.visibility = INVISIBLE
            thumbnail.visibility = VISIBLE
        }
    }

    fun removeVideoView(videoView : PlayerView){
        thumbnail.visibility = VISIBLE
        var parent = videoView.parent
        if(parent==null)
            return
        parent = parent as ViewGroup
        val index = parent.indexOfChild(videoView)
        if(index>=0){
            parent.removeViewAt(index)
            isVideoViewAdded = false
        }
    }

    fun releasePlayer(){
        if(videoPlayer!=null){
            videoPlayer.release()
        }
        viewHolderParent = null
    }

    fun setMediaObjects(mediaObjects : List<Vedio>){
        this.mediaObjects = mediaObjects
    }

}