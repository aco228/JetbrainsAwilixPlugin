package org.aco228.symbols;

import org.aco228.symbols.models.RegistrationModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContainerSymbolExtraction {
    public static boolean _debugPrintFoundSymbols = false;
    public static boolean _debugPrintFoundRegistrations = false;

    private final String _filepath;
    private boolean _hasError = false;
    private final Pattern _requirePattern = Pattern.compile("'.+?'");
    private final Pattern _awilixRegisterPattern = Pattern.compile("\\(.+?\\)");
    private Map<String, String> _symbolRelativeLocationMap = new HashMap<String, String>();
    private Map<String, RegistrationModel> _registrationSymbolMap = new HashMap<>();


    public ContainerSymbolExtraction(String filePath) {
        _filepath = filePath;
        readFileContent();
    }


    public boolean hasError() { return _hasError; }

    public String getSymbolRequirePath(String symbolName) {
        if(!_symbolRelativeLocationMap.containsKey(symbolName)) {
            return "";
        }
        return _symbolRelativeLocationMap.get(symbolName);
    }

    public RegistrationModel getRegistration(String registrationName){
        if(!_registrationSymbolMap.containsKey(registrationName)) {

            // in case that this is private variable which starts with '_'
            if(registrationName.startsWith("_")
                    && registrationName.length() > 2
                    && _registrationSymbolMap.containsKey(registrationName.substring(1))){
                return _registrationSymbolMap.get(registrationName.substring(1));
            }

            return null;
        }


        return _registrationSymbolMap.get(registrationName);
    }

    private void readFileContent() {
        try (BufferedReader br = new BufferedReader(new FileReader(_filepath))) {
            String line = br.readLine();
            int lineNumber = 0;

            while (line != null) {
                ++lineNumber;
                line = br.readLine();

                if (!IsValid(line)) {
                    continue;
                }

                line = line.trim();

                if (line.startsWith("const ") || line.contains("require(") || _readRegularLineDoublelineMode) {
                    readRegularLine(line);
                } else if(line.contains("awilix.")) {
                    readRegistrationLine(line, lineNumber);
                }
            }
            //String everything = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            _hasError = true;
        }
    }

    /*
    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
        READ SYMBOLS
     */

    private String _readRegularLineSymbolName = "";
    private boolean _readRegularLineDoublelineMode = false;

    private void readRegularLine(String line){
        if(!IsValid(line)){
            return;
        }

        line = line.trim();

        if (_readRegularLineDoublelineMode && !_readRegularLineSymbolName.isEmpty()){
            readRegularSymbolValueName(_readRegularLineSymbolName, line);
            _readRegularLineSymbolName = "";
            _readRegularLineDoublelineMode = false;
            return;
        }

        if(!line.startsWith("const")) {
            return;
        }
        line = line.replaceAll("const ", "");

        String[] split = line.split("=");
        String symbolName = getSymbolName(split[0]);
        if (split.length != 2 || split[1].trim().equals("")) {
            _readRegularLineDoublelineMode = true;
            _readRegularLineSymbolName = symbolName;
            return;
        }

        readRegularSymbolValueName(symbolName, split[1]);
    }

    private void readRegularSymbolValueName(String symbolName, String input){
        String symbolValue = getRequirePath(input.trim());
        if(!IsValid(symbolValue)) {
            return;
        }

        if(_symbolRelativeLocationMap.containsKey(symbolName)) {
            return;
        }

        _symbolRelativeLocationMap.put(symbolName, symbolValue);

        if(_debugPrintFoundSymbols){
            System.out.println(symbolName + " === " + symbolValue);
        }
    }

    private String getSymbolName(String input){
        if (!IsValid(input)){
            return "";
        }

        input = input.trim();

        if(!input.contains("{")){
            return input;
        }

        input = input
                .replaceAll("\\{", "")
                .replaceAll("}", "");

        String[] split = input.split(":");
        if(split.length == 1) {
            return split[0].trim();
        }
        return split[1].trim();
    }

    private String getRequirePath(String input){
        if(!IsValid(input)){
            return "";
        }

        Matcher match = _requirePattern.matcher(input);
        return match.find() ? match.group(0).replaceAll("'", "") : "";
    }

    /*
    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
        READ REGISTRATIONS
     */

    private void readRegistrationLine(String line, int lineNumber){
        line = line.replaceAll(",", "").trim();
        String[] split = line.split(":");

        if (split.length != 2) {
            return;
        }

        if(!split[1].contains("awilix")) {
            return;
        }

        String registrationName = split[0].trim();
        if (_registrationSymbolMap.containsKey(registrationName)) {
            return;
        }

        Matcher match = _awilixRegisterPattern.matcher(split[1].trim());
        if (!match.find()) {
            return;
        }

        String registrationValue = match.group(0).trim()
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\.")[0].trim();

        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.registrationName = registrationName;
        registrationModel.symbolName = registrationValue;
        registrationModel.relativeLocation = _symbolRelativeLocationMap.get(registrationValue);
        registrationModel.lineNumberInContainer = lineNumber;

        _registrationSymbolMap.put(registrationName, registrationModel);

        if(_debugPrintFoundRegistrations){
            System.out.println(lineNumber +  ":: " +registrationName + " ////// " + registrationValue + " //// " + split[1]);
        }
    }


    /*
    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
        DEBUG FUNCTIONS
     */

    public void debugPrintMissingRegistrationBasedOnSymbols(){
        System.out.println("");
        System.out.println("debugPrintMissingRegistrationBasedOnSymbols");
        System.out.println("");

        for (Map.Entry<String,String> entry : _symbolRelativeLocationMap.entrySet()){
            boolean found = false;
            for (Map.Entry<String,RegistrationModel> registrationModelEntry : _registrationSymbolMap.entrySet()) {
                if(registrationModelEntry.getValue().symbolName.equals(entry.getKey())){
                    found = true;
                    break;
                }
            }
            if(!found) {
                System.out.println("missing registration for symbol=" + entry.getKey());
            }
        }
    }

    public void debugPrintMissingSymbolsBasedOnRegistrations(){
        System.out.println("");
        System.out.println("debugPrintMissingSymbolsBasedOnRegistrations");
        System.out.println("");

        for (Map.Entry<String,RegistrationModel> entry : _registrationSymbolMap.entrySet()){
            boolean found = false;
            for (Map.Entry<String,String> symbolEntry : _symbolRelativeLocationMap.entrySet()){
                if(symbolEntry.getKey().equals(entry.getValue().symbolName)){
                    found = true;
                    if(!entry.getValue().hasRelativeLocation()){
                        System.out.println("missing registration (relativeLocation) for registration=" + entry.getKey());
                    }
                    break;
                }
            }
            if(!found) {
                System.out.println("missing registration for registration=" + entry.getKey());
            }
        }
    }


    /*
    +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
        STATICS
     */

    private static boolean IsValid(String input){
        return input != null && !input.isEmpty() && !input.isBlank();
    }

}
