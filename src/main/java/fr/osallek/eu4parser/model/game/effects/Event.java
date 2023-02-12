package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//country_event, province_event
public class Event {

    private final ClausewitzItem item;

    public Event(ClausewitzItem item) {
        this.item = item;
    }

    public String getId() {
        return this.item.getVarAsString("id");
    }

    public Integer getDays() {
        return this.item.getVarAsInt("days");
    }

    public Integer getRandom() {
        return this.item.getVarAsInt("random");
    }

    public String getTooltip() {
        return this.item.getVarAsString("tooltip");
    }
}
