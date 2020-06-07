package com.osallek.eu4parser.model.save.changeprices;

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

    public Map<String, ChangePriceGood> getGoods() {
        return goods;
    }

    public ChangePriceGood getGood(String good) {
        return this.goods.get(good);
    }

    public ChangePriceGood getGood(int index) {
        return this.goods.values().toArray(new ChangePriceGood[] {})[index];
    }

    public Double getCurrentPrice(String good) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            return changePriceGood.getCurrentPrice();
        }

        return null;
    }

    public void setCurrentPrice(String good, double newPrice) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.setCurrentPrice(newPrice);
        }
    }

    public void addChangePrice(String good, String key, int percent, Date expiryDate) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.addChangePrice(key, percent, expiryDate);
        }
    }

    public void removeChangePrince(String good, int id) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.removeChangePrince(id);
        }
    }


    private void refreshAttributes() {
        this.goods = this.item.getChildren()
                              .stream()
                              .map(ChangePriceGood::new)
                              .collect(Collectors.toMap(ChangePriceGood::getName,
                                                        Function.identity(),
                                                        (changePriceGood, changePriceGood2) -> changePriceGood,
                                                        LinkedHashMap::new));
    }
}
