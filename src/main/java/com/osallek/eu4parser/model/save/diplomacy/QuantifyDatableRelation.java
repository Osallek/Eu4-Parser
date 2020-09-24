package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;

public class QuantifyDatableRelation extends DatableRelation {

    public QuantifyDatableRelation(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Double getAmount() {
        return this.item.getVarAsDouble("amount");
    }

    public void setAmount(Double amount) {
        this.item.setVariable("amount", amount);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, Date startDate, double amount) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, name, first, second, startDate);
        toItem.addVariable("amount", amount);

        return toItem;
    }
}
