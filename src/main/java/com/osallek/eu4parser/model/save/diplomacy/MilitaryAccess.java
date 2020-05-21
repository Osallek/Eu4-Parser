package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class MilitaryAccess extends DatableRelation {

    public MilitaryAccess(ClausewitzItem item) {
        super(item);
    }

    public boolean enforcePeace() {
        return this.item.getVarAsBool("enforce_peace");
    }

    public void setEnforcePeace(boolean enforcePeace) {
        this.item.setVariable("enforce_peace", enforcePeace);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, Date startDate, boolean enforcePeace) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, name, first, second, startDate);
        toItem.addVariable("enforce_peace", enforcePeace);

        return toItem;
    }
}
