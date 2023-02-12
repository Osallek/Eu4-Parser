package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ChangePrice {

    private final ClausewitzItem item;

    public ChangePrice(ClausewitzItem item) {
        this.item = item;
    }

    public String getTradeGood() {
        return this.item.getVarAsString("trade_goods");
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public Double getValue() {
        return this.item.getVarAsDouble("value");
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }
}
