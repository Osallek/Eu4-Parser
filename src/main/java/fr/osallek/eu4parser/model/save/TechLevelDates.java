package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.Power;

import java.time.LocalDate;

public record TechLevelDates(ClausewitzItem item) {

    public LocalDate getTechDate(Power power) {
        return this.item.getVarAsDate(power.ordinal());
    }

    public Integer getTechLevel(Power power) {
        return NumbersUtils.toInt(ClausewitzUtils.removeQuotes(this.item.getVar(power.ordinal()).getName()));
    }

    public void setTechDate(Power power, LocalDate date) {
        this.item.setVariable(power.ordinal(), date, true);
    }

    public void setTechLevel(Power power, int level) {
        if (level < 1) {
            level = 1;
        }

        this.item.setVariableName(power.ordinal(), ClausewitzUtils.addQuotes(String.valueOf(level)));
    }
}
