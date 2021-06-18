package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Comparator;
import java.util.Objects;

public class Technology implements Comparable<Technology> {

    private int number;

    private final Power type;

    private final Modifiers aheadOfTime;

    private final int year;

    private final Modifiers modifiers;

    public Technology(ClausewitzItem item, Power power, Modifiers aheadOfTime) {
        this.type = power;
        this.aheadOfTime = aheadOfTime;
        this.year = item.getVarAsInt("year");

        this.modifiers = new Modifiers(item.getVarsNot("year"), "tech_");
    }

    public int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }

    public Power getType() {
        return type;
    }

    public Modifiers getAheadOfTime() {
        return aheadOfTime;
    }

    public int getYear() {
        return year;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public int compareTo(Technology o) {
        return Comparator.comparingInt(Technology::getNumber).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Technology that = (Technology) o;
        return number == that.number &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, number);
    }

    @Override
    public String toString() {
        return type + " " + number + " (" + year + ')';
    }
}
