package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Subsidies extends QuantifyDatableRelation {

    public Subsidies(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public void setDuration(int duration) {
        this.item.setVariable("duration", duration);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, LocalDate startDate, double amount, int duration) {
        ClausewitzItem toItem = QuantifyDatableRelation.addToItem(parent, "subsidies", first, second, startDate, amount);
        toItem.addVariable("duration", duration);

        return toItem;
    }
}
