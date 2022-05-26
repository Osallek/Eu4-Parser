package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record ConquerProv(ClausewitzItem item) {

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
