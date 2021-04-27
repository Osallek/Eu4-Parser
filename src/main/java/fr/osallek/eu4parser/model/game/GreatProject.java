package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GreatProject {

    private final String name;

    private String localizedName;

    private final String ambientObject;

    private final int start;

    private final LocalDate date;

    private Time time;

    private int buildCost;

    private int startingTier;

    private final GreatProjectType type;

    private boolean canBeMoved;

    private final Modifiers modifiers;

    private List<GreatProjectTier> tiers;

    public GreatProject(ClausewitzItem item) {
        this.name = item.getName();
        this.ambientObject = item.getVarAsString("ambient_object");
        this.start = item.getVarAsInt("start");
        this.date = item.getVarAsDate("date");
        this.buildCost = item.getVarAsInt("build_cost");
        this.startingTier = item.getVarAsInt("starting_tier");
        this.type = GreatProjectType.valueOf(item.getVarAsString("type").toUpperCase());
        this.canBeMoved = BooleanUtils.toBoolean(item.getVarAsBool("can_be_moved"));

        if (item.hasChild("time")) {
            this.time = new Time(item.getChild("time"));
        }

        this.modifiers = new Modifiers(item.getChild("modifier"));

        this.tiers = item.getChildrenStartWith("tier_").stream().map(GreatProjectTier::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getAmbientObject() {
        return ambientObject;
    }

    public int getStart() {
        return start;
    }

    public LocalDate getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getBuildCost() {
        return buildCost;
    }

    public void setBuildCost(int buildCost) {
        this.buildCost = buildCost;
    }

    public int getStartingTier() {
        return startingTier;
    }

    public void setStartingTier(int startingTier) {
        this.startingTier = startingTier;
    }

    public GreatProjectType getType() {
        return type;
    }

    public boolean isCanBeMoved() {
        return canBeMoved;
    }

    public void setCanBeMoved(boolean canBeMoved) {
        this.canBeMoved = canBeMoved;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public List<GreatProjectTier> getTiers() {
        return tiers;
    }

    public void setTiers(List<GreatProjectTier> tiers) {
        this.tiers = tiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GreatProject)) {
            return false;
        }

        GreatProject area = (GreatProject) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
