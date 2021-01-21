package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.UnitType;

public class SubUnit {

    private final ClausewitzItem item;

    public SubUnit(ClausewitzItem item) {
        this.item = item;
    }

    public String getSubUnit(UnitType unit) {
        return this.item.getVarAsString(unit.name().toLowerCase());
    }

    public void setSubUnit(UnitType unit, String subUnit) {
        this.item.setVariable(unit.name().toLowerCase(), ClausewitzUtils.addQuotes(subUnit));
    }
}
