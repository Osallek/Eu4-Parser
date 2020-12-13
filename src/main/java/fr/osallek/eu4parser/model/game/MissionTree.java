package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MissionTree {

    private final String name;

    private String localizedName;

    private final int slot;

    private final boolean generic;

    private final boolean ai;

    private final boolean hasCountryShield;

    private final Condition potentialOnLoad;

    private final Condition potential;

    private final Map<String, Mission> missions;

    public MissionTree(ClausewitzItem item, Game game) {
        this.name = item.getName();
        this.slot = item.getVarAsInt("slot");
        this.generic = BooleanUtils.toBoolean(item.getVarAsBool("generic"));
        this.ai = BooleanUtils.toBoolean(item.getVarAsBool("ai"));
        this.hasCountryShield = BooleanUtils.toBoolean(item.getVarAsBool("has_country_shield"));

        ClausewitzItem child = item.getChild("potential_on_load");
        this.potentialOnLoad = child == null ? null : new Condition(child);

        child = item.getChild("potential");
        this.potential = child == null ? null : new Condition(child);

        this.missions = item.getChildrenNot("potential_on_load", "potential")
                            .stream()
                            .map(Mission::new)
                            .collect(Collectors.toMap(Mission::getName, Function.identity()));
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

    public int getSlot() {
        return slot;
    }

    public boolean isGeneric() {
        return generic;
    }

    public boolean isAi() {
        return ai;
    }

    public boolean isHasCountryShield() {
        return hasCountryShield;
    }

    public Condition getPotentialOnLoad() {
        return potentialOnLoad;
    }

    public Condition getPotential() {
        return potential;
    }

    public Map<String, Mission> getMissions() {
        return missions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MissionTree)) {
            return false;
        }

        MissionTree area = (MissionTree) o;

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
