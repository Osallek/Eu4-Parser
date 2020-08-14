package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Institution {

    private String name;

    private String localizedName;

    private Double penalty;

    private Map<String, Double> bonus;

    private Double tradeCompanyEfficiency;

    private Date historicalStartDate;

    private Integer historicalStartProvince;

    public Institution(ClausewitzItem item) {
        this.name = item.getName();
        this.penalty = item.getVarAsDouble("penalty");
        this.tradeCompanyEfficiency = item.getVarAsDouble("trade_company_efficiency");
        ClausewitzItem child = item.getChild("bonus");
        this.bonus = child == null ? null : child.getVariables()
                                                 .stream()
                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                           ClausewitzVariable::getAsDouble,
                                                                           (a, b) -> b,
                                                                           LinkedHashMap::new));
        this.historicalStartDate = item.getVarAsDate("historical_start_date");
        this.historicalStartProvince = item.getVarAsInt("historical_start_province");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Double getPenalty() {
        return this.penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public Map<String, Double> getBonuses() {
        return this.bonus == null ? new LinkedHashMap<>() : this.bonus;
    }

    public void addBonus(String bonus, Double quantity) {
        if (this.bonus == null) {
            this.bonus = new LinkedHashMap<>();
        }

        this.bonus.put(bonus, quantity);
    }

    public void removeBonus(String bonus) {
        if (this.bonus != null) {
            this.bonus.remove(bonus);
        }
    }

    public Double getTradeCompanyEfficiency() {
        return this.tradeCompanyEfficiency;
    }

    public void setTradeCompanyEfficiency(double tradeCompanyEfficiency) {
        this.tradeCompanyEfficiency = tradeCompanyEfficiency;
    }

    public Date getHistoricalStartDate() {
        return this.historicalStartDate;
    }

    public void setHistoricalStartDate(Date historicalStartDate) {
        this.historicalStartDate = historicalStartDate;
    }

    public Integer getHistoricalStartProvince() {
        return this.historicalStartProvince;
    }

    public void setHistoricalStartProvince(int historicalStartProvince) {
        this.historicalStartProvince = historicalStartProvince;
    }
}
