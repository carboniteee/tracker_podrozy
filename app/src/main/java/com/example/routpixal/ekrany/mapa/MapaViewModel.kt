package com.example.routpixal.ekrany.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fragment z mapą"
    }
    val text: LiveData<String> = _text
}