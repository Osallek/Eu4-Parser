package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.Optional;

public record TechLevelDates(ClausewitzItem item) {

    public Optional<LocalDate> getTechDate(Power power) {
        return this.item.getVarAsDate(power.ordinal());
    }

    public Optional<Integer> getTechLevel(Power power) {
        return this.item.getVar(power.ordinal()).map(ClausewitzVariable::getName).map(ClausewitzUtils::removeQuotes).map(NumbersUtils::toInt);
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
