package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Unit;

public class SubUnit {

    private final ClausewitzItem item;

    public SubUnit(ClausewitzItem item) {
        this.item = item;
    }

    public String getSubUnit(Unit unit) {
        return this.item.getVarAsString(unit.name().toLowerCase());
    }

    public void setSubUnit(Unit unit, String subUnit) {
        this.item.setVariable(unit.name().toLowerCase(), ClausewitzUtils.addQuotes(subUnit));
    }
}
