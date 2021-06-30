package com.example.vedioplayer.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vedioplayer.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class showVedio : Fragment(R.layout.vedio_show) {
    
 private lateinit var simpleExoPlayer: SimpleExoPlayer
 private lateinit var playerView : PlayerView

 val args : showVedioArgs by navArgs()

 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  super.onViewCreated(view, savedInstanceState)

  val context = view.context
  playerView = view.findViewById(R.id.exoplayer_view)
   val uri = args.vedioUrl
   simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
 // val mediaItem = MediaItem.fromUri(uri)
   val dataSouceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context,"show vedio"))
   val mediaSource = ProgressiveMediaSource.Factory(dataSouceFactory).createMediaSource(Uri.parse(uri))
   simpleExoPlayer.setMediaSource(mediaSource)
   simpleExoPlayer.prepare()
   simpleExoPlayer.playWhenReady = true
  playerView.player = simpleExoPlayer

 }

 override fun onStop() {
  super.onStop()
   simpleExoPlayer.release()
 }







}