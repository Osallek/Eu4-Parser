package com.osallek.eu4parser.model.save.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChangePriceGood {

    private final ClausewitzItem item;

    private List<ChangePrice> changePrices;

    public ChangePriceGood(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getName();
    }

    public Double getCurrentPrice() {
        return this.item.getVarAsDouble("current_price");
    }

    public void setCurrentPrice(double newPrice) {
        ClausewitzVariable currentPriceVar = this.item.getVar("current_price");

        if (currentPriceVar != null) {
            currentPriceVar.setValue(newPrice);
        }
    }

    public void removeChangePrince(int id) {
        this.item.removeChild(id);
        refreshAttributes();
    }

    public void addChangePrice(String key, int percent, Date expiryDate) {
        ChangePrice.addToItem(this.item, key, percent, expiryDate);
        refreshAttributes();
    }

    public List<ChangePrice> getChangePrices() {
        return changePrices;
    }

    private void refreshAttributes() {
        this.changePrices = this.item.getChildren("change_price").stream().map(ChangePrice::new).collect(Collectors.toList());
    }
}
