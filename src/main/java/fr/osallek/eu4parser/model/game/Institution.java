package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Institution implements Comparable<Institution> {

    private final String name;

    private String localizedName;

    private int index;

    private final Double penalty;

    private final Modifiers bonus;

    private final Double tradeCompanyEfficiency;

    private final LocalDate historicalStartDate;

    private final Integer historicalStartProvince;

    public Institution(ClausewitzItem item) {
        this.name = item.getName();
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

    void setIndex(int index) {
        this.index = index;
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

    public LocalDate getHistoricalStartDate() {
        return this.historicalStartDate;
    }

    public Integer getHistoricalStartProvince() {
        return this.historicalStartProvince;
    }

    @Override
    public int compareTo(Institution o) {
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
