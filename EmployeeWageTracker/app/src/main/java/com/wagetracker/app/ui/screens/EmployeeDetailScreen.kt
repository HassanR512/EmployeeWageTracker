package com.wagetracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wagetracker.app.data.DailyRecord
import com.wagetracker.app.data.Payment
import com.wagetracker.app.ui.components.AddEditRecordDialog
import com.wagetracker.app.ui.components.AddPaymentDialog
import com.wagetracker.app.ui.components.ConfirmDialog
import com.wagetracker.app.ui.components.RecordCard
import com.wagetracker.app.ui.components.SummaryCard
import com.wagetracker.app.ui.components.formatCurrency
import com.wagetracker.app.util.DateUtils
import com.wagetracker.app.viewmodel.EmployeeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employeeName: String,
    viewModel: EmployeeDetailViewModel,
    onBack: () -> Unit
) {
    val records by viewModel.filteredRecords.collectAsStateWithLifecycle()
    val payments by viewModel.filteredPayments.collectAsStateWithLifecycle()
    val availableMonths by viewModel.availableMonths.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    var tabIndex by remember { mutableStateOf(0) }
    var showAddRecord by remember { mutableStateOf(false) }
    var showAddPayment by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<DailyRecord?>(null) }
    var recordToDelete by remember { mutableStateOf<DailyRecord?>(null) }
    var paymentToDelete by remember { mutableStateOf<Payment?>(null) }

    val lastRate = records.firstOrNull()?.rate ?: viewModel.allRecords.value.firstOrNull()?.rate ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(employeeName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabIndex == 0) {
                FloatingActionButton(onClick = { showAddRecord = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add daily record")
                }
            } else {
                FloatingActionButton(onClick = { showAddPayment = true }) {
                    Icon(Icons.Default.Payments, contentDescription = "Record payment")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            MonthDropdown(
                availableMonths = availableMonths,
                selectedMonth = selectedMonth,
                onMonthSelected = { viewModel.setMonth(it) }
            )

            SummaryCard(monthLabel = DateUtils.monthKeyToLabel(selectedMonth), summary = summary)

            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Daily Records") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Payments") })
            }

            if (tabIndex == 0) {
                if (records.isEmpty()) {
                    EmptyState(text = "No work records for this period.\nTap + to add one.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp, bottom = 96.dp)) {
                        items(records, key = { it.id }) { record ->
                            RecordCard(
                                record = record,
                                onEdit = { recordToEdit = record },
                                onDelete = { recordToDelete = record }
                            )
                        }
                    }
                }
            } else {
                if (payments.isEmpty()) {
                    EmptyState(text = "No payments recorded for this period.\nTap + to record one.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp, bottom = 96.dp)) {
                        items(payments, key = { it.id }) { payment ->
                            PaymentRow(payment = payment, onDelete = { paymentToDelete = payment })
                        }
                    }
                }
            }
        }
    }

    if (showAddRecord) {
        AddEditRecordDialog(
            defaultRate = lastRate,
            onDismiss = { showAddRecord = false },
            onConfirm = { date, workUnits, rate, notes ->
                viewModel.addRecord(date, workUnits, rate, notes)
                showAddRecord = false
            }
        )
    }

    recordToEdit?.let { rec ->
        AddEditRecordDialog(
            existingRecord = rec,
            onDismiss = { recordToEdit = null },
            onConfirm = { date, workUnits, rate, notes ->
                viewModel.updateRecord(rec, date, workUnits, rate, notes)
                recordToEdit = null
            }
        )
    }

    recordToDelete?.let { rec ->
        ConfirmDialog(
            title = "Delete this record?",
            message = "This will remove the record for ${DateUtils.formatDate(rec.date)}.",
            onDismiss = { recordToDelete = null },
            onConfirm = {
                viewModel.deleteRecord(rec)
                recordToDelete = null
            }
        )
    }

    if (showAddPayment) {
        AddPaymentDialog(
            onDismiss = { showAddPayment = false },
            onConfirm = { date, amount, notes ->
                viewModel.addPayment(date, amount, notes)
                showAddPayment = false
            }
        )
    }

    paymentToDelete?.let { pay ->
        ConfirmDialog(
            title = "Delete this payment?",
            message = "This will remove the payment of Rs. ${formatCurrency(pay.amount)} recorded on ${DateUtils.formatDate(pay.date)}.",
            onDismiss = { paymentToDelete = null },
            onConfirm = {
                viewModel.deletePayment(pay)
                paymentToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthDropdown(
    availableMonths: List<String>,
    selectedMonth: String,
    onMonthSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("ALL") + availableMonths.ifEmpty { listOf(DateUtils.currentMonthKey()) }.distinct()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = DateUtils.monthKeyToLabel(selectedMonth),
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter by month") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { monthKey ->
                DropdownMenuItem(
                    text = { Text(DateUtils.monthKeyToLabel(monthKey)) },
                    onClick = {
                        onMonthSelected(monthKey)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PaymentRow(payment: Payment, onDelete: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = DateUtils.formatDate(payment.date), fontWeight = FontWeight.SemiBold)
                if (payment.notes.isNotBlank()) {
                    Text(text = payment.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = "Rs. ${formatCurrency(payment.amount)}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete payment")
            }
        }
    }
}
