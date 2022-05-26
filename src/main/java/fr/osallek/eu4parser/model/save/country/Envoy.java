package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record Envoy(ClausewitzItem item) {

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public Integer getAction() {
        return this.item.getVarAsInt("action");
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }
}
