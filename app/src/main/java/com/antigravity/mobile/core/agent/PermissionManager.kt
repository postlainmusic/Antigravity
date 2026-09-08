package com.antigravity.mobile.core.agent

import com.antigravity.mobile.domain.model.PermissionTier
import com.antigravity.mobile.domain.model.ToolDefinition

class PermissionManager {

    private var autoApproveModerate: Boolean = true

    fun setAutoApproveModerate(enabled: Boolean) {
        autoApproveModerate = enabled
    }

    /**
     * Determines whether a tool call requires explicit user confirmation.
     */
    fun requiresUserApproval(tool: ToolDefinition): Boolean {
        return when (tool.permissionTier) {
            PermissionTier.SAFE -> false
            PermissionTier.MODERATE -> !autoApproveModerate
            PermissionTier.DANGEROUS -> true
        }
    }
}
