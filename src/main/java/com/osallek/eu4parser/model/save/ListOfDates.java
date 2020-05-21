package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfDates {

    private final ClausewitzItem item;

    public ListOfDates(ClausewitzItem item) {
        this.item = item;
    }

    public List<String> getNames() {
        return this.item.getVariables().stream().map(ClausewitzVariable::getName).collect(Collectors.toList());
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
