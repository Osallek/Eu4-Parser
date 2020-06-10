package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.Date;

public class EstateInteraction {

    private final ClausewitzList list;

    public EstateInteraction(ClausewitzList list) {
        this.list = list;
    }

    public String getName() {
        return this.list.get(0);
    }

    public Date getDate() {
        return this.list.getAsDate(1);
    }

    public void setDate(Date date) {
        this.list.set(1, date);
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, Date date) {
        return parent.addList(null, name, ClausewitzUtils.dateToString(date));
    }
}
