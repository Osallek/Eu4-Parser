package com.osallek.eu4parser.model.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class DatableRelation {

    protected final ClausewitzItem item;

    public DatableRelation(ClausewitzItem item) {
        this.item = item;
    }

    public String getFirst() {
        return this.item.getVarAsString("first");
    }

    public String getSecond() {
        return this.item.getVarAsString("second");
    }

    public Date getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(Date startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, Date startDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, parent.getOrder() + 1);
        toItem.addVariable("first", ClausewitzUtils.addQuotes(first));
        toItem.addVariable("second", ClausewitzUtils.addQuotes(second));
        toItem.addVariable("start_date", startDate);

        parent.addChild(toItem);

        return toItem;
    }
}
