package org.aco228.symbols.models;

public class NpmPackageFindResult {
    public String location;
    public int lineNumber;

    public NpmPackageFindResult(String location, int lineNumber) {
        this.location = location;
        this.lineNumber = lineNumber;
    }
}
