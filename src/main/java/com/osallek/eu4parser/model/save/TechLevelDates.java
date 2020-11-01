package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.Date;

public class TechLevelDates {

    private final ClausewitzItem item;

    public TechLevelDates(ClausewitzItem item) {
        this.item = item;
    }

    public LocalDate getTechDate(Power power) {
        return this.item.getVarAsDate(power.ordinal());
    }

    public Integer getTechLevel(Power power) {
        return this.item.getVarAsInt(power.ordinal());
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
