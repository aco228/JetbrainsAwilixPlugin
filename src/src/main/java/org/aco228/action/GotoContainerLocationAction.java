package org.aco228.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.aco228.symbols.ContainerSymbolExtraction;
import org.aco228.symbols.models.RegistrationModel;
import org.jetbrains.annotations.NotNull;

public class GotoContainerLocationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
            final Project currentProject = e.getRequiredData(CommonDataKeys.PROJECT);
            final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
            final VirtualFile currentFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
            final VirtualFile srcFile = GoToHelper.getSrcRoot(currentFile);
            final String selectedText = GoToHelper.extractSelectedText(editor.getSelectionModel().getSelectedText());

        try {
            if (selectedText == null || selectedText.isEmpty()) {
                GoToHelper.notifyError(currentProject, "Nothing is selected in editor");
                return;
            }

            if (srcFile == null){
                GoToHelper.notifyError(currentProject, "Could not find `src` folder in project");
                return;
            }

            final VirtualFile containerFile = srcFile.findChild("container.js");
            if (containerFile == null) {
                GoToHelper.notifyError(currentProject, "Could not find `container.js`");
                return;
            }

            ContainerSymbolExtraction symbolExtraction = new ContainerSymbolExtraction(containerFile.getPath());
            if (symbolExtraction.hasError()) {
                GoToHelper.notifyError(currentProject, "Symbol extractor had an error");
                return;
            }

            RegistrationModel registrationModel = symbolExtraction.getRegistration(selectedText);
            if(registrationModel == null) {
                GoToHelper.notifyError(currentProject, "Could not find symbol = " + selectedText);
                return;
            }

            FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, containerFile, registrationModel.lineNumberInContainer, 0), true);
        } catch (Exception ex) {
            GoToHelper.notifyError(currentProject, "EXCEPTION!! Shit happened. " + ex.toString());
        }
    }
}
