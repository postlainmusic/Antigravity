package com.antigravity.mobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.domain.model.AutonomyLevel
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.EmeraldSuccess
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import com.antigravity.mobile.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    currentAutonomyLevel: AutonomyLevel = AutonomyLevel.LEVEL_4_BOUNDED,
    onAutonomyLevelChanged: (AutonomyLevel) -> Unit = {},
    onSaveApiKey: (providerId: String, apiKey: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var geminiKey by remember { mutableStateOf("") }
    var claudeKey by remember { mutableStateOf("") }
    var openaiKey by remember { mutableStateOf("") }
    var offlineMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "SETTINGS & GOVERNANCE",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Configure AI model keys, security sandboxes, and autonomy gates.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Section 1: Autonomy Level
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AUTONOMY LEVEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoGlow,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AutonomyLevel.entries.forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (level == currentAutonomyLevel) DarkSurfaceElevated else DarkSurface)
                                .clickable { onAutonomyLevelChanged(level) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = level.name.replace("_", " "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (level == currentAutonomyLevel) EmeraldSuccess else TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Credential Vault (KeyStore Backed)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HARDWARE-BACKED CREDENTIAL VAULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoGlow,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Encrypted at rest using Android KeyStore AES-256-GCM.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = geminiKey,
                        onValueChange = {
                            geminiKey = it
                            onSaveApiKey("gemini", it)
                        },
                        label = { Text("Google Gemini API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = claudeKey,
                        onValueChange = {
                            claudeKey = it
                            onSaveApiKey("claude", it)
                        },
                        label = { Text("Anthropic Claude API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = openaiKey,
                        onValueChange = {
                            openaiKey = it
                            onSaveApiKey("openai", it)
                        },
                        label = { Text("OpenAI API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Section 3: Offline / Privacy Policy
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Strict Offline / On-Device Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Forces execution via local Gemma 2B model only.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = offlineMode,
                        onCheckedChange = { offlineMode = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = IndigoGlow)
                    )
                }
            }
        }
    }
}
