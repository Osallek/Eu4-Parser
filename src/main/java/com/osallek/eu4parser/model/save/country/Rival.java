package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class Rival {

    private final ClausewitzItem item;

    public Rival(ClausewitzItem item) {
        this.item = item;
    }

    public String getRival() {
        return this.item.getVarAsString("country");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
