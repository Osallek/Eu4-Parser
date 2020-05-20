package com.osallek.eu4parser.model.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class TransferTradePower extends QuantifyDatableRelation {

    public TransferTradePower(ClausewitzItem item) {
        super(item);
    }

    public boolean isEnforced() {
        return this.item.getVarAsBool("is_enforced");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, double amount, boolean isEnforced) {
        if (amount < 0d) {
            amount = 0d;
        } else if (amount > 100d) {
            amount = 100d;
        }

        ClausewitzItem toItem = QuantifyDatableRelation.addToItem(parent, "subsidies", first, second, startDate, amount);
        toItem.addVariable("is_enforced", isEnforced);

        return toItem;
    }
}
