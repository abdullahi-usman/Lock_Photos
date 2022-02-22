package com.dahham.lockphotos.zenquotes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ZenQuote(@PrimaryKey(autoGenerate = true) val uid: Int = -1, val q: String, val a: String, val c: String, val h: String){
    var noHasBeenUsed = 0
}