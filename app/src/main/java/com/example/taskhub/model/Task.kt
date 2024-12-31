package com.example.taskhub.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "tasks", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["catId"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val categoryId: Int,
    val isCompleted: Boolean = false
) : Parcelable

