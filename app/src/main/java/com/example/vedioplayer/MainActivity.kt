package com.example.vedioplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class MainActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: VideoPlayerRecyclerView
    private lateinit var vedioAdapter: VideoPlayerAdapter
    private val viewModel = VedioViewModel()
    private val vedioDatabase = VedioDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_VedioPlayer)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        vedioAdapter = VideoPlayerAdapter(provideGlideInstance())
        mRecyclerView.adapter = vedioAdapter
        val itemDecorator = VerticalSpacingItemDecorator(10)
        mRecyclerView.addItemDecoration(itemDecorator)


        mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            mRecyclerView.playVideo(false)
        }
        vedioDatabase.vediosLiveData.observe(this, Observer { vedioList ->
            mRecyclerView.setMediaObjects(vedioList)
            vedioAdapter.setVedios(vedioList)
        })
    }
    private fun provideGlideInstance() = Glide.with(this).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image) // set given graphic as placeholder in case image is not fully loaded.
            .error(R.drawable.ic_image) // set when something when wrong then what to display
            .diskCacheStrategy(DiskCacheStrategy.DATA) // this  make sure that images are cached with Glide
    )

    override fun onStop() {
        if(::mRecyclerView.isInitialized)
            mRecyclerView.videoPlayer.pause()
        super.onStop()
    }

    override fun onResume() {
        if(::mRecyclerView.isInitialized)
            mRecyclerView.videoPlayer.play()
        super.onResume()
    }
    override fun onDestroy() {
        if(::mRecyclerView.isInitialized)
            mRecyclerView.releasePlayer()
        super.onDestroy()
    }
}