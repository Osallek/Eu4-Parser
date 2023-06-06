package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public record HreIncident(ClausewitzItem item) {

    public Optional<String> getName() {
        return this.item.getVarAsString("incident");
    }

    public Optional<LocalDate> getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate date) {
        this.item.setVariable("expiry_date", date);
    }

    public Optional<Integer> getOption() {
        return this.item.getVarAsInt("option");
    }

    public void setOption(int option) {
        this.item.setVariable("option", option);
    }
}
