package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public class HasDeclaredWar {

    private final ClausewitzItem item;

    public HasDeclaredWar(ClausewitzItem item) {
        this.item = item;
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public String getWar() {
        return this.item.getVarAsString("war");
    }
}
