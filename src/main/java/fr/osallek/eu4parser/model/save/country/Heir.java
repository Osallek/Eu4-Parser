package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Heir extends Monarch {

    public Heir(ClausewitzItem item, Save save, Country country) {
        super(item, save, country);
    }

    public Heir(ClausewitzItem item, Save save, Country country, LocalDate date) {
        super(item, save, country, date);
    }

    public Integer getClaim() {
        return this.item.getVarAsInt("claim");
    }

    public void setClaim(int claim) {
        if (claim < 0) {
            claim = 0;
        } else if (claim > 100) {
            claim = 100;
        }

        this.item.setVariable("claim", claim);
    }
}
