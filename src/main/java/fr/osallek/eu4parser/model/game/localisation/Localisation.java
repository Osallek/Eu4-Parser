package fr.osallek.eu4parser.model.game.localisation;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.game.FileNode;
import fr.osallek.eu4parser.model.game.Nodded;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

public class Localisation extends Nodded {

    private String key;

    private Eu4Language eu4Language;

    private String version;

    private String value;

    public Localisation(String key, Eu4Language eu4Language, String version, String value) {
        this.key = key;
        this.eu4Language = eu4Language;
        this.version = version;
        this.value = value;
    }

    public Localisation(FileNode fileNode, String key, Eu4Language eu4Language, String version, String value) {
        super(fileNode);
        this.key = key;
        this.eu4Language = eu4Language;
        this.version = version;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Eu4Language getEu4Language() {
        return eu4Language;
    }

    public void setEu4Language(Eu4Language eu4Language) {
        this.eu4Language = eu4Language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void write(BufferedWriter writer) throws IOException {
        ClausewitzUtils.printSpace(writer);
        writer.write(this.key);
        writer.write(':');
        writer.write(this.version);
        ClausewitzUtils.printSpace(writer);
        writer.write('"');
        writer.write(ClausewitzUtils.removeQuotes(this.value).replace("\"", "\\\""));
        writer.write('"');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Localisation)) {
            return false;
        }

        Localisation that = (Localisation) o;
        return Objects.equals(key, that.key) && eu4Language == that.eu4Language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, eu4Language);
    }

    @Override
    public String toString() {
        return key;
    }
}
