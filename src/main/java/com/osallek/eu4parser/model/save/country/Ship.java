package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.Save;

public class Ship extends AbstractRegiment {

    private FlagShip flagShip;

    public Ship(ClausewitzItem item, Save save) {
        super(item, save);
        refreshAttributes();
    }

    public Boolean hasDisengaged() {
        return this.item.getVarAsBool("has_disengaged");
    }

    public void setHasDisengaged(boolean hasDisengaged) {
        this.item.setVariable("has_disengaged", hasDisengaged);
    }

    public FlagShip getFlagShip() {
        return flagShip;
    }

    public void setFlagShip(FlagShip flagShip) {
        this.flagShip = flagShip;
    }

    private void refreshAttributes() {
        ClausewitzItem flagShipItem = this.item.getChild("flagship");

        if (flagShipItem != null) {
            this.flagShip = new FlagShip(flagShipItem, this.save);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int home, String type, double morale, boolean hasDisengaged) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "regiment", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);
        toItem.addVariable("has_disengaged", hasDisengaged);

        parent.addChild(toItem);

        return toItem;
    }
}
