package com.wagetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wagetracker.app.data.DailyRecord
import com.wagetracker.app.util.DateUtils
import java.util.Locale

@Composable
fun AddEditRecordDialog(
    existingRecord: DailyRecord? = null,
    defaultRate: Double = 0.0,
    onDismiss: () -> Unit,
    onConfirm: (date: Long, workUnits: Double, rate: Double, notes: String) -> Unit
) {
    var date by remember { mutableStateOf(existingRecord?.date ?: DateUtils.startOfDay(System.currentTimeMillis())) }
    var workUnitsText by remember { mutableStateOf(existingRecord?.workUnits?.let { formatNumber(it) } ?: "1") }
    var rateText by remember { mutableStateOf(existingRecord?.rate?.let { formatNumber(it) } ?: (if (defaultRate > 0) formatNumber(defaultRate) else "")) }
    var notes by remember { mutableStateOf(existingRecord?.notes ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    val workUnits = workUnitsText.toDoubleOrNull()
    val rate = rateText.toDoubleOrNull()
    val earned = if (workUnits != null && rate != null) workUnits * rate else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingRecord == null) "Add Daily Record" else "Edit Daily Record") },
        text = {
            Column {
                DatePickerField(label = "Date", selectedDateMillis = date, onDateSelected = { date = it })

                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = workUnitsText,
                        onValueChange = { workUnitsText = it; error = null },
                        label = { Text("Work (days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it; error = null },
                        label = { Text("Rate / day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Amount earned: ${if (earned != null) formatCurrency(earned) else "--"}",
                    fontWeight = FontWeight.Bold
                )

                error?.let {
                    Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (workUnits == null || workUnits <= 0.0) {
                    error = "Enter a valid work amount"
                } else if (rate == null || rate < 0.0) {
                    error = "Enter a valid rate"
                } else {
                    onConfirm(date, workUnits, rate, notes.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun formatNumber(value: Double): String {
    return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
}

fun formatCurrency(value: Double): String {
    return String.format(Locale.getDefault(), "%,.2f", value)
}
