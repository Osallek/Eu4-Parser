package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Faction {

    private final ClausewitzItem item;

    public Faction(ClausewitzItem item) {
        this.item = item;
    }

    public Double getInfluence() {
        return this.item.getVarAsDouble("influence");
    }

    public void setInfluence(double influence) {
        this.item.setVariable("influence", Math.min(Math.max(0, influence), 100));
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public Double getOldInfluence() {
        return this.item.getVarAsDouble("old_influence");
    }

    public void setOldInfluence(double influence) {
        this.item.setVariable("old_influence", Math.min(Math.max(0, influence), 100));
    }
}
