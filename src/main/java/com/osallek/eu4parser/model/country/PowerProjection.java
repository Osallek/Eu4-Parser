package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class PowerProjection {

    private final ClausewitzItem item;

    public PowerProjection(ClausewitzItem item) {
        this.item = item;
    }

    public String getTarget() {
        return this.item.getVarAsString("target");
    }

    public String getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public Double getCurrent() {
        return this.item.getVarAsDouble("current");
    }

    public void setCurrent(Double current) {
        ClausewitzVariable var = this.item.getVar("current");

        if (var != null) {
            var.setValue(current);
        } else {
            this.item.addVariable("current", current);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String target, String modifier, Double current) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "power_projection", parent.getOrder() + 1);
        toItem.addVariable("target", target);
        toItem.addVariable("modifier", modifier);
        toItem.addVariable("current", current);

        parent.addChild(toItem);

        return toItem;
    }
}
