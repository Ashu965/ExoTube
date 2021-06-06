package com.example.vedioplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VedioViewModel : ViewModel(){
    var vediosLiveData : MutableLiveData<List<Vedio>> = MutableLiveData()

}