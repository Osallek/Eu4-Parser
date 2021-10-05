package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Institution implements Comparable<Institution> {

    private final ClausewitzItem item;

    private int index;

    public Institution(ClausewitzItem item, int index) {
        this.item = item;
        this.index = index;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Double getPenalty() {
        return this.item.getVarAsDouble("penalty");
    }

    public void setPenalty(Double penalty) {
        if (penalty == null) {
            this.item.removeVariable("penalty");
        } else {
            this.item.setVariable("penalty", penalty);
        }
    }

    public Modifiers getBonuses() {
        return new Modifiers(this.item.getChild("bonus"));
    }

    public Double getTradeCompanyEfficiency() {
        return this.item.getVarAsDouble("trade_company_efficiency");
    }

    public void setTradeCompanyEfficiency(Double tradeCompanyEfficiency) {
        if (tradeCompanyEfficiency == null) {
            this.item.removeVariable("trade_company_efficiency");
        } else {
            this.item.setVariable("trade_company_efficiency", tradeCompanyEfficiency);
        }
    }

    public Integer getHistoricalStartProvince() {
        return this.item.getVarAsInt("historical_start_province");
    }

    public void setHistoricalStartProvince(Integer historicalStartProvince) {
        if (historicalStartProvince == null) {
            this.item.removeVariable("historical_start_province");
        } else {
            this.item.setVariable("historical_start_province", historicalStartProvince);
        }
    }

    public LocalDate getHistoricalStartDate() {
        return this.item.getVarAsDate("historical_start_date");
    }

    public void setHistoricalStartDate(LocalDate historicalStartDate) {
        if (historicalStartDate == null) {
            this.item.removeVariable("historical_start_date");
        } else {
            this.item.setVariable("historical_start_date", historicalStartDate);
        }
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
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
