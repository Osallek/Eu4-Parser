package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class ListOfDoubles {

    private final ClausewitzItem item;

    public ListOfDoubles(ClausewitzItem item) {
        this.item = item;
    }

    public Double get(String name) {
        return this.item.getVarAsDouble(name);
    }

    public void set(String name, Double aDouble) {
        ClausewitzVariable var = this.item.getVar(name);

        if (var != null) {
            var.setValue(aDouble);
        } else {
            this.item.addVariable(name, aDouble);
        }
    }
}
