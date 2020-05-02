package com.osallek.eu4parser;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.eu4parser.model.Save;

import java.io.File;
import java.util.zip.ZipFile;

public class Eu4Parser {
    private Eu4Parser() {}

    public static Save loadSave(File file) {
        return new Save(ClausewitzParser.parse(file, 1));
    }

    public static Save loadSave(ZipFile zipFile) {
        return new Save(ClausewitzParser.parse(zipFile, "gamestate", 1));
    }
}
