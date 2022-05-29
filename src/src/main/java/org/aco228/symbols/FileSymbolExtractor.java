package org.aco228.symbols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileSymbolExtractor {
    private final String _filePath;
    private final int _lineNumber;
    private final int _columnNumber;

    private String _line = "";
    public String _symbolWord = "";
    public String _functionWord = "";
    private boolean _hasError = false;

    public String getLine() { return _line; }
    public String getSymbolWord() { return _symbolWord;}
    public String getFunctionWord(){ return _functionWord; }
    public boolean hasError() { return _hasError; }

    public FileSymbolExtractor(String filePath, int lineNumber, int columnNumber){
        _filePath = filePath;
        _lineNumber = lineNumber;
        _columnNumber = columnNumber;

        readLine();
    }

    private void readLine() {
        try (Stream<String> lines = Files.lines(Paths.get(_filePath))) {
            processLine(lines.skip(_lineNumber).findFirst().get());
        } catch (IOException e) {
            e.printStackTrace();
            _hasError = true;
        }
    }

    private void processLine(String line){
        if(_columnNumber >= line.length()) {
            _hasError = true;
            return;
        }

        int startIndex = GetBeginningOfLineWord(line);
        int endIndex = GetEndingOfWord(line);

        line = line.substring(startIndex, endIndex);
        if(line.startsWith("this."))
            line = line.substring("this.".length());

        _line = line;

        String[] split = _line.split("\\.");
        _symbolWord = split[0];
        if(split.length > 1) {
            _functionWord = split[1];
        }
    }

    private int GetBeginningOfLineWord(String line) {
        for(int i = _columnNumber -1; i >= 0; i --) {
            if(!isCharacterValidAt(i, line))
                return ++i;
        }
        return 0;
    }

    private int GetEndingOfWord(String line){
        for(var i = _columnNumber; i < line.length(); i++) {
            if(!isCharacterValidAt(i, line))
                return i;
        }
        return _columnNumber;
    }

    private boolean isCharacterValidAt(int position, String line){
        char character = line.charAt(position);
        return character == '.' || isValidChat(character);
    }

    private static boolean isValidChat(char c) {
        return c == '_' ||
                (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }
}

