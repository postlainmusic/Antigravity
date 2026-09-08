package com.antigravity.mobile.ui.agent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.domain.model.AgentStatus
import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.MessageRole
import com.antigravity.mobile.domain.model.PlanStatus
import com.antigravity.mobile.domain.model.PlanStep
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
fun AgentChatPanel(
    messages: List<ChatMessage>,
    planSteps: List<PlanStep>,
    agentStatus: AgentStatus,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .imePadding()
    ) {
        // Plan Overview Card if active
        if (planSteps.isNotEmpty()) {
            PlanOverviewCard(
                planSteps = planSteps,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Chat History Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            reverseLayout = false
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageBubble(message = msg)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Bottom Prompt Input Bar
        PromptInputBar(
            value = promptInput,
            onValueChange = { promptInput = it },
            isThinking = agentStatus == AgentStatus.THINKING || agentStatus == AgentStatus.EXECUTING_TOOL,
            onSend = {
                if (promptInput.isNotBlank()) {
                    onSendMessage(promptInput)
                    promptInput = ""
                }
            }
        )
    }
}

@Composable
fun PlanOverviewCard(
    planSteps: List<PlanStep>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = IndigoGlow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE EXECUTION PLAN",
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoGlow,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            planSteps.forEach { step ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    when (step.status) {
                        PlanStatus.COMPLETED -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        PlanStatus.IN_PROGRESS -> CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = IndigoGlow,
                            modifier = Modifier.size(12.dp)
                        )
                        else -> Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TextMuted)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (step.status == PlanStatus.COMPLETED) TextSecondary else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val bubbleColor = if (isUser) DarkSurfaceElevated else DarkSurface
    val borderColor = if (isUser) IndigoGlow.copy(alpha = 0.5f) else DarkBorder

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(IndigoGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Agent",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(14.dp))
                .background(bubbleColor)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = if (isUser) "You" else "Antigravity Agent",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) IndigoGlow else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.content.ifEmpty { if (message.isStreaming) "Thinking and inspecting project..." else "" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PromptInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    isThinking: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, DarkBorder)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = if (isThinking) "Agent is executing task..." else "Ask agent: e.g. 'Build landing page for my app'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                enabled = !isThinking,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoGlow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isThinking,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank() && !isThinking) IndigoGlow else DarkSurfaceElevated)
            ) {
                if (isThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = TextMuted,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) Color.White else TextMuted
                    )
                }
            }
        }
    }
}
