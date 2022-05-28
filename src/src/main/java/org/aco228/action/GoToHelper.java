package org.aco228.action;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;

public class GoToHelper {

    public static VirtualFile getSrcRoot(VirtualFile currentFile){
        int maxRootFile = 50;
        for (var i = 0; i < maxRootFile; i++) {
            if (currentFile == null) {
                break;
            }

            if (currentFile.isDirectory() && currentFile.getName().equals("src")) {
                return currentFile;
            }

            currentFile = currentFile.getParent();
        }
        return null;
    }

    public static String extractSelectedText(String input){
        if (input == null || input.isEmpty()){
            return "";
        }
        input = input.trim();
        if(input.startsWith("this.")){
            input = input.replaceAll("this.", "");
        }
        if (input.startsWith("_")){
            input =  input.replaceAll("_", "");
        }
        return input;
    }

    public static void notifyError(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("NotificationGroup")
                .createNotification(content, NotificationType.ERROR)
                .notify(project);
    }
}
