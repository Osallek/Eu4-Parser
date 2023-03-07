package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.effects.Effects;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class AgeAbility {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public AgeAbility(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getAllow() {
        return Optional.ofNullable(this.item.getChild("allow")).map(ConditionAnd::new).orElse(null);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Effects getEffects() {
        return Optional.ofNullable(this.item.getChild("effect")).map(i -> new Effects(i, this.game)).orElse(null);
    }

    public List<String> getRules() {
        return Optional.ofNullable(this.item.getChild("rule"))
                       .map(ClausewitzItem::getVariables)
                       .map(Collection::stream)
                       .map(s -> s.map(ClausewitzVariable::getName))
                       .map(Stream::toList)
                       .orElse(null);
    }

    public File getImage() {
        return this.game.getSpriteTypeImageFile("GFX_" + getName());
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getImage()), "png", dest.toFile());
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

        if (!(o instanceof AgeAbility ageAbility)) {
            return false;
        }

        return Objects.equals(getName(), ageAbility.getName());
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
