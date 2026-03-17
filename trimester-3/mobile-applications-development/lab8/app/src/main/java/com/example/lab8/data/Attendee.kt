package com.example.lab8.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendees")
data class Attendee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val phoneNumber: String
)
