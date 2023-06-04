package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class ProfessionalismModifier implements Comparable<ProfessionalismModifier> {

    private final ClausewitzItem item;

    public ProfessionalismModifier(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return Optional.of(this.item.getVarsNot("marker_sprite", "unit_sprite_start", "army_professionalism", "hidden"))
                       .filter(CollectionUtils::isNotEmpty)
                       .map(Modifiers::new);
    }

    public double getArmyProfessionalism() {
        return this.item.getVarAsDouble("army_professionalism").orElse(0d);
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
