package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record ConquerProv(ClausewitzItem item) {

    public Optional<Integer> getId() {
        return this.item.getVarAsInt("id");
    }

    public Optional<Integer> getValue() {
        return this.item.getVarAsInt("value");
    }
}
