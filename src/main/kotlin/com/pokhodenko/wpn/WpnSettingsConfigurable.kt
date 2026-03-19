package com.pokhodenko.wpn

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class WpnSettingsConfigurable : Configurable {

    private var ignoreFilesOutsideLibraryRootCheckBox: JBCheckBox? = null

    override fun getDisplayName(): String = "Workspace Package Navigator"

    override fun createComponent(): JComponent {
        ignoreFilesOutsideLibraryRootCheckBox = JBCheckBox("Ignore files outside library root")
        return FormBuilder.createFormBuilder()
            .addComponent(ignoreFilesOutsideLibraryRootCheckBox!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        return ignoreFilesOutsideLibraryRootCheckBox?.isSelected != WpnSettings.getInstance().ignoreFilesOutsideLibraryRoot
    }

    override fun apply() {
        WpnSettings.getInstance().ignoreFilesOutsideLibraryRoot =
            ignoreFilesOutsideLibraryRootCheckBox?.isSelected ?: false
    }

    override fun reset() {
        ignoreFilesOutsideLibraryRootCheckBox?.isSelected =
            WpnSettings.getInstance().ignoreFilesOutsideLibraryRoot
    }

    override fun disposeUIResources() {
        ignoreFilesOutsideLibraryRootCheckBox = null
    }
}
