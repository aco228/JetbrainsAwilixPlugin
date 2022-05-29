package org.aco228.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.aco228.symbols.ContainerSymbolExtraction;
import org.aco228.symbols.FileLineNumberHelper;
import org.aco228.symbols.FileSymbolExtractor;
import org.aco228.symbols.NpmPackageFunctionExtractor;
import org.aco228.symbols.models.NpmPackageFindResult;
import org.aco228.symbols.models.RegistrationModel;
import org.jetbrains.annotations.NotNull;

public class GotoServiceDeclarationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project currentProject = e.getRequiredData(CommonDataKeys.PROJECT);
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final VirtualFile currentFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        final VirtualFile srcFile = GoToHelper.getSrcRoot(currentFile);

        try{
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

            final String selectedText = GoToHelper.extractSelectedText(editor.getSelectionModel().getSelectedText());

            if (selectedText != null && !selectedText.isEmpty()) {
                GotoFileBasedOnSelection(currentProject, containerFile, srcFile, symbolExtraction, selectedText);
                return;
            }

            final VisualPosition visualPosition = editor.getSelectionModel().getLeadSelectionPosition();
            if(visualPosition == null) {
                GoToHelper.notifyError(currentProject, "VisualPosition is null for some reason");
                return;
            }

            GotoFileBasedOnPosition(currentProject, currentFile, containerFile, srcFile, symbolExtraction, visualPosition);
        } catch (Exception ex){
            GoToHelper.notifyError(currentProject, "EXCEPTION!! Shit happened. " + ex.toString());
        }
    }

    private void GotoFileBasedOnPosition(
            Project currentProject,
            VirtualFile currentFile,
            VirtualFile containerFile,
            VirtualFile srcFile,
            ContainerSymbolExtraction containerSymbolExtraction,
            VisualPosition visualPosition
    ){
        FileSymbolExtractor fileSymbolExtractor = new FileSymbolExtractor(currentFile.getPath(), visualPosition.getLine(), visualPosition.getColumn());
        if (fileSymbolExtractor.hasError()) {
            GoToHelper.notifyError(currentProject, "File symbol extractor had an error");
            return;
        }

        RegistrationModel registrationModel = containerSymbolExtraction.getRegistration(fileSymbolExtractor.getSymbolWord());
        if (registrationModel == null){
            GoToHelper.notifyError(currentProject, "Could not find symbol = " + fileSymbolExtractor.getSymbolWord());
            return;
        }

        if (!registrationModel.hasRelativeLocation()){
            FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, containerFile, registrationModel.lineNumberInContainer, 0), true);
            return;
        }

        if (registrationModel.isNpmPackage()
                && !fileSymbolExtractor.getFunctionWord().isEmpty()
                && GoToHelper.checkIfNpmPackageExists(srcFile, registrationModel.relativeLocation)){
            NpmPackageFunctionExtractor npmPackageFunctionExtractor = new NpmPackageFunctionExtractor(
                    srcFile.getPath(),
                    registrationModel.relativeLocation,
                    fileSymbolExtractor.getSymbolWord(),
                    fileSymbolExtractor.getFunctionWord());

            NpmPackageFindResult npmPackageFindResult = npmPackageFunctionExtractor.getResult();
            if (npmPackageFindResult != null) {
                VirtualFile npmFile = srcFile.findFileByRelativePath(npmPackageFindResult.location);
                if(npmFile != null){
                    FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, npmFile, npmPackageFindResult.lineNumber, 0), true);
                    return;
                }
            }
        }

        VirtualFile virtualFile = getVirtualFile(srcFile, registrationModel);
        if(virtualFile == null) {
            GoToHelper.notifyError(currentProject, "Could not find anything at location, so you will be transported to `container.js`. Location = " + registrationModel.relativeLocation);
            FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, containerFile, registrationModel.lineNumberInContainer, 0), true);
            return;
        }

        if (fileSymbolExtractor.getFunctionWord().isEmpty()) {
            FileEditorManager.getInstance(currentProject).openTextEditor( new OpenFileDescriptor(currentProject, virtualFile), true);
            return;
        }

        int lineNumber = FileLineNumberHelper.GetLineNumber(virtualFile.getPath(), " " + fileSymbolExtractor.getFunctionWord());
        if (lineNumber == -1) {
            FileEditorManager.getInstance(currentProject).openTextEditor( new OpenFileDescriptor(currentProject, virtualFile), true);
            return;
        }

        FileEditorManager.getInstance(currentProject).openTextEditor( new OpenFileDescriptor(currentProject, virtualFile, lineNumber, 0), true);
    }

    private void GotoFileBasedOnSelection(
            Project currentProject,
            VirtualFile containerFile,
            VirtualFile srcFile,
            ContainerSymbolExtraction symbolExtraction,
            String selectedText
    ){
        RegistrationModel registrationModel = symbolExtraction.getRegistration(selectedText);
        if (registrationModel == null) {
            GoToHelper.notifyError(currentProject, "Could not find symbol = " + selectedText);
            return;
        }

        if(!registrationModel.hasRelativeLocation()) {
            FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, containerFile, registrationModel.lineNumberInContainer, 0), true);
            return;
        }

        VirtualFile virtualFile = getVirtualFile(srcFile, registrationModel);
        if(virtualFile == null) {
            GoToHelper.notifyError(currentProject, "Could not find anything at location, so you will be transported to `container.js`. Location = " + registrationModel.relativeLocation);
            FileEditorManager.getInstance(currentProject).openTextEditor(new OpenFileDescriptor(currentProject, containerFile, registrationModel.lineNumberInContainer, 0), true);
            return;
        }

        FileEditorManager.getInstance(currentProject).openTextEditor( new OpenFileDescriptor(currentProject, virtualFile), true);
    }

    private VirtualFile getVirtualFile(VirtualFile src, RegistrationModel registrationModel){
        if(!registrationModel.hasRelativeLocation()){
            return null;
        }

        VirtualFile virtualFile = src.findFileByRelativePath(registrationModel.relativeLocation);
        if (virtualFile != null && !virtualFile.isDirectory()) {
            return  virtualFile;
        }

        virtualFile = src.findFileByRelativePath(registrationModel.relativeLocation + ".js");
        if(virtualFile != null && !virtualFile.isDirectory()) {
            return virtualFile;
        }

        return src.findFileByRelativePath(registrationModel.relativeLocation + "/index.js");
    }

}
