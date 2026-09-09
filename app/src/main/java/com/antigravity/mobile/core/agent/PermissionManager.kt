package com.antigravity.mobile.core.agent

import com.antigravity.mobile.domain.model.AutonomyLevel
import com.antigravity.mobile.domain.model.PendingAction
import com.antigravity.mobile.domain.model.PermissionDecision
import com.antigravity.mobile.domain.model.PermissionTier
import com.antigravity.mobile.domain.model.ToolDefinition

class PermissionManager {

    private var autoApproveModerate: Boolean = true

    fun setAutoApproveModerate(enabled: Boolean) {
        autoApproveModerate = enabled
    }

    /**
     * Determines whether a tool call requires explicit user confirmation.
     * Guaranteed to be exhaustive across all PermissionTier levels.
     */
    fun requiresUserApproval(tool: ToolDefinition): Boolean {
        return when (tool.permissionTier) {
            PermissionTier.SAFE -> false
            PermissionTier.MODERATE -> !autoApproveModerate
            PermissionTier.DANGEROUS -> true
            PermissionTier.DESTRUCTIVE -> true
        }
    }

    /**
     * Evaluates permission for a tool call against the active autonomy level.
     * Enforces Layer 5 security invariants deterministically.
     */
    fun evaluatePermission(
        tool: ToolDefinition,
        autonomyLevel: AutonomyLevel,
        callId: String,
        argumentsJson: String
    ): PermissionDecision {
        return when (autonomyLevel) {
            AutonomyLevel.LEVEL_1_READ_ONLY -> {
                when (tool.permissionTier) {
                    PermissionTier.SAFE -> PermissionDecision.Approved
                    PermissionTier.MODERATE,
                    PermissionTier.DANGEROUS,
                    PermissionTier.DESTRUCTIVE -> PermissionDecision.Blocked("Autonomy Level 1 (Read Only) blocks all write or execution operations.")
                }
            }
            AutonomyLevel.LEVEL_2_SUGGEST -> {
                when (tool.permissionTier) {
                    PermissionTier.SAFE -> PermissionDecision.Approved
                    PermissionTier.MODERATE,
                    PermissionTier.DANGEROUS,
                    PermissionTier.DESTRUCTIVE -> PermissionDecision.RequiresApproval(
                        PendingAction(
                            actionId = callId,
                            title = "Approve ${tool.name}",
                            description = "Tool arguments: $argumentsJson",
                            permissionTier = tool.permissionTier.name
                        )
                    )
                }
            }
            AutonomyLevel.LEVEL_3_SUPERVISED -> {
                when (tool.permissionTier) {
                    PermissionTier.SAFE -> PermissionDecision.Approved
                    PermissionTier.MODERATE -> {
                        if (autoApproveModerate) PermissionDecision.Approved
                        else PermissionDecision.RequiresApproval(
                            PendingAction(callId, "Approve ${tool.name}", "Arguments: $argumentsJson", tool.permissionTier.name)
                        )
                    }
                    PermissionTier.DANGEROUS,
                    PermissionTier.DESTRUCTIVE -> PermissionDecision.RequiresApproval(
                        PendingAction(callId, "Approve ${tool.name}", "Arguments: $argumentsJson", tool.permissionTier.name)
                    )
                }
            }
            AutonomyLevel.LEVEL_4_BOUNDED -> {
                when (tool.permissionTier) {
                    PermissionTier.SAFE,
                    PermissionTier.MODERATE,
                    PermissionTier.DANGEROUS -> PermissionDecision.Approved
                    PermissionTier.DESTRUCTIVE -> PermissionDecision.RequiresApproval(
                        PendingAction(callId, "Approve Destructive Operation: ${tool.name}", "Arguments: $argumentsJson", tool.permissionTier.name)
                    )
                }
            }
            AutonomyLevel.LEVEL_5_AUTONOMOUS -> {
                when (tool.permissionTier) {
                    PermissionTier.SAFE,
                    PermissionTier.MODERATE,
                    PermissionTier.DANGEROUS -> PermissionDecision.Approved
                    PermissionTier.DESTRUCTIVE -> PermissionDecision.Blocked("Security Invariant: Destructive out-of-bounds operations are permanently prohibited.")
                }
            }
        }
    }
}
