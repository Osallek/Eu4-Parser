package fr.osallek.eu4parser.model.save.changeprices;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangePrices {

    private final ClausewitzItem item;

    private final Game game;

    private Map<String, ChangePriceGood> goods;

    public ChangePrices(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
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

    public void addChangePrice(String good, String key, int percent, LocalDate expiryDate) {
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
                              .map(goodItem -> new ChangePriceGood(goodItem, this.game))
                              .filter(ChangePriceGood::isValid)
                              .collect(Collectors.toMap(ChangePriceGood::getName,
                                                        Function.identity(),
                                                        (changePriceGood, changePriceGood2) -> changePriceGood,
                                                        LinkedHashMap::new));
    }
}
