package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public String getIcon() {
        return this.item.getVarAsString("icon");
    }

    public void setIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public Path getIconPath(String extension) {
        SpriteType spriteType = this.game.getSpriteType(getIcon());
        return Path.of(FilenameUtils.removeExtension(ClausewitzUtils.removeQuotes(spriteType.getTextureFile())) + "." + extension);
    }

    public File getIconFile() {
        return this.game.getSpriteTypeImageFile(getIcon());
    }

    public Boolean isGeneric() {
        return this.item.getVarAsBool("generic");
    }

    public void setGeneric(Boolean generic) {
        if (generic == null) {
            this.item.removeVariable("generic");
        } else {
            this.item.setVariable("generic", generic);
        }
    }

    public Integer getPosition() {
        return this.item.getVarAsInt("position");
    }

    public void setPosition(Integer position) {
        if (position == null) {
            this.item.removeVariable("position");
        } else {
            this.item.setVariable("position", position);
        }
    }

    public LocalDate getCompletedBy() {
        return this.item.getVarAsDate("completed_by");
    }

    public void setCompletedBy(LocalDate completedBy) {
        if (completedBy == null) {
            this.item.removeVariable("completed_by");
        } else {
            this.item.setVariable("completed_by", completedBy);
        }
    }

    public ConditionAnd getProvincesToHighlight() {
        ClausewitzItem child = this.item.getChild("provinces_to_highlight");
        return child == null ? null : new ConditionAnd(child);
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public List<Mission> getRequiredMissions() {
        ClausewitzList list = this.item.getList("required_missions");
        return list == null ? null : list.getValues().stream().map(this.game::getMission).filter(Objects::nonNull).toList();
    }

    public void setRequiredMissions(List<String> requiredMissions) {
        if (CollectionUtils.isEmpty(requiredMissions)) {
            ClausewitzList list = this.item.getList("required_missions");

            if (list != null) {
                list.clear();
            } else {
                this.item.addList("required_missions", new ArrayList<>());
            }

            return;
        }

        ClausewitzList list = this.item.getList("required_missions");

        if (list != null) {
            list.setAll(requiredMissions.stream().filter(Objects::nonNull).toList());
        } else {
            this.item.addList("required_missions", requiredMissions.stream().filter(Objects::nonNull).toList());
        }
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getIconFile()), "png", dest.toFile());
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
