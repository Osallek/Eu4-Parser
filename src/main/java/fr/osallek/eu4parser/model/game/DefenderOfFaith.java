package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Comparator;
import java.util.Objects;

public class DefenderOfFaith implements Comparable<DefenderOfFaith> {

    private final String name;

    private final int level;

    private final Integer rangeFrom;

    private final Integer rangeTo;

    private final Modifiers modifiers;

    public DefenderOfFaith(ClausewitzItem item) {
        this.name = item.getName();
        this.level = item.getVarAsInt("level");
        this.rangeFrom = item.getVarAsInt("range_from");
        this.rangeTo = item.getVarAsInt("range_to");

        this.modifiers = new Modifiers(item.getChild("modifier"));
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Integer getRangeFrom() {
        return rangeFrom;
    }

    public Integer getRangeTo() {
        return rangeTo;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public boolean isInRange(int range) {
        return (this.rangeFrom == null || this.rangeFrom <= range) && (this.rangeTo == null || this.rangeTo > range);
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
    public int compareTo(DefenderOfFaith o) {
        return Comparator.comparingInt(DefenderOfFaith::getLevel).compare(this, o);
    }
}
