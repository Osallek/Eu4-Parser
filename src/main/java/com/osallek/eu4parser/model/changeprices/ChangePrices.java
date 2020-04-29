package com.osallek.eu4parser.model.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangePrices {

    private final ClausewitzItem item;

    private Map<String, ChangePriceGood> goods;

    public ChangePrices(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public ChangePriceGood getGood(String name) {
        return this.goods.get(name);
    }

    public Double getCurrentPrice(String name) {
        ChangePriceGood changePriceGood = this.goods.get(name);

        if (changePriceGood != null) {
            return changePriceGood.getCurrentPrice();
        }

        return null;
    }

    public Map<String, ChangePriceGood> getGoods() {
        return goods;
    }

    public void setCurrentPrice(String name, double newPrice) {
        ChangePriceGood changePriceGood = this.goods.get(name);

        if (changePriceGood != null) {
            changePriceGood.setCurrentPrice(newPrice);
        }
    }

    public void addChangePrice(String name, String key, int percent, Date expiryDate) {
        ChangePriceGood changePriceGood = this.goods.get(name);

        if (changePriceGood != null) {
            changePriceGood.addChangePrice(key, percent, expiryDate);
        }
    }

    public void removeChangePrince(String name, int id) {
        ChangePriceGood changePriceGood = this.goods.get(name);

        if (changePriceGood != null) {
            changePriceGood.removeChangePrince(id);
        }
    }


    private void refreshAttributes() {
        this.goods = this.item.getChildren()
                              .stream()
                              .map(ChangePriceGood::new)
                              .collect(Collectors.toMap(ChangePriceGood::getName, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new));
    }
}
