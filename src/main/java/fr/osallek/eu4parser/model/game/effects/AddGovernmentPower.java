package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

public class AddGovernmentPower {

    private final ClausewitzItem item;

    public AddGovernmentPower(ClausewitzItem item) {
        this.item = item;
    }

    public Power getWhich() {
        return Power.byName(this.item.getVarAsString("which"));
    }

    public String getGovernmentMechanic() {
        return this.item.getVarAsString("government_mechanic");
    }

    public Integer getAmount() {
        return this.item.getVarAsInt("amount");
    }
}
