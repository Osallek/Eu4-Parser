package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public record ActiveAgenda(ClausewitzItem item, SaveCountry country) {

    public Optional<String> getAgenda() {
        return this.item.getVarAsString("agenda");
    }

    public void setAgenda(String agenda) {
        this.item.setVariable("agenda", ClausewitzUtils.addQuotes(agenda));
    }

    public Optional<SaveEstate> getEstate() {
        return this.item.getVarAsString("estate").map(this.country::getEstate);
    }

    public void setEstate(SaveEstate estate) {
        this.item.setVariable("estate", ClausewitzUtils.addQuotes(estate.getType()));
    }

    public Optional<LocalDate> getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }
}
