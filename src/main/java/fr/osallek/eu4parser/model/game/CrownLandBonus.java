package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class CrownLandBonus implements Comparable<CrownLandBonus> {

    private final String name;

    private final Double rangeFrom;

    private final Double rangeTo;

    private final Modifiers modifiers;

    public CrownLandBonus(ClausewitzItem item) {
        this.name = item.getVarAsString("key");
        this.rangeFrom = item.getVarAsDouble("range_from");
        this.rangeTo = item.getVarAsDouble("range_to");

        this.modifiers = new Modifiers(item.getChild("modifier"));
    }

    public String getName() {
        return name;
    }

    public Double getRangeFrom() {
        return rangeFrom;
    }

    public Double getRangeTo() {
        return rangeTo;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public boolean isInRange(double range) {
        return (this.rangeFrom == null || this.rangeFrom <= range) && (this.rangeTo == null || this.rangeTo > range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CrownLandBonus)) {
            return false;
        }

        CrownLandBonus area = (CrownLandBonus) o;

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

    @Override
    public int compareTo(CrownLandBonus o) {
        return Comparator.comparingDouble(CrownLandBonus::getRangeFrom).compare(this, o);
    }
}
