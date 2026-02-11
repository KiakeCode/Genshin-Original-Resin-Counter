package com.example.genshin_original_resin_counter.util

fun validateInput(input: String, string: String): String =
    if (input.contains("^0".toRegex())) string.replace(
        regex = "^0".toRegex(), replacement = ""
    )
    else if (input.length == 3 && string.length > 3) input
    else if (string.isNotEmpty() && string.toInt() > 200) "200"
    else string

fun validateInputFromDataStore(input: String, string: String): String =
    if (input.length == 3 && string.length > 3) input
    else if (string.isNotEmpty() && string.toInt() > 200) "200"
    else string
