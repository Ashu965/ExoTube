package com.example.vedioplayer

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.vedioplayer.constants.VEDIO_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class VedioDatabase {

    private val fireStore = FirebaseFirestore.getInstance()
    private val vedioCollection = fireStore.collection(VEDIO_COLLECTION)
    val vediosLiveData : MutableLiveData<List<Vedio>> = MutableLiveData()

    init {
        getAllVedios()
    }

    fun getAllVedios() {
       /* try{
          return  vedioCollection.get().await().toObjects(Vedio::class.java)
        }
        catch (e : Exception){
            return emptyList()
        }*/

         

        vedioCollection.addSnapshotListener { value, error ->
            if(value!=null)
              vediosLiveData.postValue(value.toObjects(Vedio::class.java))
        }
    }
}