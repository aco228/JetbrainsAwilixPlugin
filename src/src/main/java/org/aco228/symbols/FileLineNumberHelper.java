package org.aco228.symbols;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileLineNumberHelper {

    public static int GetLineNumber(String filepath, String searchParam){
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            int lineNumber = 1;

            while (line != null) {
                ++lineNumber;
                line = br.readLine();
                if (line.contains(searchParam)) {
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
