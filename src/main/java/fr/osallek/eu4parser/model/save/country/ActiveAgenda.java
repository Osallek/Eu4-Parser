package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public record ActiveAgenda(ClausewitzItem item, SaveCountry country) {

    public String getAgenda() {
        return this.item.getVarAsString("agenda");
    }

    public void setAgenda(String agenda) {
        this.item.setVariable("agenda", ClausewitzUtils.addQuotes(agenda));
    }

    public SaveEstate getEstate() {
        return this.country.getEstate(ClausewitzUtils.removeQuotes(this.item.getVarAsString("estate")));
    }

    public void setEstate(SaveEstate estate) {
        this.item.setVariable("estate", ClausewitzUtils.addQuotes(estate.getType()));
    }

    public LocalDate getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }
}
