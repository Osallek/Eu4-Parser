package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class Time {

    private final ClausewitzItem item;

    public Time(ClausewitzItem item) {
        this.item = item;
    }

    public int getMonths() {
        return this.item.getVarAsInt("months");
    }

    public void setMonths(int months) {
        this.item.setVariable("months", months);
    }
}
