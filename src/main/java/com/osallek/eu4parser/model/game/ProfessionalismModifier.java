package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProfessionalismModifier implements Comparable<ProfessionalismModifier> {

    private final Condition trigger;

    private final Map<String, List<String>> modifiers;

    private final double armyProfessionalism;

    public ProfessionalismModifier(ClausewitzItem item) {
        this.armyProfessionalism = item.getVarAsDouble("army_professionalism");
        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
        this.modifiers = item.getVarsNot("marker_sprite", "unit_sprite_start", "army_professionalism", "hidden")
                             .stream()
                             .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                            Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    public double getArmyProfessionalism() {
        return armyProfessionalism;
    }

    @Override
    public int compareTo(@NotNull ProfessionalismModifier o) {
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
