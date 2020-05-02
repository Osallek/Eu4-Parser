package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

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
        ClausewitzVariable var = this.item.getVar("date");

        if (var != null) {
            var.setValue(date);
        } else {
            this.item.addVariable("date", date);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
