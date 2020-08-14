package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class ListOfDoubles {

    private final ClausewitzItem item;

    public ListOfDoubles(ClausewitzItem item) {
        this.item = item;
    }

    public Double get(String name) {
        return this.item.getVarAsDouble(name);
    }

    public void set(String name, Double aDouble) {
        this.item.setVariable(name, aDouble);
    }
}
