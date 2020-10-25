package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Age {

    private final String name;

    private String localizedName;

    private final int start;

    private final boolean religiousConflict;

    private final Condition canStart;

    private final Map<String, Integer> absolutism;

    private final Double papacy;

    private final Map<String, AgeObjective> objectives;

    private final Map<String, AgeAbility> abilities;

    public Age(ClausewitzItem item) {
        this.name = item.getName();
        this.start = item.getVarAsInt("start");
        this.religiousConflict = BooleanUtils.toBoolean(item.getVarAsBool("religious_conflicts"));
        this.papacy = item.getVarAsDouble("papacy");

        ClausewitzItem child = item.getChild("can_start");
        this.canStart = child == null ? null : new Condition(child);

        child = item.getChild("absolutism");
        this.absolutism = child == null ? null : child.getVariables()
                                                      .stream()
                                                      .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsInt));

        child = item.getChild("abilities");
        this.abilities = child == null ? null : child.getChildren()
                                                     .stream()
                                                     .map(AgeAbility::new)
                                                     .collect(Collectors.toMap(AgeAbility::getName, Function.identity()));

        child = item.getChild("objectives");
        this.objectives = child == null ? null : child.getChildren()
                                                      .stream()
                                                      .map(AgeObjective::new)
                                                      .collect(Collectors.toMap(AgeObjective::getName, Function.identity()));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public int getStart() {
        return start;
    }

    public boolean getReligiousConflict() {
        return religiousConflict;
    }

    public Condition getCanStart() {
        return canStart;
    }

    public Map<String, Integer> getAbsolutism() {
        return absolutism;
    }

    public Double getPapacy() {
        return papacy;
    }

    public Map<String, AgeObjective> getObjectives() {
        return objectives;
    }

    public Map<String, AgeAbility> getAbilities() {
        return abilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Age)) {
            return false;
        }

        Age age = (Age) o;

        return Objects.equals(name, age.name);
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
