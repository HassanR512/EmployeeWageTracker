package com.wagetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_records WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getRecordsForEmployee(employeeId: Long): Flow<List<DailyRecord>>

    @Insert
    suspend fun insert(record: DailyRecord): Long

    @Update
    suspend fun update(record: DailyRecord)

    @Delete
    suspend fun delete(record: DailyRecord)
}
