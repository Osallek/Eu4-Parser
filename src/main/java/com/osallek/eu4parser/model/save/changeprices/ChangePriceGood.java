package com.osallek.eu4parser.model.save.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.save.province.ProvinceBuilding;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ChangePriceGood {

    private final ClausewitzItem item;

    private final Game game;

    private List<ChangePrice> changePrices;

    public ChangePriceGood(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
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

    public ChangePrice addChangePrice(String key, int percent, Date expiryDate) {
        ChangePrice.addToItem(this.item, key, percent, expiryDate);
        refreshAttributes();
        return this.changePrices.stream()
                                .filter(changePrice -> key.equalsIgnoreCase(changePrice.getKey()))
                                .findFirst()
                                .orElse(null);
    }

    public void setChangePrices(List<ChangePrice> changePrices) {
        changePrices.removeIf(changePrice -> this.changePrices.contains(changePrice));
        changePrices.forEach(changePrice -> addChangePrice(changePrice.getKey(), changePrice.getValue(), changePrice.getExpiryDate()));
    }

    public List<ChangePrice> getChangePrices() {
        return changePrices;
    }

    private void refreshAttributes() {
        this.changePrices = this.item.getChildren("change_price")
                                     .stream()
                                     .map(changePriceItem -> new ChangePrice(changePriceItem, this.game))
                                     .collect(Collectors.toList());
    }
}
