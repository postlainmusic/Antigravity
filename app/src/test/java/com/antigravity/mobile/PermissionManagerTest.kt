package com.antigravity.mobile

import com.antigravity.mobile.core.agent.PermissionManager
import com.antigravity.mobile.domain.model.AutonomyLevel
import com.antigravity.mobile.domain.model.PermissionDecision
import com.antigravity.mobile.domain.model.PermissionTier
import com.antigravity.mobile.domain.model.ToolDefinition
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PermissionManagerTest {

    private lateinit var permissionManager: PermissionManager

    private val safeTool = ToolDefinition("read_file", "Read file", emptyList(), PermissionTier.SAFE)
    private val moderateTool = ToolDefinition("write_file", "Write file", emptyList(), PermissionTier.MODERATE)
    private val dangerousTool = ToolDefinition("run_command", "Run shell", emptyList(), PermissionTier.DANGEROUS)
    private val destructiveTool = ToolDefinition("wipe_disk", "Destructive", emptyList(), PermissionTier.DESTRUCTIVE)

    @Before
    fun setUp() {
        permissionManager = PermissionManager()
    }

    @Test
    fun testRequiresUserApprovalIsExhaustive() {
        assertFalse(permissionManager.requiresUserApproval(safeTool))
        assertFalse(permissionManager.requiresUserApproval(moderateTool)) // Default auto-approve
        permissionManager.setAutoApproveModerate(false)
        assertTrue(permissionManager.requiresUserApproval(moderateTool))
        assertTrue(permissionManager.requiresUserApproval(dangerousTool))
        assertTrue(permissionManager.requiresUserApproval(destructiveTool))
    }

    @Test
    fun testAutonomyLevel1ReadOnlyBlocksWrites() {
        val safeDecision = permissionManager.evaluatePermission(safeTool, AutonomyLevel.LEVEL_1_READ_ONLY, "c1", "{}")
        assertTrue(safeDecision is PermissionDecision.Approved)

        val writeDecision = permissionManager.evaluatePermission(moderateTool, AutonomyLevel.LEVEL_1_READ_ONLY, "c2", "{}")
        assertTrue(writeDecision is PermissionDecision.Blocked)
    }

    @Test
    fun testAutonomyLevel2SuggestRequiresApprovalForModifications() {
        val decision = permissionManager.evaluatePermission(moderateTool, AutonomyLevel.LEVEL_2_SUGGEST, "c3", "{}")
        assertTrue(decision is PermissionDecision.RequiresApproval)
    }

    @Test
    fun testAutonomyLevel4BoundedAllowsNormalOpsAndGatesDestructive() {
        val moderateDecision = permissionManager.evaluatePermission(moderateTool, AutonomyLevel.LEVEL_4_BOUNDED, "c4", "{}")
        assertTrue(moderateDecision is PermissionDecision.Approved)

        val dangerousDecision = permissionManager.evaluatePermission(dangerousTool, AutonomyLevel.LEVEL_4_BOUNDED, "c5", "{}")
        assertTrue(dangerousDecision is PermissionDecision.Approved)

        val destructiveDecision = permissionManager.evaluatePermission(destructiveTool, AutonomyLevel.LEVEL_4_BOUNDED, "c6", "{}")
        assertTrue(destructiveDecision is PermissionDecision.RequiresApproval)
    }

    @Test
    fun testAutonomyLevel5AutonomousBlocksDestructiveInvariant() {
        val destructiveDecision = permissionManager.evaluatePermission(destructiveTool, AutonomyLevel.LEVEL_5_AUTONOMOUS, "c7", "{}")
        assertTrue(destructiveDecision is PermissionDecision.Blocked)
    }
}
