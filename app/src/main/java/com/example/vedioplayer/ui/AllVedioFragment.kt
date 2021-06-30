package com.example.vedioplayer.ui

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.vedioplayer.*

class AllVedioFragment : Fragment(R.layout.fragment_allvedio) {

    private lateinit var mRecyclerView: VideoPlayerRecyclerView
    private lateinit var vedioAdapter: VideoPlayerAdapter
    private val viewModel = VedioViewModel()
    private val vedioDatabase = VedioDatabase()


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu,menu)
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                   fireStoreSearch(query)
                   return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                   fireStoreSearch(newText)
                   return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        vedioAdapter = VideoPlayerAdapter(provideGlideInstance()){
            val action = AllVedioFragmentDirections.actionAllVedioFragmentToShowVedio(it)
             findNavController().navigate(action)
        }

        mRecyclerView.adapter = vedioAdapter
        val itemDecorator = VerticalSpacingItemDecorator(10)
        mRecyclerView.addItemDecoration(itemDecorator)


        mRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            mRecyclerView.playVideo(false)
        }
        fireStoreSearch("")
    }
    private fun fireStoreSearch(text : String?){
        vedioDatabase.vediosLiveData.observe(viewLifecycleOwner, { vedioList ->
            var newlist = when(text){
                null -> vedioList
                else -> vedioList.filter {
                    it.title.contains(text,true)
                }
            }
            mRecyclerView.setMediaObjects(newlist)
            vedioAdapter.setVedios(newlist)
        })
    }


    private fun provideGlideInstance() = Glide.with(this).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image) // set given graphic as placeholder in case image is not fully loaded.
            .error(R.drawable.ic_image) // set when something when wrong then what to display
            .diskCacheStrategy(DiskCacheStrategy.DATA) // this  make sure that images are cached with Glide
    )
    override fun onPause() {
        if(::mRecyclerView.isInitialized)
            mRecyclerView.videoPlayer.pause()
        super.onPause()
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