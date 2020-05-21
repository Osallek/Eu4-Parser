package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class Edit {

    private final ClausewitzItem item;

    public Edit(ClausewitzItem item) {
        this.item = item;
    }

    public String getWhich() {
        return this.item.getVarAsString("which");
    }

    public void setWhich(String which) {
        this.item.setVariable("which", ClausewitzUtils.addQuotes(which));
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String which, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "active_edict", parent.getOrder() + 1);
        toItem.addVariable("which", ClausewitzUtils.addQuotes(which));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
