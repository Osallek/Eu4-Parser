package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Disaster extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public Disaster(FileNode fileNode, ClausewitzItem item, Game game) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<ConditionAnd> getPotential() {
        return this.item.getChild("potential").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getCanStart() {
        return this.item.getChild("can_start").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getCanStop() {
        return this.item.getChild("can_stop").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getProgress() {
        return this.item.getChild("progress").map(Modifiers::new);
    }

    public Optional<Modifiers> getModifier() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public Optional<String> getOnStart() {
        return this.item.getVarAsString("on_start");
    }

    public Optional<String> getOnEnd() {
        return this.item.getVarAsString("on_end");
    }

    public List<String> getMonthlyEvents() {
        return this.item.getChild("on_monthly")
                        .map(i -> i.getList("events"))
                        .stream()
                        .flatMap(Optional::stream)
                        .map(ClausewitzList::getValues)
                        .flatMap(Collection::stream)
                        .toList();
    }

    public Map<String, Integer> getRandomEvents() {
        return this.item.getChild("on_monthly")
                        .map(i -> i.getChild("random_events"))
                        .stream()
                        .flatMap(Optional::stream)
                        .map(ClausewitzItem::getVariables)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(ClausewitzVariable::getValue, v -> NumbersUtils.toInt(v.getName())));
    }

    public BufferedImage getImage() throws IOException {
        SpriteType spriteType = this.game.getSpriteType("GFX_disaster_" + getName());
        File file = this.game.getSpriteTypeImageFile("GFX_disaster_" + getName());
        BufferedImage image = ImageReader.convertFileToImage(file);

        double size = (double) image.getWidth() / NumbersUtils.intOrDefault(spriteType.getNoOfFrames(), 1);
        return image.getSubimage((int) size, 0, (int) size, image.getHeight());
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(getImage(), "png", dest.toFile());
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

        if (!(o instanceof Disaster faction)) {
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
