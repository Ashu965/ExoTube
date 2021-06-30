package com.example.vedioplayer.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.vedioplayer.R
import com.example.vedioplayer.Vedio
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

private const val REQUEST_CODE_VEDIO_PICK = 0
private const val REQUEST_CODE_IMAGE_PICK = 1

class AddVedioFragment : Fragment(R.layout.fragment_addvedio){
    lateinit var editText: EditText
    lateinit var addBtn : Button
    lateinit var videoView : VideoView
    lateinit var imageView: ImageView
    lateinit var uploadImageProgressBar: ProgressBar
    lateinit var uploadVideoProgressBar: ProgressBar
    var curImageFile: Uri? = null
    var curVedioFile: Uri? = null
    val storageRef = Firebase.storage.reference
    val fireStoreRef = Firebase.firestore.collection("videos")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText = view.findViewById(R.id.editText)
        addBtn = view.findViewById(R.id.addbutton)
        videoView = view.findViewById(R.id.addvideoView)
        imageView = view.findViewById(R.id.imageView)
        uploadImageProgressBar = view.findViewById(R.id.uploadImageProgressBar)
        uploadVideoProgressBar = view.findViewById(R.id.uploadVideoProgressBar)

        videoView.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "video/*"
                startActivityForResult(it, REQUEST_CODE_VEDIO_PICK)
            }
        }
        imageView.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }
        addBtn.setOnClickListener{
        uploadDataToStorage()
        }
    }


     @RequiresApi(Build.VERSION_CODES.N)
     fun uploadDataToStorage() = CoroutineScope(Dispatchers.IO).launch{
        try{
            val filename = editText.text.toString()
            curImageFile?.let {
               // uploadImageProgressBar.visibility = VISIBLE
              storageRef.child("$filename.jpg").putFile(it).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred)/ it.totalByteCount
                uploadImageProgressBar.setProgress(progress.toInt(),true)
              }.await()
               // uploadImageProgressBar.visibility = INVISIBLE
                withContext(Dispatchers.Main){
                 Toast.makeText(context,"Successfully uploaded image in storage",Toast.LENGTH_SHORT).show()
             }
            }
            curVedioFile?.let {
              //  uploadVideoProgressBar.visibility = VISIBLE
             storageRef.child("$filename.mp4").putFile(it).addOnProgressListener {
                 val progress = (100.0 * it.bytesTransferred)/it.totalByteCount
                 uploadVideoProgressBar.setProgress(progress.toInt(),true)
             }.await()
            //    uploadVideoProgressBar.visibility = INVISIBLE
                withContext(Dispatchers.Main){
                 Toast.makeText(context,"Successfully uploaded vedio in storage",Toast.LENGTH_SHORT).show()
             }
            }
            uploadDataTOFireStore()
          }
        catch (e : Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context,e.message,Toast.LENGTH_SHORT)
            }
        }
     }



    fun uploadDataTOFireStore() = CoroutineScope(Dispatchers.IO).launch{
        try{
            Log.i("URL","firestore start....")
            val filename = editText.text.toString()
            val imageUrl = storageRef.child("$filename.jpg").downloadUrl.await()
            Log.i("URL",imageUrl.toString())
            val vedioUrl = storageRef.child("$filename.mp4").downloadUrl.await()
            Log.i("URL",vedioUrl.toString())
            val vedioObj = Vedio(filename,imageUrl.toString(),vedioUrl.toString())
            fireStoreRef.add(vedioObj).await()
            withContext(Dispatchers.Main){
                Toast.makeText(context,"VedioObject upload to firestore",Toast.LENGTH_LONG).show()
            }
        }
        catch (e : Exception){
            Log.i("URL" ,"Firestop not start....")
            withContext(Dispatchers.Main){
                Toast.makeText(context,e.message,Toast.LENGTH_SHORT)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode == REQUEST_CODE_VEDIO_PICK){
            data?.data?.let {
               curVedioFile = it
               videoView.setVideoURI(it)
                videoView.start()


            }
        }
        if(resultCode== Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK){
            data?.data?.let {
                curImageFile = it
                imageView.setImageURI(it)
            }
        }
    }
}