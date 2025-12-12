package com.example.vitesse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class Candidate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: String,
    val expectedSalary: Double,
    val notes: String,
    val profilePhotoUrl: String? = null,
    val isFavorite: Boolean = false
)