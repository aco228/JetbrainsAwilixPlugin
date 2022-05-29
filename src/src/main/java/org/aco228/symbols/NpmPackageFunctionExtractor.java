package org.aco228.symbols;

import org.aco228.symbols.models.NpmPackageFindResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NpmPackageFunctionExtractor {

    private final String _srcLocation;
    private final String _relativeLocation;
    private final Pattern _searchPattern;

    public NpmPackageFunctionExtractor(
            String srcFolderLocation,
            String relativeLocation,
            String symbolName,
            String searchFunction) {
        _srcLocation = srcFolderLocation;
        _relativeLocation = relativeLocation;
        _searchPattern = Pattern.compile("( "+searchFunction+")(?=\\()");
    }

    public NpmPackageFindResult getResult(){
        Path path = Paths.get(_srcLocation, "node_modules", _relativeLocation);
        File directory = new File(path.toString());
        if (!directory.isDirectory()) {
            return null;
        }

        return visitDirectory(directory, 0);
    }

    private NpmPackageFindResult visitDirectory(File directory, int depth) {
        if(depth >= 15) {
            return null;
        }

        if(!directory.isDirectory()) {
            return null;
        }

        int nextDepth = depth++;
        for(File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()){
                if(file.getName().toLowerCase().equals("domain")) {
                    continue;
                }
                NpmPackageFindResult recFile = visitDirectory(file, nextDepth);
                if(recFile != null) {
                    return recFile;
                }
            }
            if (file.isFile() && file.getName().toLowerCase().endsWith(".js")) {
                NpmPackageFindResult result = readFile(file);
                if(result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private NpmPackageFindResult readFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            int lineNumber = 0;

            while (line != null) {
                ++lineNumber;
                line = br.readLine();

                if (line == null || line.isEmpty() || line.isBlank()) {
                    continue;
                }

                Matcher match = _searchPattern.matcher(line);
                if (match.find()) {
                    String location = "." + file.getPath().substring(_srcLocation.length()).replaceAll("\\\\", "/");
                    return new NpmPackageFindResult(location, lineNumber);
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
