package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class Id {

    private final ClausewitzItem item;

    public Id(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public void setId(Integer id) {
        ClausewitzVariable var = this.item.getVar("id");

        if (var != null) {
            var.setValue(id);
        } else {
            this.item.addVariable("id", id);
        }
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public void setType(Integer type) {
        ClausewitzVariable typeVar = this.item.getVar("type");

        if (typeVar != null) {
            typeVar.setValue(type);
        } else {
            this.item.addVariable("type", type);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Integer id, Integer type) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "id", parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("type", type);

        parent.addChild(toItem);

        return toItem;
    }
}
