package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class HasDeclaredWar {

    private final ClausewitzItem item;

    public HasDeclaredWar(ClausewitzItem item) {
        this.item = item;
    }

    public Date getId() {
        return this.item.getVarAsDate("date");
    }

    public String getWar() {
        return this.item.getVarAsString("war");
    }
}
