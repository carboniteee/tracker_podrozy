package com.example.routpixal.ekrany.lista

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fragment z listą"
    }
    val text: LiveData<String> = _text
}