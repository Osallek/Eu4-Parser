package com.osallek.eu4parser.model.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Id;

public class Cardinal {

    private final ClausewitzItem item;

    private Id id;

    public Cardinal(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(Integer provinceId) {
        ClausewitzVariable locationVar = this.item.getVar("location");

        if (locationVar != null) {
            locationVar.setValue(provinceId);
        } else {
            this.item.addVariable("location", provinceId);
        }
    }

    public Id getId() {
        return id;
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Integer id, Integer location) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "cardinal", parent.getOrder() + 1);
        toItem.addVariable("location", location);
        Id.addToItem(toItem, id, 4713);

        parent.addChild(toItem);

        return toItem;
    }
}
