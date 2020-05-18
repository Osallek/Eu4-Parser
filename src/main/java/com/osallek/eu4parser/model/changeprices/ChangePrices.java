package com.osallek.eu4parser.model.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Good;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangePrices {

    private final ClausewitzItem item;

    private Map<Good, ChangePriceGood> goods;

    public ChangePrices(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public ChangePriceGood getGood(Good good) {
        return this.goods.get(good);
    }

    public Double getCurrentPrice(Good good) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            return changePriceGood.getCurrentPrice();
        }

        return null;
    }

    public Map<Good, ChangePriceGood> getGoods() {
        return goods;
    }

    public void setCurrentPrice(Good good, double newPrice) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.setCurrentPrice(newPrice);
        }
    }

    public void addChangePrice(Good good, String key, int percent, Date expiryDate) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.addChangePrice(key, percent, expiryDate);
        }
    }

    public void removeChangePrince(Good good, int id) {
        ChangePriceGood changePriceGood = this.goods.get(good);

        if (changePriceGood != null) {
            changePriceGood.removeChangePrince(id);
        }
    }


    private void refreshAttributes() {
        this.goods = this.item.getChildren()
                              .stream()
                              .map(ChangePriceGood::new)
                              .collect(Collectors.toMap(changePriceGood -> Good.valueOf(changePriceGood.getName()
                                                                                                       .toUpperCase()),
                                                        Function.identity(),
                                                        (e1, e2) -> e1,
                                                        () -> new EnumMap<>(Good.class)));
    }
}
