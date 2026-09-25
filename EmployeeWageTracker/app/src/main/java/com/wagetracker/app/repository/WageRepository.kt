package com.wagetracker.app.repository

import com.wagetracker.app.data.DailyRecord
import com.wagetracker.app.data.DailyRecordDao
import com.wagetracker.app.data.Employee
import com.wagetracker.app.data.EmployeeDao
import com.wagetracker.app.data.Payment
import com.wagetracker.app.data.PaymentDao
import kotlinx.coroutines.flow.Flow

class WageRepository(
    private val employeeDao: EmployeeDao,
    private val dailyRecordDao: DailyRecordDao,
    private val paymentDao: PaymentDao
) {
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun addEmployee(name: String): Long = employeeDao.insert(Employee(name = name))
    suspend fun updateEmployee(employee: Employee) = employeeDao.update(employee)
    suspend fun deleteEmployee(employee: Employee) = employeeDao.delete(employee)

    fun getRecords(employeeId: Long): Flow<List<DailyRecord>> =
        dailyRecordDao.getRecordsForEmployee(employeeId)

    suspend fun addRecord(record: DailyRecord): Long = dailyRecordDao.insert(record)
    suspend fun updateRecord(record: DailyRecord) = dailyRecordDao.update(record)
    suspend fun deleteRecord(record: DailyRecord) = dailyRecordDao.delete(record)

    fun getPayments(employeeId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForEmployee(employeeId)

    suspend fun addPayment(payment: Payment): Long = paymentDao.insert(payment)
    suspend fun updatePayment(payment: Payment) = paymentDao.update(payment)
    suspend fun deletePayment(payment: Payment) = paymentDao.delete(payment)
}
