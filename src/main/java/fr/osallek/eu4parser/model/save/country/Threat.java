package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record Threat(ClausewitzItem item) {

    public Optional<String> getId() {
        return this.item.getVarAsString("id");
    }

    public Optional<Integer> getValue() {
        return this.item.getVarAsInt("value");
    }
}
