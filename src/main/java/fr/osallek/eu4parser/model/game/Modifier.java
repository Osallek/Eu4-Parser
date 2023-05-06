package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Localised;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class Modifier implements Localised {

    private final String name;

    private final ModifierType type;

    private final ModifierScope scope;

    private String localisationKey;

    private Path writenTo;

    public Modifier(String name, ModifierType type, ModifierScope scope) {
        this.name = ClausewitzUtils.removeQuotes(name.toLowerCase());
        this.type = type;
        this.scope = scope;
        this.localisationKey = name;
    }

    public Modifier(String name, ModifierType type, ModifierScope scope, String localisationKey) {
        this(name, type, scope);
        this.localisationKey = localisationKey;
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

    @Override
    public String getLocalisationKey() {
        return localisationKey;
    }

    public File getImage(Game game) {
        return game.getAbsoluteFile(Path.of(Eu4Utils.GFX_FOLDER_PATH, Eu4Utils.INTERFACE_FOLDER_PATH, "ideas_EU4", getName() + ".dds"));
    }

    public void writeImageTo(Path dest, Game game) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getImage(game)), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
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
