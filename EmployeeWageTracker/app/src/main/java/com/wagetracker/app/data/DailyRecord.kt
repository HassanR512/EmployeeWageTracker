package com.wagetracker.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_records",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = ["id"],
        childColumns = ["employeeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("employeeId")]
)
data class DailyRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val date: Long,        // epoch millis, normalized to start of day
    val workUnits: Double, // e.g. 1.0 = one full day, 0.5 = half day
    val rate: Double,      // rate per unit/day
    val earned: Double,    // workUnits * rate, stored for fast summing
    val notes: String = ""
)
