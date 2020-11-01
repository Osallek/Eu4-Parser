package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public class EstateModifier {

    private final ClausewitzItem item;

    public EstateModifier(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String type, Double value, String desc, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, type, parent.getOrder() + 1);
        toItem.addVariable("value", value);
        toItem.addVariable("date", date);
        toItem.addVariable("desc", desc);

        parent.addChild(toItem);

        return toItem;
    }
}
