package com.example.carservice.ui.appointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppointmentsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is appointments Fragment"
    }
    val text: LiveData<String> = _text
}