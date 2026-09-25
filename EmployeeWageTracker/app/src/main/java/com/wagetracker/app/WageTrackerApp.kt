package com.wagetracker.app

import android.app.Application
import com.wagetracker.app.data.AppDatabase
import com.wagetracker.app.repository.WageRepository

class WageTrackerApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: WageRepository by lazy {
        WageRepository(database.employeeDao(), database.dailyRecordDao(), database.paymentDao())
    }
}
