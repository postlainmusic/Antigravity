package com.antigravity.mobile

import android.app.Application
import com.antigravity.mobile.core.di.AppContainer
import com.antigravity.mobile.core.di.DefaultAppContainer

class AntigravityApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        val workspaceDir = filesDir.resolve("workspace").apply { if (!exists()) mkdirs() }
        container = DefaultAppContainer(workspaceDir)
    }
}
