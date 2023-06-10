package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public record HasDeclaredWar(ClausewitzItem item) {

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public Optional<String> getWar() {
        return this.item.getVarAsString("war");
    }
}
