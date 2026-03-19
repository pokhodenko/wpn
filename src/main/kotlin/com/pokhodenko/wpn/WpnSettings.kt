package com.pokhodenko.wpn

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(name = "WpnSettings", storages = [Storage("WpnSettings.xml")])
class WpnSettings : PersistentStateComponent<WpnSettings.State> {

    data class State(
        var ignoreFilesOutsideLibraryRoot: Boolean = false,
        var firstRunNotificationShown: Boolean = false,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var ignoreFilesOutsideLibraryRoot: Boolean
        get() = state.ignoreFilesOutsideLibraryRoot
        set(value) { state.ignoreFilesOutsideLibraryRoot = value }

    var firstRunNotificationShown: Boolean
        get() = state.firstRunNotificationShown
        set(value) { state.firstRunNotificationShown = value }

    companion object {
        fun getInstance(): WpnSettings = service()
    }
}
