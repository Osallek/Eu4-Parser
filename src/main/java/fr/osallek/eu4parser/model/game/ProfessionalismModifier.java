package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class ProfessionalismModifier implements Comparable<ProfessionalismModifier> {

    private final ClausewitzItem item;

    public ProfessionalismModifier(ClausewitzItem item) {
        this.item = item;
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setTrigger(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("trigger");
            return;
        }

        ClausewitzItem child = this.item.getChild("trigger");
        //Todo Condition => item
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("marker_sprite", "unit_sprite_start", "army_professionalism", "hidden"));
    }

    public double getArmyProfessionalism() {
        return this.item.getVarAsDouble("army_professionalism");
    }

    public void setArmyProfessionalism(double armyProfessionalism) {
        this.item.setVariable("army_professionalism", armyProfessionalism);
    }

    @Override
    public int compareTo(ProfessionalismModifier o) {
        return Comparator.comparingDouble(ProfessionalismModifier::getArmyProfessionalism).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProfessionalismModifier that = (ProfessionalismModifier) o;
        return Double.compare(that.getArmyProfessionalism(), getArmyProfessionalism()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArmyProfessionalism());
    }

    @Override
    public String toString() {
        return String.valueOf(getArmyProfessionalism());
    }
}
