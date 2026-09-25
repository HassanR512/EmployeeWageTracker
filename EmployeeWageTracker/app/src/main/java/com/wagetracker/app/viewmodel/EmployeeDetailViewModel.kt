package com.wagetracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wagetracker.app.data.DailyRecord
import com.wagetracker.app.data.Payment
import com.wagetracker.app.repository.WageRepository
import com.wagetracker.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MonthlySummary(
    val totalDaysWorked: Double,
    val totalRecordsCount: Int,
    val totalEarned: Double,
    val totalPaid: Double,
    val remaining: Double
)

class EmployeeDetailViewModel(
    private val repository: WageRepository,
    val employeeId: Long
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtils.currentMonthKey())
    val selectedMonth: StateFlow<String> = _selectedMonth

    val allRecords: StateFlow<List<DailyRecord>> = repository.getRecords(employeeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Payment>> = repository.getPayments(employeeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRecords: StateFlow<List<DailyRecord>> = combine(allRecords, _selectedMonth) { records, month ->
        if (month == "ALL") records else records.filter { DateUtils.monthKeyOf(it.date) == month }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPayments: StateFlow<List<Payment>> = combine(allPayments, _selectedMonth) { payments, month ->
        if (month == "ALL") payments else payments.filter { DateUtils.monthKeyOf(it.date) == month }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableMonths: StateFlow<List<String>> = allRecords.map { records ->
        records.map { DateUtils.monthKeyOf(it.date) }.distinct().sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<MonthlySummary> = combine(filteredRecords, filteredPayments) { records, payments ->
        val totalDays = records.sumOf { it.workUnits }
        val totalEarned = records.sumOf { it.earned }
        val totalPaid = payments.sumOf { it.amount }
        MonthlySummary(
            totalDaysWorked = totalDays,
            totalRecordsCount = records.size,
            totalEarned = totalEarned,
            totalPaid = totalPaid,
            remaining = totalEarned - totalPaid
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthlySummary(0.0, 0, 0.0, 0.0, 0.0)
    )

    fun setMonth(month: String) {
        _selectedMonth.value = month
    }

    fun addRecord(date: Long, workUnits: Double, rate: Double, notes: String) {
        viewModelScope.launch {
            repository.addRecord(
                DailyRecord(
                    employeeId = employeeId,
                    date = date,
                    workUnits = workUnits,
                    rate = rate,
                    earned = workUnits * rate,
                    notes = notes
                )
            )
        }
    }

    fun updateRecord(record: DailyRecord, date: Long, workUnits: Double, rate: Double, notes: String) {
        viewModelScope.launch {
            repository.updateRecord(
                record.copy(date = date, workUnits = workUnits, rate = rate, earned = workUnits * rate, notes = notes)
            )
        }
    }

    fun deleteRecord(record: DailyRecord) {
        viewModelScope.launch { repository.deleteRecord(record) }
    }

    fun addPayment(date: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.addPayment(Payment(employeeId = employeeId, date = date, amount = amount, notes = notes))
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch { repository.deletePayment(payment) }
    }

    class Factory(
        private val repository: WageRepository,
        private val employeeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EmployeeDetailViewModel::class.java)) {
                return EmployeeDetailViewModel(repository, employeeId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
