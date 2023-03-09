package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Power;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Faction {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public Faction(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        ClausewitzVariable variable = item.getVar("monarch_power");
        return variable == null ? null : Power.byName(variable.getValue());
    }

    public void setCategory(Power category) {
        this.item.setVariable("monarch_power", category.name());
    }

    public ConditionAnd getTrigger() {
        return new ConditionAnd(this.item, "monarch_power", "modifier", "triggered_faction_name");
    }

    public List<Names> getNames() {
        List<ClausewitzItem> namesItems = this.item.getChildren("triggered_faction_name");
        return namesItems.stream().map(Names::new).toList();
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public File getImage() {
        return this.game.getSpriteTypeImageFile("GFX_faction_" + getName());
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

        if (!(o instanceof Faction faction)) {
            return false;
        }

        return Objects.equals(getName(), faction.getName());
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
