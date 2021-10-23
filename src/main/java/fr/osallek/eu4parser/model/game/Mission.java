package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Mission {

    private final ClausewitzItem item;

    private final Game game;

    private final MissionTree missionsTree;

    public Mission(ClausewitzItem item, Game game, MissionTree missionsTree) {
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

    public MissionTree getMissionsTree() {
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

    public Condition getProvincesToHighlight() {
        ClausewitzItem child = this.item.getChild("provinces_to_highlight");
        return child == null ? null : new Condition(child);
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public List<Mission> getRequiredMissions() {
        ClausewitzList list = this.item.getList("required_missions");
        return list == null ? null : list.getValues().stream().map(this.game::getMission).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Mission)) {
            return false;
        }

        Mission mission = (Mission) o;

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
