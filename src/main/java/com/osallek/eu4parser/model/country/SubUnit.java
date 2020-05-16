package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Unit;

public class SubUnit {

    private final ClausewitzItem item;

    public SubUnit(ClausewitzItem item) {
        this.item = item;
    }

    public String getSubUnit(Unit unit) {
        return this.item.getVarAsString(unit.name().toLowerCase());
    }

    public void setSubUnit(Unit unit, String subUnit) {
        ClausewitzVariable var = this.item.getVar(unit.name().toLowerCase());
        subUnit = ClausewitzUtils.addQuotes(subUnit);

        if (var != null) {
            var.setValue(subUnit);
        } else {
            this.item.addVariable(unit.name().toLowerCase(), subUnit);
        }
    }
}
