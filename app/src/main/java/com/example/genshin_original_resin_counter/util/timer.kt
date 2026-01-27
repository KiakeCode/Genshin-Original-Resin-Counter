package com.example.genshin_original_resin_counter.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt


@SuppressLint("UnrememberedMutableState")

fun Timer(resin: MutableState<String>): MutableState<String> {
    val r: MutableState<String> = mutableStateOf("${(200 - resin.value.toInt()) * 8}")
    return mutableStateOf(formatTimer(r))
}


@SuppressLint("DefaultLocale")
fun formatTimer(value: MutableState<String>): String {
    Log.d("format", value.value.toInt().toString())
    var seconds = (value.value.toFloat() * 60).roundToInt() % 60
    var minutes = (value.value.toFloat() % 60).roundToInt()
    var hours = (value.value.toFloat() / 60).roundToInt()
    return "${hours}h : ${minutes}m : ${seconds}s"
}