package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Institution {

    private final ClausewitzItem item;

    public Institution(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Double getPenalty() {
        return this.item.getVarAsDouble("penalty");
    }

    public void setPenalty(double penalty) {
        this.item.setVariable("penalty", penalty);
    }

    public Map<String, Double> getBonuses() {
        ClausewitzItem bonusesItem = this.item.getChild("bonus");

        if (bonusesItem != null) {
            return bonusesItem.getVariables()
                              .stream()
                              .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                        ClausewitzVariable::getAsDouble,
                                                        (a, b) -> b,
                                                        LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addBonus(String bonus, Double quantity) {
        ClausewitzItem bonusesItem = this.item.getChild("bonus");

        if (bonusesItem != null) {
            bonusesItem.setVariable(bonus, quantity);
        }
    }

    public void removeBonus(String bonus) {
        ClausewitzItem bonusesItem = this.item.getChild("bonus");

        if (bonusesItem != null) {
            bonusesItem.removeVariable(bonus);
        }
    }

    public Double getTradeCompanyEfficiency() {
        return this.item.getVarAsDouble("trade_company_efficiency");
    }

    public void setTradeCompanyEfficiency(double tradeCompanyEfficiency) {
        this.item.setVariable("trade_company_efficiency", tradeCompanyEfficiency);
    }

    public Date getHistoricalStartDate() {
        return this.item.getVarAsDate("historical_start_date");
    }

    public void setHistoricalStartDate(Date historicalStartDate) {
        this.item.setVariable("historical_start_date", historicalStartDate);
    }

    public Integer getHistoricalStartProvince() {
        return this.item.getVarAsInt("historical_start_province");
    }

    public void setHistoricalStartProvince(int historicalStartProvince) {
        this.item.setVariable("historical_start_province", historicalStartProvince);
    }
}
