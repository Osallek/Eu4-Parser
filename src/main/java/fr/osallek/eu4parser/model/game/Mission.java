package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Mission extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    private final MissionsTree missionsTree;

    private Path writenTo;

    public Mission(ClausewitzItem item, Game game, MissionsTree missionsTree) {
        super(missionsTree.getFileNode());
        this.item = item;
        this.game = game;
        this.missionsTree = missionsTree;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public MissionsTree getMissionsTree() {
        return missionsTree;
    }

    public Optional<String> getIcon() {
        return this.item.getVarAsString("icon");
    }

    public void setIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public Optional<Path> getIconPath(String extension) {
        return getIcon().map(this.game::getSpriteType).map(type -> type.getTextureFilePath(extension));
    }

    public Optional<File> getIconFile() {
        return getIcon().map(this.game::getSpriteTypeImageFile);
    }

    public Optional<Boolean> isGeneric() {
        return this.item.getVarAsBool("generic");
    }

    public void setGeneric(Boolean generic) {
        if (generic == null) {
            this.item.removeVariable("generic");
        } else {
            this.item.setVariable("generic", generic);
        }
    }

    public Optional<Integer> getPosition() {
        return this.item.getVarAsInt("position");
    }

    public void setPosition(Integer position) {
        if (position == null) {
            this.item.removeVariable("position");
        } else {
            this.item.setVariable("position", position);
        }
    }

    public Optional<LocalDate> getCompletedBy() {
        return this.item.getVarAsDate("completed_by");
    }

    public void setCompletedBy(LocalDate completedBy) {
        if (completedBy == null) {
            this.item.removeVariable("completed_by");
        } else {
            this.item.setVariable("completed_by", completedBy);
        }
    }

    public Optional<ConditionAnd> getProvincesToHighlight() {
        return this.item.getChild("provinces_to_highlight").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public List<Mission> getRequiredMissions() {
        return this.item.getList("required_missions")
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this.game::getMission)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public void setRequiredMissions(List<String> requiredMissions) {
        this.item.getList("required_missions")
                 .ifPresentOrElse(list -> list.setAll(requiredMissions.stream().filter(Objects::nonNull).toList()),
                                  () -> this.item.addList("required_missions", requiredMissions.stream().filter(Objects::nonNull).toList()));
    }

    public void writeImageTo(Path dest) throws IOException {
        Optional<File> icon = getIconFile();

        if (icon.isPresent()) {
            FileUtils.forceMkdirParent(dest.toFile());
            ImageIO.write(ImageIO.read(icon.get()), "png", dest.toFile());
            Eu4Utils.optimizePng(dest, dest);
            this.writenTo = dest;
        }
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.missionsTree.write(writer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Mission mission)) {
            return false;
        }

        return Objects.equals(getName(), mission.getName());
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
