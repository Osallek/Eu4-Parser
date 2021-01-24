package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class ProfessionalismModifier implements Comparable<ProfessionalismModifier> {

    private final Condition trigger;

    private final Modifiers modifiers;

    private final double armyProfessionalism;

    public ProfessionalismModifier(ClausewitzItem item) {
        this.armyProfessionalism = item.getVarAsDouble("army_professionalism");
        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
        this.modifiers = new Modifiers(item.getVarsNot("marker_sprite", "unit_sprite_start", "army_professionalism", "hidden"));
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public double getArmyProfessionalism() {
        return armyProfessionalism;
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
        return Double.compare(that.armyProfessionalism, armyProfessionalism) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(armyProfessionalism);
    }

    @Override
    public String toString() {
        return String.valueOf(armyProfessionalism);
    }
}
