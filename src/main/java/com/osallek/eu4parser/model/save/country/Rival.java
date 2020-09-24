package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;

public class Rival {

    private final Save save;

    private final ClausewitzItem item;

    public Rival(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getRivalTag() {
        return this.item.getVarAsString("country");
    }

    public Country getRival() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getRivalTag()));
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
