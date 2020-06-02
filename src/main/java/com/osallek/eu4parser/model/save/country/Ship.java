package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Id;

public class Ship {

    protected final ClausewitzItem item;

    private Id id;

    private Id lastTarget;

    public Ship(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        ClausewitzVariable var = this.item.getVar("name");
        name = ClausewitzUtils.addQuotes(name);

        if (var != null) {
            var.setValue(name);
        } else {
            this.item.addVariable("name", name);
        }
    }

    public Integer getHome() {
        return this.item.getVarAsInt("home");
    }

    public void setHome(Integer home) {
        ClausewitzVariable var = this.item.getVar("home");

        if (var != null) {
            var.setValue(home);
        } else {
            this.item.addVariable("home", home);
        }
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        ClausewitzVariable var = this.item.getVar("type");
        type = ClausewitzUtils.addQuotes(type);

        if (var != null) {
            var.setValue(type);
        } else {
            this.item.addVariable("type", type);
        }
    }

    public Double getMorale() {
        return this.item.getLastVarAsDouble("morale");
    }

    public void setMorale(Double morale) {
        ClausewitzVariable var = this.item.getVar("morale");

        if (morale < 0d) {
            morale = 0d;
        }

        if (var != null) {
            var.setValue(morale);
        } else {
            this.item.addVariable("morale", morale);
        }
    }

    public Id getLastTarget() {
        return lastTarget;
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int home, String type, double morale) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "ship", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);

        parent.addChild(toItem);

        return toItem;
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }

        ClausewitzItem lastTargetItem = this.item.getChild("last_target");

        if (lastTargetItem != null) {
            this.lastTarget = new Id(lastTargetItem);
        }
    }
}
