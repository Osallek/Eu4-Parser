package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Institution implements Comparable<Institution> {

    private final String name;

    private String localizedName;

    private final int index;

    private final Double penalty;

    private final Map<String, String> bonus;

    private final Double tradeCompanyEfficiency;

    private final Date historicalStartDate;

    private final Integer historicalStartProvince;

    public Institution(ClausewitzItem item, int index) {
        this.name = item.getName();
        this.index = index;
        this.penalty = item.getVarAsDouble("penalty");
        this.tradeCompanyEfficiency = item.getVarAsDouble("trade_company_efficiency");
        ClausewitzItem child = item.getChild("bonus");
        this.bonus = child == null ? null : child.getVariables()
                                                 .stream()
                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                           ClausewitzVariable::getValue,
                                                                           (a, b) -> b,
                                                                           LinkedHashMap::new));
        this.historicalStartDate = item.getVarAsDate("historical_start_date");
        this.historicalStartProvince = item.getVarAsInt("historical_start_province");
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public int getIndex() {
        return index;
    }

    public Double getPenalty() {
        return this.penalty;
    }

    public Map<String, String> getBonuses() {
        return this.bonus == null ? new LinkedHashMap<>() : this.bonus;
    }

    public Double getTradeCompanyEfficiency() {
        return this.tradeCompanyEfficiency;
    }

    public Date getHistoricalStartDate() {
        return this.historicalStartDate;
    }

    public Integer getHistoricalStartProvince() {
        return this.historicalStartProvince;
    }

    @Override
    public int compareTo(@NotNull Institution o) {
        return Comparator.comparingInt(Institution::getIndex).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Institution)) {
            return false;
        }

        Institution that = (Institution) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
