package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Power;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

public class Technology implements Comparable<Technology> {

    private final Power type;

    private final Modifiers aheadOfTime;

    private final int year;

    private final Modifiers modifiers;

    public Technology(ClausewitzItem item, Power power, Modifiers aheadOfTime) {
        this.type = power;
        this.aheadOfTime = aheadOfTime;
        this.year = item.getVarAsInt("year");

        this.modifiers = new Modifiers(item.getVarsNot("year"));
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
    public int compareTo(@NotNull Technology o) {
        return Comparator.comparingInt(Technology::getYear).compare(this, o);
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
        return year == that.year &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, year);
    }

    @Override
    public String toString() {
        return type + " (" + year + ')';
    }
}
