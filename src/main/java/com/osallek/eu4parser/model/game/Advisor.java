package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Power;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Advisor {

    private final String name;

    private String localizedName;

    private final Power power;

    private final boolean allowOnlyMale;

    private final boolean allowOnlyFemale;

    private final Map<String, String> skillScaledModifier;

    private final Map<String, String> modifiers;

    public Advisor(ClausewitzItem item) {
        this.name = item.getName();
        this.power = Power.valueOf(item.getVarAsString("monarch_power").toUpperCase());
        this.allowOnlyMale = BooleanUtils.toBoolean(item.getVarAsBool("allow_only_male"));
        this.allowOnlyFemale = BooleanUtils.toBoolean(item.getVarAsBool("allow_only_female"));
        this.modifiers = item.getVarsNot("monarch_power", "allow_only_male", "allow_only_female")
                             .stream()
                             .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getValue, (a, b) -> b, LinkedHashMap::new));
        ClausewitzItem child = item.getChild("skill_scaled_modifier");
        this.skillScaledModifier = child == null ? null : child.getVariables()
                                                               .stream()
                                                               .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                         ClausewitzVariable::getValue,
                                                                                         (a, b) -> b,
                                                                                         LinkedHashMap::new));
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

    public Power getPower() {
        return power;
    }

    public boolean allowOnlyMale() {
        return allowOnlyMale;
    }

    public boolean allowOnlyFemale() {
        return allowOnlyFemale;
    }

    public Map<String, String> getSkillScaledModifier() {
        return skillScaledModifier;
    }

    public Map<String, String> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Advisor)) {
            return false;
        }

        Advisor area = (Advisor) o;

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
