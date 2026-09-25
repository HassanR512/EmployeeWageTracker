package com.wagetracker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wagetracker.app.ui.theme.ErrorRed
import com.wagetracker.app.ui.theme.SuccessGreen
import com.wagetracker.app.viewmodel.MonthlySummary

@Composable
fun SummaryCard(monthLabel: String, summary: MonthlySummary) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = "$monthLabel Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            SummaryRow("Days recorded", "${formatCurrency(summary.totalDaysWorked)}", MaterialTheme.colorScheme.onPrimary)
            SummaryRow("Entries", "${summary.totalRecordsCount}", MaterialTheme.colorScheme.onPrimary)
            SummaryRow("Total earned", "Rs. ${formatCurrency(summary.totalEarned)}", MaterialTheme.colorScheme.onPrimary)
            SummaryRow("Total paid", "Rs. ${formatCurrency(summary.totalPaid)}", MaterialTheme.colorScheme.onPrimary)
            SummaryRow(
                label = if (summary.remaining >= 0) "Remaining balance" else "Overpaid",
                value = "Rs. ${formatCurrency(kotlin.math.abs(summary.remaining))}",
                color = if (summary.remaining > 0) Color(0xFFFFE9A8) else Color(0xFFC8F5D6),
                bold = true
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        Text(text = label, color = color, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            color = color,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
