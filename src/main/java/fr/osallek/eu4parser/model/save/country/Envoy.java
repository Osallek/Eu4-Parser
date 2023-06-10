package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record Envoy(ClausewitzItem item) {

    public Optional<Integer> getType() {
        return this.item.getVarAsInt("type");
    }

    public Optional<Integer> getAction() {
        return this.item.getVarAsInt("action");
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<Integer> getId() {
        return this.item.getVarAsInt("id");
    }
}
