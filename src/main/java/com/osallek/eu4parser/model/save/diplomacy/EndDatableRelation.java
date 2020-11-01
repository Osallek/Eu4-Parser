package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Date;

public class EndDatableRelation extends DatableRelation {

    public EndDatableRelation(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public LocalDate getEndDate() {
        return this.item.getVarAsDate("end_date");
    }

    public void setEndDate(LocalDate endDate) {
        this.item.setVariable("end_date", endDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate, LocalDate endDate) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "casus_belli", first, second, startDate);

        if (endDate != null) {
            toItem.addVariable("end_date", endDate);
        }

        return toItem;
    }
}
