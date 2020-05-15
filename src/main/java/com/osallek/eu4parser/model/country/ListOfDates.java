package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class ListOfDates {

    private final ClausewitzItem item;

    public ListOfDates(ClausewitzItem item) {
        this.item = item;
    }

    public Date get(String name) {
        return this.item.getVarAsDate(name);
    }

    public void set(String name, Date date) {
        ClausewitzVariable var = this.item.getVar(name);

        if (var != null) {
            var.setValue(date);
        } else {
            this.item.addVariable(name, date);
        }
    }
}
