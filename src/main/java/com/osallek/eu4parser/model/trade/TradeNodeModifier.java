package com.osallek.eu4parser.model.trade;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

public class TradeNodeModifier {

    private final ClausewitzItem item;

    public TradeNodeModifier(ClausewitzItem item) {
        this.item = item;
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public Double getPower() {
        return this.item.getVarAsDouble("power");
    }

    public Double getPowerModifier() {
        return this.item.getVarAsDouble("power_modifier");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String key, int duration, double power, double powerModifier) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "modifier", parent.getOrder() + 1);
        toItem.addVariable("key", ClausewitzUtils.addQuotes(key));
        toItem.addVariable("duration", duration);
        toItem.addVariable("power", power);
        toItem.addVariable("power_modifier", powerModifier);

        parent.addChild(toItem);

        return toItem;
    }
}
