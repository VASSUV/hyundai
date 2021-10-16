package ru.example.hyundai.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.example.hyundai.viewmodels.CarsViewModel

@Composable
fun CarsScreen(carsViewModel: CarsViewModel) {
    Scaffold {
        Column {
            TopAppBar {
                Text(
                    text = "Выбор автомобиля",
                    modifier = Modifier.padding(horizontal = Dp(16f))
                )
            }
            val cars = carsViewModel.getAllCars().observeAsState()
            for (car in cars.value?.carsListList ?: arrayListOf()) {
                CarItem(car.name, car.img)
            }
        }
    }
}

@Composable
fun CarItem(name: String, url: String) {
    Row(
        modifier = Modifier
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = name,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
//        Image(
//            painter = rememberImagePainter(),
//            contentDescription = null,
//            modifier = Modifier.size(56.dp)
//        )
    }
}