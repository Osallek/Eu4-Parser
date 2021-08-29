package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Heir extends Monarch {

    public Heir(ClausewitzItem item, Save save, SaveCountry country) {
        super(item, save, country);
    }

    public Heir(ClausewitzItem item, Save save, SaveCountry country, LocalDate date) {
        super(item, save, country, date);
    }

    public Double getClaim() {
        return this.item.getVarAsDouble("claim");
    }

    public void setClaim(Double claim) {
        if (claim < 0) {
            claim = 0d;
        } else if (claim > 100) {
            claim = 100d;
        }

        this.item.setVariable("claim", claim);
    }
}
