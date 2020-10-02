package com.osallek.eu4parser.model.save.changeprices;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.TradeGood;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChangePriceGood {

    private final ClausewitzItem item;

    private final Game game;

    private final TradeGood tradeGood;

    private List<ChangePrice> changePrices;

    public ChangePriceGood(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
        this.tradeGood = this.game.getTradeGood(getName());
        refreshAttributes();
    }

    public boolean isValid() {
        return this.tradeGood != null;
    }

    public String getName() {
        return this.item.getName();
    }

    public Double getCurrentPrice() {
        return this.item.getVarAsDouble("current_price");
    }

    public Double getBasicPrice() {
        return this.tradeGood.getBasePrice();
    }

    public String getLocalizedName() {
        return isValid() ? this.tradeGood.getLocalizedName() : getName();
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
        changePrices.forEach(changePrice -> addChangePrice(changePrice.getKey(), changePrice.getValue(),
                                                           changePrice.getExpiryDate()));
        refreshAttributes();
    }

    public List<ChangePrice> getChangePrices() {
        return changePrices;
    }

    private void refreshAttributes() {
        this.changePrices = this.item.getChildren("change_price")
                                     .stream()
                                     .map(changePriceItem -> new ChangePrice(changePriceItem, this.game))
                                     .collect(Collectors.toList());

        if (isValid()) {
            setCurrentPrice();
        }
    }

    private void setCurrentPrice() {
        double modifiersSum = this.changePrices.stream()
                                               .mapToDouble(ChangePrice::getValue)
                                               .sum();

        BigDecimal newPrice = BigDecimal.valueOf(getBasicPrice())
                                        .multiply(BigDecimal.valueOf(100).add(BigDecimal.valueOf(modifiersSum)))
                                        .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_EVEN)
                                        .setScale(3, RoundingMode.HALF_EVEN);

        this.item.setVariable("current_price", newPrice.doubleValue());
    }

}
