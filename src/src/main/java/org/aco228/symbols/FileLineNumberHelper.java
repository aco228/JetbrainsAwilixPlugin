package org.aco228.symbols;

import org.aco228.symbols.models.NpmPackageFindResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileLineNumberHelper {

    public static int GetLineNumber(String filepath, String searchParam){
        final Pattern _searchPattern = Pattern.compile("( "+searchParam+")(?=\\()");
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            int lineNumber = 1;

            while (line != null) {
                ++lineNumber;
                line = br.readLine();

                Matcher match = _searchPattern.matcher(line);
                if (match.find()) {
                    return lineNumber;
                }
            }

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
