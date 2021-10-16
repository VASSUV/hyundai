package ru.example.hyundai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServiceViewModel: ViewModel() {
    enum class Event {
        START, STOP
    }

    private val eventLiveData = MutableLiveData<Event>()

    fun getEventLiveData(): LiveData<Event> = eventLiveData

    fun startService() {
        eventLiveData.value = Event.START
    }

    fun stopService() {
        eventLiveData.value = Event.STOP
    }
}