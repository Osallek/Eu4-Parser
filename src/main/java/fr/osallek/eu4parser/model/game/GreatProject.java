package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GreatProject {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public GreatProject(ClausewitzItem item, Game game) {
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
        return this.item.getVarAsInt("start");
    }

    public void setStart(int start) {
        this.item.setVariable("start", start);
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public Integer getMoveDaysPerUnitDistance() {
        return this.item.getVarAsInt("move_days_per_unit_distance");
    }

    public void setMoveDaysPerUnitDistance(Integer days) {
        this.item.setVariable("move_days_per_unit_distance", days);
    }

    public Time getTime() {
        ClausewitzItem child = item.getChild("time");
        return child == null ? null : new Time(child);
    }

    public void setTime(int months) {
        ClausewitzItem child = item.getChild("time");

        if (child == null) {
            child = this.item.addChild("time");
        }

        new Time(child).setMonths(months);
    }

    public int getBuildCost() {
        return this.item.getVarAsInt("build_cost");
    }

    public void setBuildCost(int buildCost) {
        this.item.setVariable("build_cost", buildCost);
    }

    public int getStartingTier() {
        return this.item.getVarAsInt("starting_tier");
    }

    public void setStartingTier(int startingTier) {
        this.item.setVariable("starting_tier", startingTier);
    }

    public GreatProjectType getType() {
        return GreatProjectType.valueOf(this.item.getVarAsString("type").toUpperCase());
    }

    public void setType(GreatProjectType type) {
        this.item.setVariable("type", type.name().toLowerCase());
    }

    public boolean isCanBeMoved() {
        return BooleanUtils.toBoolean(item.getVarAsBool("can_be_moved"));
    }

    public void setCanBeMoved(boolean canBeMoved) {
        this.item.setVariable("can_be_moved", canBeMoved);
    }

    public List<GreatProjectTier> getTiers() {
        return this.item.getChildrenStartWith("tier_").stream().map(item1 -> new GreatProjectTier(item1, this.game)).toList();
    }

    public int getMaxLevel() {
        return Math.max(0, CollectionUtils.size(this.item.getChildrenStartWith("tier_")) - 1);
    }

    public ConditionAnd getCanUpgradeTrigger() {
        return Optional.ofNullable(this.item.getChild("can_upgrade_trigger")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getCanUseModifiersTrigger() {
        return Optional.ofNullable(this.item.getChild("can_use_modifiers_trigger")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getKeepTrigger() {
        return Optional.ofNullable(this.item.getChild("keep_trigger")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getBuildTrigger() {
        return Optional.ofNullable(this.item.getChild("build_trigger")).map(ConditionAnd::new).orElse(null);
    }

    public Modifiers getOnBuilt() {
        return Optional.ofNullable(this.item.getChild("on_built")).map(Modifiers::new).orElse(null);
    }

    public Modifiers getOnDestroy() {
        return Optional.ofNullable(this.item.getChild("on_destroy")).map(Modifiers::new).orElse(null);
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile("GFX_great_project_" + ClausewitzUtils.removeQuotes(getName()));
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

        if (!(o instanceof GreatProject greatProject)) {
            return false;
        }

        return Objects.equals(getName(), greatProject.getName());
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
