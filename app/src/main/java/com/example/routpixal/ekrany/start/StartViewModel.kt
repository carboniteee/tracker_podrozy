package com.example.routpixal.ekrany.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fragment ze startem"
    }
    val text: LiveData<String> = _text
}