package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public record HasDeclaredWar(ClausewitzItem item) {

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public String getWar() {
        return this.item.getVarAsString("war");
    }
}
