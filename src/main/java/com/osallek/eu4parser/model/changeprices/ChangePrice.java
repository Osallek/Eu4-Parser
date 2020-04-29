package com.osallek.eu4parser.model.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class ChangePrice {

    private final ClausewitzItem item;

    public ChangePrice(ClausewitzItem item) {
        this.item = item;
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public int getValue() {
        return (int) (this.item.getVarAsDouble("value") * 100);
    }

    public void setValue(int percent) {
        ClausewitzVariable currentPriceVar = this.item.getVar("value");

        if (currentPriceVar != null) {
            currentPriceVar.setValue((double) percent / 100);
        }
    }

    public Date getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(Date expiryDate) {
        ClausewitzVariable currentPriceVar = this.item.getVar("expiry_date");

        if (currentPriceVar != null) {
            currentPriceVar.setValue(expiryDate);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String key, int percent, Date expiryDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "change_price", parent.getOrder() + 1);
        toItem.addVariable("key", key.toUpperCase());
        toItem.addVariable("value", (double) percent / 100);
        toItem.addVariable("expiry_date", expiryDate);

        parent.addChild(toItem);

        return toItem;
    }
}
