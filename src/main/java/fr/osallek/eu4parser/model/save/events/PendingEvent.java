package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record PendingEvent(ClausewitzItem item) {

    public String getName() {
        return this.item.getName();
    }

    public String getCountry() {
        return this.item.getVarAsString("country");
    }
}
