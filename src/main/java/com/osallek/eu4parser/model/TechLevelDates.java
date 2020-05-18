package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class TechLevelDates {

    private final ClausewitzItem item;

    public TechLevelDates(ClausewitzItem item) {
        this.item = item;
    }

    public Date getTechDate(Power power) {
        return this.item.getVarAsDate(power.ordinal());
    }

    public Integer getTechLevel(Power power) {
        ClausewitzVariable variable = this.item.getVar(power.ordinal());

        if (variable != null) {
            return Integer.valueOf(ClausewitzUtils.removeQuotes(variable.getName()));
        }

        return null;
    }

    public void setTechDate(Power power, Date date) {
        this.item.setVariable(power.ordinal(), date, true);
    }

    public void setTechLevel(Power power, int level) {
        if (level < 1) {
            level = 1;
        }

        this.item.setVariableName(power.ordinal(), ClausewitzUtils.addQuotes(String.valueOf(level)));
    }
}
