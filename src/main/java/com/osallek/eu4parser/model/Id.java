package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Id {

    private final ClausewitzItem item;

    public Id(ClausewitzItem item) {
        this.item = item;
    }

    public Long getId() {
        return this.item.getVarAsLong("id");
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, long id, int type) {
        return addToItem(parent, "id", id, type);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, long id, int type) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("type", type);

        parent.addChild(toItem);

        return toItem;
    }
}
