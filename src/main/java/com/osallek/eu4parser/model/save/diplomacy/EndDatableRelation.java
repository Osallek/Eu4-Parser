package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class EndDatableRelation extends DatableRelation {

    public EndDatableRelation(ClausewitzItem item) {
        super(item);
    }

    public Date getEndDate() {
        return this.item.getVarAsDate("end_date");
    }

    public void setEndDate(Date endDate) {
        this.item.setVariable("end_date", endDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, Date startDate, Date endDate) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "casus_belli", first, second, startDate);

        if (endDate != null) {
            toItem.addVariable("end_date", endDate);
        }

        return toItem;
    }
}
