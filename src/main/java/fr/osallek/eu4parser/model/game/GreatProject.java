package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GreatProject {

    private final ClausewitzItem item;

    public GreatProject(ClausewitzItem item) {
        this.item = item;
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

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public List<GreatProjectTier> getTiers() {
        return this.item.getChildrenStartWith("tier_").stream().map(GreatProjectTier::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GreatProject)) {
            return false;
        }

        GreatProject greatProject = (GreatProject) o;

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
