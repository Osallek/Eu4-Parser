package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class EstateInfluenceModifier {

    private final ClausewitzItem item;

    public EstateInfluenceModifier(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Double value, String desc, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "influence_modifier", parent.getOrder() + 1);
        toItem.addVariable("value", value);
        toItem.addVariable("date", date);
        toItem.addVariable("desc", desc);

        parent.addChild(toItem);

        return toItem;
    }
}
