package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class Advisor {

    private final String name;

    private String localizedName;

    private final Power power;

    private final boolean allowOnlyMale;

    private final boolean allowOnlyFemale;

    private final Modifiers skillScaledModifier;

    private final Modifiers modifiers;

    public Advisor(ClausewitzItem item) {
        this.name = item.getName();
        this.power = Power.valueOf(item.getVarAsString("monarch_power").toUpperCase());
        this.allowOnlyMale = BooleanUtils.toBoolean(item.getVarAsBool("allow_only_male"));
        this.allowOnlyFemale = BooleanUtils.toBoolean(item.getVarAsBool("allow_only_female"));
        this.modifiers = new Modifiers(item.getVarsNot("monarch_power", "allow_only_male", "allow_only_female"));
        this.skillScaledModifier = new Modifiers(item.getChild("skill_scaled_modifier"));
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

    public Modifiers getSkillScaledModifier() {
        return skillScaledModifier;
    }

    public Modifiers getModifiers() {
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
