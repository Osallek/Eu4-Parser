package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record HistoryStatsCache(ClausewitzItem item) {

    public Integer getStartingNumOfStates() {
        return this.item.getVarAsInt("starting_num_of_states");
    }

    public void setStartingNumOfStates(int startingNumOfStates) {
        this.item.setVariable("starting_num_of_states", startingNumOfStates);
    }

    public Double getStartingDevelopment() {
        return this.item.getVarAsDouble("starting_development");
    }

    public void setStartingDevelopment(Double startingDevelopment) {
        this.item.setVariable("starting_development", startingDevelopment);
    }

    public Double getStartingIncome() {
        return this.item.getVarAsDouble("starting_income");
    }

    public void setStartingIcome(Double startingIncome) {
        this.item.setVariable("starting_income", startingIncome);
    }
}
