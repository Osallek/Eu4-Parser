package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Province {

    private final ClausewitzItem item;

    public Province(ClausewitzItem item) {
        this.item = item;
    }

    public String getOwner() {
        return this.item.getVarAsString("owner");
    }
}
