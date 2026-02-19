package com.example.genshin_original_resin_counter.util

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


const val totalResin = 200 // Genshin current cap

fun convertResinInTimeLeftMillis(resin: MutableState<String>): Long {
    val currentResin = resin.value.toInt() // Conversion value
    return calculateTimeUntilFinishMillis(value = currentResin)
}

fun calculateTimeUntilFinishMillis(value: Int): Long {
    val totalMinutesUntilFinished = (totalResin - value) * 8
    return (totalMinutesUntilFinished * 1000L * 60) // minutes->*60->seconds->*1000L->milliseconds
}

@SuppressLint("DefaultLocale")
fun formatTimeToString(millis: Long): MutableState<String> {

    val totalSeconds = millis / 1000

    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = (totalSeconds / 60) / 60
    return mutableStateOf(value = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds))
}

fun calculateResinToAdd(oldTime: Long, currentTime: Long, resin: String): String {
    val timeDiff = currentTime - oldTime

    val resinToAdd = (timeDiff / (8L * 60L * 1000L)).toInt()

    return (resin.toInt() + resinToAdd).toString()
}