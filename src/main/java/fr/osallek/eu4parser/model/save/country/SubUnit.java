package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.UnitType;

import java.util.Optional;

public record SubUnit(ClausewitzItem item) {

    public Optional<String> getSubUnit(UnitType unit) {
        return this.item.getVarAsString(unit.name().toLowerCase());
    }

    public void setSubUnit(UnitType unit, String subUnit) {
        this.item.setVariable(unit.name().toLowerCase(), ClausewitzUtils.addQuotes(subUnit));
    }
}
