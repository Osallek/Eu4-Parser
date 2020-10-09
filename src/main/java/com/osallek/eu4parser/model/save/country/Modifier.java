package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

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

    public Boolean rulerModifier() {
        return this.item.getVarAsBool("ruler_modifier");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    /**
     * @param date If null set to never expires
     */
    public void setDate(Date date) {
        if (date == null) {
            this.item.setVariable("date", "-1.1.1");
            this.item.setVariable("permanent", true);
        } else {
            this.item.setVariable("date", date);
        }
    }

    public Boolean isPermanent() {
        return this.item.getVarAsBool("permanent");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String modifier, Date date, Boolean hidden) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "modifier", parent.getOrder() + 1);
        toItem.addVariable("modifier", ClausewitzUtils.addQuotes(modifier));
        toItem.addVariable("hidden", hidden);

        if (date == null) {
            toItem.addVariable("date", "-1.1.1");
            toItem.addVariable("permanent", true);
        } else {
            toItem.addVariable("date", date);
        }

        parent.addChild(toItem);

        return toItem;
    }
}
