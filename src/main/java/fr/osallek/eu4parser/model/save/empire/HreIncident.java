package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public class HreIncident {

    private final ClausewitzItem item;

    public HreIncident(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("incident");
    }

    public LocalDate getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate date) {
        this.item.setVariable("expiry_date", date);
    }

    public Integer getOption() {
        return this.item.getVarAsInt("option");
    }

    public void setOption(int option) {
        this.item.setVariable("option", option);
    }
}
