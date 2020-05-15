package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Threat {

    private final ClausewitzItem item;

    public Threat(ClausewitzItem item) {
        this.item = item;
    }

    public String getId() {
        return this.item.getVarAsString("id");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
