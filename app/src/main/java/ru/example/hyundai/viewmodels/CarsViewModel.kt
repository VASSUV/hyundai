package ru.example.hyundai.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.example.hyundai.gen.Allcars
import java.lang.Exception

class CarsViewModel: ViewModel() {
    private val cars = MutableLiveData<Allcars.AllCars>()

    fun getAllCars(): LiveData<Allcars.AllCars> = cars

    fun onAllCars(carsBinary: ByteArray) {
        val allCars = try {
            Allcars.AllCars.parseFrom(carsBinary)
        } catch (e: Exception) {
            null
        }
        cars.value = allCars
    }

    fun onListenedCars(carsBinary: String) {
        cars.value = try { Allcars.AllCars.parseFrom(carsBinary.byteInputStream()) } catch (e: Exception) { null }
    }
}