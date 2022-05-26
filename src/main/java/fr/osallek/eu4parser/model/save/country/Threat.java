package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record Threat(ClausewitzItem item) {

    public String getId() {
        return this.item.getVarAsString("id");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
