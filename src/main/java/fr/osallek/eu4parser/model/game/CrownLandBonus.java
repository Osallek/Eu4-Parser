package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;
import java.util.Optional;

public class CrownLandBonus implements Comparable<CrownLandBonus> {

    private final ClausewitzItem item;

    public CrownLandBonus(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<Double> getRangeFrom() {
        return this.item.getVarAsDouble("range_from");
    }

    public void setRangeFrom(Double rangeFrom) {
        if (rangeFrom == null) {
            this.item.removeVariable("range_from");
        } else {
            this.item.setVariable("range_from", rangeFrom);
        }
    }

    public Optional<Double> getRangeTo() {
        return this.item.getVarAsDouble("range_to");
    }

    public void setRangeTo(Double rangeTo) {
        if (rangeTo == null) {
            this.item.removeVariable("range_to");
        } else {
            this.item.setVariable("range_to", rangeTo);
        }
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public boolean isInRange(double range) {
        return (getRangeFrom().isEmpty() || getRangeFrom().get() <= range) && (getRangeTo().isEmpty() || getRangeTo().get() > range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CrownLandBonus crownLandBonus)) {
            return false;
        }

        return Objects.equals(getName(), crownLandBonus.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(CrownLandBonus o) {
        return getRangeFrom().orElse(0d).compareTo(o.getRangeFrom().orElse(0d));
    }
}
