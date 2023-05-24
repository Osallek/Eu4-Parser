package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Age {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public Age(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getStart() {
        return this.item.getVarAsInt("start").get();
    }

    public void setStart(int start) {
        this.item.setVariable("start", start);
    }

    public Optional<Boolean> getReligiousConflict() {
        return this.item.getVarAsBool("religious_conflicts");
    }

    public void setReligiousConflict(Boolean religiousConflict) {
        if (religiousConflict == null) {
            this.item.removeVariable("religious_conflicts");
        } else {
            this.item.setVariable("religious_conflicts", religiousConflict);
        }
    }

    public Optional<ConditionAnd> getCanStart() {
        return this.item.getChild("can_start").map(ConditionAnd::new);
    }

    public Optional<Double> getPapacy() {
        return this.item.getVarAsDouble("papacy");
    }

    public void setPapacy(Double papacy) {
        if (papacy == null) {
            this.item.removeVariable("papacy");
        } else {
            this.item.setVariable("papacy", papacy);
        }
    }

    public Map<String, Double> getAbsolutism() {
        return this.item.getChild("absolutism")
                        .map(ClausewitzItem::getVariables)
                        .map(Collection::stream)
                        .map(s -> s.collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsDouble)))
                        .orElse(null);
    }

    public void setAbsolutism(Map<String, Integer> absolutism) {
        if (MapUtils.isEmpty(absolutism)) {
            this.item.removeChild("absolutism");
        } else {
            ClausewitzItem child = this.item.getChild("absolutism").map(item1 -> {
                item1.removeAllVariables();
                return item1;
            }).orElse(this.item.addChild("absolutism"));

            ClausewitzItem finalChild = child;
            absolutism.forEach((s, integer) -> {
                if (integer != null) {
                    finalChild.addVariable(s, integer);
                }
            });
        }
    }

    public List<AgeObjective> getObjectives() {
        return this.item.getChild("objectives")
                        .map(ClausewitzItem::getChildren)
                        .map(Collection::stream)
                        .map(s -> s.map(i -> new AgeObjective(i, this.game)))
                        .map(Stream::toList)
                        .orElse(null);
    }

    public List<AgeAbility> getAbilities() {
        return this.item.getChild("abilities")
                        .map(ClausewitzItem::getChildren)
                        .map(Collection::stream)
                        .map(s -> s.map(i -> new AgeAbility(i, this.game)))
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

        if (!(o instanceof Age age)) {
            return false;
        }

        return Objects.equals(getName(), age.getName());
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
