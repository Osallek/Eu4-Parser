package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class OldEmperor {

    private final ClausewitzItem item;

    public OldEmperor(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public String getCountry() {
        return this.item.getVarAsString("country");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setCountry(String tag) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(tag.toUpperCase()));
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String id, String country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "old_emperor", parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
