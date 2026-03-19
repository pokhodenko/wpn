package com.pokhodenko.wpn

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.jetbrains.annotations.Nls
import java.util.LinkedHashSet

class SymlinksGotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor
    ): Array<PsiElement>? {


        if (sourceElement == null) {
            return null
        }

        val resolvedTargets = resolveTargets(sourceElement)
        if (resolvedTargets.isEmpty()) {
            return null
        }

        val remappedTargets = LinkedHashSet<PsiElement>()
        for (target in resolvedTargets) {
            val remapped = editor.project?.let { remapToRealFile(target, it) }
            remappedTargets.add(remapped ?: target)
        }

        if (remappedTargets.isEmpty()) {
            return null
        }

        return remappedTargets.toTypedArray()
    }

    override fun getActionText(context: DataContext): @Nls(capitalization = Nls.Capitalization.Title) String? {
        return super.getActionText(context)
    }

    private fun resolveTargets(sourceElement: PsiElement): List<PsiElement> {
        val result = ArrayList<PsiElement>()

        var reference = sourceElement.reference
        if (reference == null && sourceElement.parent != null) {
            reference = sourceElement.parent.reference
        }
        if (reference == null) {
            return result
        }

        if (reference is PsiPolyVariantReference) {
            val resolveResults: Array<ResolveResult> = reference.multiResolve(false)
            for (resolveResult in resolveResults) {
                val element = resolveResult.element
                if (element != null) {
                    result.add(element)
                }
            }
            return result
        }

        val resolved = reference.resolve()
        if (resolved != null) {
            result.add(resolved)
        }

        return result
    }

    private fun remapToRealFile(target: PsiElement, project: Project): PsiElement? {
        val originalPsiFile: PsiFile = target.containingFile ?: return target

        val originalVirtualFile = originalPsiFile.virtualFile
        if (originalVirtualFile == null || !originalVirtualFile.isInLocalFileSystem) {
            return target
        }

        if (WpnSettings.getInstance().ignoreFilesOutsideLibraryRoot) {
            val fileIndex = ProjectFileIndex.getInstance(project)
            if (!fileIndex.isInLibrary(originalVirtualFile)) {
                return target
            }
        }

        // VFS already resolved the canonical path during indexing — no fresh IO needed
        val realVirtualFile = originalVirtualFile.canonicalFile ?: return target
        if (realVirtualFile == originalVirtualFile) {
            return target
        }

        val realPsiFile = PsiManager.getInstance(project).findFile(realVirtualFile) ?: return target

        val textRange = target.textRange
        val startOffset = textRange?.startOffset ?: target.textOffset
        if (startOffset < 0 || startOffset > realPsiFile.textLength) {
            return realPsiFile
        }

        val mapped = realPsiFile.findElementAt(startOffset) ?: return realPsiFile
        return climbToReasonableTarget(mapped, target) ?: mapped
    }

    private fun climbToReasonableTarget(mapped: PsiElement, originalTarget: PsiElement): PsiElement? {
        val targetClass = originalTarget.javaClass

        var current: PsiElement? = mapped
        while (current != null) {
            if (targetClass.isInstance(current)) {
                return current
            }

            if (sameRange(current, originalTarget)) {
                return current
            }

            current = current.parent
        }

        return mapped
    }

    private fun sameRange(left: PsiElement, right: PsiElement): Boolean {
        val leftRange = left.textRange
        val rightRange = right.textRange
        return leftRange != null &&
                rightRange != null &&
                leftRange.startOffset == rightRange.startOffset &&
                leftRange.endOffset == rightRange.endOffset
    }
}