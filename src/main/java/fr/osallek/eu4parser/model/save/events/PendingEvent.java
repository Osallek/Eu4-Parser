package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class PendingEvent {

    private final ClausewitzItem item;

    public PendingEvent(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public String getCountry() {
        return this.item.getVarAsString("country");
    }
}
