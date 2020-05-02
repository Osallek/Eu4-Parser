package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class Modifier {

    private final ClausewitzItem item;

    public Modifier(ClausewitzItem item) {
        this.item = item;
    }

    public String getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public Boolean getHidden() {
        return this.item.getVarAsBool("hidden");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    /**
     * @param date If null set to never expires
     */
    public void setDate(Date date) {
        ClausewitzVariable var = this.item.getVar("date");

        if (date == null) {
            if (var != null) {
                var.setValue("-1.1.1");
            } else {
                this.item.addVariable("date", "-1.1.1");
            }
        } else {
            if (var != null) {
                var.setValue(date);
            } else {
                this.item.addVariable("date", date);
            }
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String modifier, Date date, Boolean hidden) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "modifier", parent.getOrder() + 1);
        toItem.addVariable("modifier", ClausewitzUtils.addQuotes(modifier));
        toItem.addVariable("hidden", hidden);

        if (date == null) {
            toItem.addVariable("date", "-1.1.1");
        } else {
            toItem.addVariable("date", date);
        }

        parent.addChild(toItem);

        return toItem;
    }
}
