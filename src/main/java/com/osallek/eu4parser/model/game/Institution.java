package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Institution implements Comparable<Institution> {

    private final String name;

    private String localizedName;

    private final int index;

    private final Double penalty;

    private final Modifiers bonus;

    private final Double tradeCompanyEfficiency;

    private final Date historicalStartDate;

    private final Integer historicalStartProvince;

    public Institution(ClausewitzItem item, int index) {
        this.name = item.getName();
        this.index = index;
        this.penalty = item.getVarAsDouble("penalty");
        this.tradeCompanyEfficiency = item.getVarAsDouble("trade_company_efficiency");
        this.bonus = new Modifiers(item.getChild("bonus"));
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

    public Modifiers getBonuses() {
        return this.bonus;
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
