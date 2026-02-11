package com.example.genshin_original_resin_counter.util

interface MessagesInterface {
    companion object{
        val NOTIFICATION_FULL: String
        get() = "Your resins are full"

        val NOTIFICATION_ALMOST_FULL: String
        get() = "Your resins are almost full"

        val NOTIFICATION_HEADER: String
        get() = "Original Resin Counter"
    }
}
