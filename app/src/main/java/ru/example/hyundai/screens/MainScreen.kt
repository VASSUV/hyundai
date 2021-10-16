package ru.example.hyundai.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.example.hyundai.ui.theme.HyundaiTheme
import ru.example.hyundai.ui.theme.Link
import ru.example.hyundai.viewmodels.MainViewModel
import ru.example.hyundai.viewmodels.ProfileViewModel
import ru.example.hyundai.viewmodels.ServiceViewModel

@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel(),
    serviceViewModel: ServiceViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    Scaffold {
        Column {
            TopAppBar {
                Text(
                    text = "Отслеживание машин Hyundai",
                    modifier = Modifier.padding(horizontal = Dp(16f))
                )
            }
            PlayPause(
                "Запуск отслеживания",
                serviceViewModel::startService,
                serviceViewModel::stopService
            )
            Profile(profileViewModel, openProfile = { navController.navigate("profile") })
            CarItem(onTap = { navController.navigate("cars") })
            ColorItem(onTap = { navController.navigate("colors") })
        }
    }
}

@Composable
fun Profile(profileViewModel: ProfileViewModel, openProfile: () -> Unit) {
    val profile by profileViewModel.profile.observeAsState()
    if(profile != null) {
        Row(modifier = Modifier.clickable { openProfile() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .weight(1f)) {
                Text(
                    text = "Привет, ${profile?.firstName}",
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            Text(
                text = AnnotatedString("Профиль", SpanStyle(color = Link)),
                style = MaterialTheme.typography.h6,
            )
        }
    }
}

@Composable
private fun CarItem(onTap: () -> Unit) {
    Box(modifier = Modifier.clickable { onTap() }.fillMaxWidth().padding(16.dp) ) {
        Text(
            text = "Выбор авто",
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}

@Composable
private fun ColorItem(onTap: () -> Unit) {
    Box(modifier = Modifier.clickable { onTap() }.fillMaxWidth().padding(16.dp) ) {
        Text(
            text = "Выбор цвета",
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}

@Composable
private fun PlayPause(title: String, onStart: () -> Unit, onStop: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(Dp(8f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier
            .weight(1f)
            .height(Dp(56f))) {
            Text(text = title, modifier = Modifier.align(Alignment.Center))
        }
        Button(onClick = onStart) {
            Icon(
                painter = painterResource(id = R.drawable.ic_media_play),
                contentDescription = "Start Service",
            )
        }
        Spacer(modifier = Modifier.width(Dp(8f)))
        Button(onClick = onStop) {
            Icon(
                painter = painterResource(id = R.drawable.ic_media_pause),
                contentDescription = "Start Service",
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun DefaultPreview() {
    HyundaiTheme {
        Text("")
    }
}