package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class HistoryStatsCache {

    private final ClausewitzItem item;

    public HistoryStatsCache(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getStartingNumOfStates() {
        return this.item.getVarAsInt("starting_num_of_states");
    }

    public void setStartingNumOfStates(int startingNumOfStates) {
        ClausewitzVariable var = this.item.getVar("starting_num_of_states");

        if (var != null) {
            var.setValue(startingNumOfStates);
        } else {
            this.item.addVariable("starting_num_of_states", startingNumOfStates);
        }
    }

    public Double getStartingDevelopment() {
        return this.item.getVarAsDouble("starting_development");
    }

    public void setStartingDevelopment(Double startingDevelopment) {
        ClausewitzVariable var = this.item.getVar("starting_development");

        if (var != null) {
            var.setValue(startingDevelopment);
        } else {
            this.item.addVariable("starting_development", startingDevelopment);
        }
    }

    public Double getStartingIncome() {
        return this.item.getVarAsDouble("starting_income");
    }

    public void setStartingIcome(Double startingIncome) {
        ClausewitzVariable var = this.item.getVar("starting_income");

        if (var != null) {
            var.setValue(startingIncome);
        } else {
            this.item.addVariable("starting_income", startingIncome);
        }
    }
}
