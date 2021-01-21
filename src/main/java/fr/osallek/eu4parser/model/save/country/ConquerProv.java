package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ConquerProv {

    private final ClausewitzItem item;

    public ConquerProv(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
