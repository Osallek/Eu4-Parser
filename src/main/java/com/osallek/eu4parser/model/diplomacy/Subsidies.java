package com.osallek.eu4parser.model.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class Subsidies extends QuantifyDatableRelation {

    public Subsidies(ClausewitzItem item) {
        super(item);
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public void setDuration(int duration) {
        this.item.setVariable("duration", duration);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, double amount, int duration) {
        ClausewitzItem toItem = QuantifyDatableRelation.addToItem(parent, "subsidies", first, second, startDate, amount);
        toItem.addVariable("duration", duration);

        return toItem;
    }
}
