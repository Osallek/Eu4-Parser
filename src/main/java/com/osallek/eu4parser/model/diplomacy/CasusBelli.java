package com.osallek.eu4parser.model.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class CasusBelli extends EndDatableRelation {

    public CasusBelli(ClausewitzItem item) {
        super(item);
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, Date endDate, String type) {
        ClausewitzItem toItem = EndDatableRelation.addToItem(parent, "casus_belli", first, second, startDate, endDate);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));

        return toItem;
    }
}
