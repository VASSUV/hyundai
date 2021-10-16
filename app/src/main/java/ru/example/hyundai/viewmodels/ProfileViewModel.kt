package ru.example.hyundai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.example.hyundai.data.Profile
import java.lang.Exception

class ProfileViewModel : ViewModel() {
    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> = _profile

    fun onProfile(profile: String) {
        _profile.value = try { Profile(profile) } catch (e: Exception) { null }
    }
}