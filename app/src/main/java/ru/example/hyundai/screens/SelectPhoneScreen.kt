package ru.example.hyundai.screens

import android.Manifest.*
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.app.ActivityCompat.*
import java.lang.StringBuilder


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("HardwareIds")
@Composable
fun SelectPhoneScreen() {
    Scaffold {
        Column(modifier = Modifier
            .fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    PhoneInput()
                }
            }
        }
    }
}

@Composable
fun PhoneInput() {
    var phone: String by remember { mutableStateOf("") }
    val transformation = PhoneTransformation()
    TextField(
        value = phone.replace("[^\\d]".toRegex(), ""),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        onValueChange = {
            phone = transformation.filter(AnnotatedString(transformation.trimmedPhone(it))).text.text
            println(phone)
        },
        visualTransformation = PhoneTransformation(),
        label = { Text("Введите свой номер") }
    )
}

class PhoneTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }

    fun trimmedPhone(text: String): String {
        var digits = text.replace("[^\\d]".toRegex(), "")
        val isSevenStart = digits.firstOrNull() == '7'
        if (!isSevenStart && digits.isNotEmpty()) {
            digits = "7$digits"
        }
        return if (digits.length >= 11) digits.substring(0..10) else digits
    }

    private fun maskFilter(phone: AnnotatedString): TransformedText {
        var i = 0
        val trimmed = trimmedPhone(phone.text)
        val out = StringBuilder()
        for(char in "+* (***) ***-**-**") {
            if(trimmed.length <= i) break
            if(char != '*') {
                out.append(char)
            } else {
                out.append(trimmed[i])
                i++
            }
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset ==  0) return  0 //
                if (offset ==  1) return  2 // 7
                if (offset ==  2) return  5 // 9
                if (offset ==  3) return  6 // 9
                if (offset ==  4) return  7 // 9
                if (offset ==  5) return 10 // 7
                if (offset ==  6) return 11 // 7
                if (offset ==  7) return 12 // 7
                if (offset ==  8) return 14 // 5
                if (offset ==  9) return 15 // 5
                if (offset == 10) return 17 // 2
                if (offset == 11) return 18 // 2
                return 18
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset ==  0) return  0 // +
                if (offset ==  1) return  0 // 7
                if (offset ==  2) return  0 //
                if (offset ==  3) return  0 // (
                if (offset ==  4) return  2 // *
                if (offset ==  5) return  3 // *
                if (offset ==  6) return  4 // *
                if (offset ==  7) return  4 // )
                if (offset ==  8) return  4 //
                if (offset ==  9) return  5 // *
                if (offset == 10) return  6 // *
                if (offset == 11) return  7 // *
                if (offset == 12) return  7 // -
                if (offset == 13) return  8 // *
                if (offset == 14) return  9 // *
                if (offset == 15) return  9 // -
                if (offset == 16) return 10 // *
                if (offset == 17) return 11 // *
                return 11
            }
        }

        return TransformedText(AnnotatedString(out.toString()), numberOffsetTranslator)
    }
}

