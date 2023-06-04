package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record PendingEvent(ClausewitzItem item) {

    public String getName() {
        return this.item.getName();
    }

    public Optional<String> getCountry() {
        return this.item.getVarAsString("country");
    }
}
