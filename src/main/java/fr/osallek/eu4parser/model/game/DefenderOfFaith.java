package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class DefenderOfFaith implements Comparable<DefenderOfFaith> {

    private final ClausewitzItem item;

    public DefenderOfFaith(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getLevel() {
        return this.item.getVarAsInt("level");
    }

    public void setLevel(int level) {
        this.item.setVariable("level", level);
    }
    
    public Integer getRangeFrom() {
        return this.item.getVarAsInt("range_from");
    }

    public void setRangeFrom(Integer rangeFrom) {
        if (rangeFrom == null) {
            this.item.removeVariable("range_from");
        } else {
            this.item.setVariable("range_from", rangeFrom);
        }
    }

    public Integer getRangeTo() {
        return this.item.getVarAsInt("range_to");
    }

    public void setRangeTo(Integer rangeTo) {
        if (rangeTo == null) {
            this.item.removeVariable("range_to");
        } else {
            this.item.setVariable("range_to", rangeTo);
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public boolean isInRange(int range) {
        return (getRangeFrom() == null || getRangeFrom() <= range) && (getRangeTo() == null || getRangeTo() > range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DefenderOfFaith)) {
            return false;
        }

        DefenderOfFaith area = (DefenderOfFaith) o;

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

    @Override
    public int compareTo(DefenderOfFaith o) {
        return Comparator.comparingInt(DefenderOfFaith::getLevel).compare(this, o);
    }
}
