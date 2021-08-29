package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Comparator;
import java.util.Objects;

/*
    Todo: list of modifiers: https://eu4.paradoxwikis.com/Technology_modding
 */

public class Technology {

    private final ClausewitzItem item;

    private final int number;

    private final Power type;

    private final Modifiers aheadOfTime;

    public Technology(ClausewitzItem item, Power power, Modifiers aheadOfTime, int number) {
        this.item = item;
        this.type = power;
        this.aheadOfTime = aheadOfTime;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public Power getType() {
        return type;
    }

    public Modifiers getAheadOfTime() {
        return aheadOfTime;
    }

    public int getYear() {
        return this.item.getVarAsInt("year");
    }

    public void setYear(int year) {
        this.item.setVariable("year", year);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("year"), "tech_");
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
        return type + " " + number + " (" + getYear() + ')';
    }
}
