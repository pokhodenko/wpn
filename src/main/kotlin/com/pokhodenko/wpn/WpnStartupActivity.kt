package com.pokhodenko.wpn

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class WpnStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val settings = WpnSettings.getInstance()
        if (settings.firstRunNotificationShown) return
        settings.firstRunNotificationShown = true

        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("com.pokhodenko.wpn")
            .createNotification(
                "Workspace Package Navigator",
                "For symlink navigation to work correctly, please invalidate caches and restart the IDE.",
                NotificationType.INFORMATION,
            )

        notification.addAction(object : NotificationAction("Invalidate Caches & Restart") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                notification.expire()
                ActionManager.getInstance().getAction("InvalidateCaches")?.actionPerformed(e)
            }
        })

        notification.notify(project)
    }
}
