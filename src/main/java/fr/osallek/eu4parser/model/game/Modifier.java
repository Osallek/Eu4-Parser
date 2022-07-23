package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;

import fr.osallek.eu4parser.common.Eu4Utils;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class Modifier {

    private final String name;

    private final ModifierType type;

    private final ModifierScope scope;

    public Modifier(String name, ModifierType type, ModifierScope scope) {
        this.name = ClausewitzUtils.removeQuotes(name.toLowerCase());
        this.type = type;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public ModifierType getType() {
        return type;
    }

    public ModifierScope getScope() {
        return scope;
    }

    public File getImage(Game game) {
        return game.getAbsoluteFile(Path.of(Eu4Utils.GFX_FOLDER_PATH, Eu4Utils.INTERFACE_FOLDER_PATH, "ideas_EU4", getName() + ".dds"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Modifier modifier)) {
            return false;
        }

        return Objects.equals(getName(), modifier.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
