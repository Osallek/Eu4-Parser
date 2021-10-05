package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Objects;

public class Advisor {

    private final ClausewitzItem item;

    public Advisor(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getPower() {
        return Power.byName(this.item.getVarAsString("monarch_power"));
    }

    public void setPower(Power power) {
        this.item.setVariable("monarch_power", power.name());
    }

    public Boolean allowOnlyMale() {
        return this.item.getVarAsBool("allow_only_male");
    }

    public void setAllowOnlyMale(Boolean onlyMale) {
        if (onlyMale == null) {
            this.item.removeVariable("allow_only_male");
        } else {
            this.item.setVariable("allow_only_male", onlyMale);
        }
    }

    public Boolean allowOnlyFemale() {
        return this.item.getVarAsBool("allow_only_female");
    }

    public void setAllowOnlyFemale(Boolean onlyFemale) {
        if (onlyFemale == null) {
            this.item.removeVariable("allow_only_female");
        } else {
            this.item.setVariable("allow_only_female", onlyFemale);
        }
    }

    public Modifiers getSkillScaledModifier() {
        return new Modifiers(this.item.getChild("skill_scaled_modifier"));
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("monarch_power", "allow_only_male", "allow_only_female"));
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

        return Objects.equals(getName(), area.getName());
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
