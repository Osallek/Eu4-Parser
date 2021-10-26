package fr.osallek.eu4parser.model.game.localisation;

import java.nio.file.Path;
import java.util.Objects;

public class Localisation {

    private String key;

    private Eu4Language eu4Language;

    private String value;

    private Path path;

    public Localisation(String key, Eu4Language eu4Language, String value, Path path) {
        this.key = key;
        this.eu4Language = eu4Language;
        this.value = value;
        this.path = path;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Localisation)) {
            return false;
        }

        Localisation localisation = (Localisation) o;

        return Objects.equals(key, localisation.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
