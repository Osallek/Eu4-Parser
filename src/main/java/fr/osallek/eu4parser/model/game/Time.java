package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class Time {

    private int months;

    public Time(ClausewitzItem item) {
        this.months = item.getVarAsInt("months");
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }
}
