package org.aco228.symbols;

public class RegistrationModel {
    public String registrationName = "";
    public String symbolName = "";
    public String relativeLocation = "";
    public int lineNumberInContainer = -1;

    public boolean hasRelativeLocation() {
        return relativeLocation != null && !relativeLocation.isEmpty() && !relativeLocation.isBlank();
    }

    public String getLastArgumentFromPath() {
        if(!hasRelativeLocation()){
            return "";
        }

        String[] split = relativeLocation.split("/");
        return split[split.length - 1];
    }

    public void debugPrint(){
        System.out.println("RegistratioName = " + registrationName);
        System.out.println("SymbolName = " + symbolName);
        System.out.println("Location = " + relativeLocation);
        System.out.println("LineNumberInContainer = " + lineNumberInContainer);
    }
}