package fr.osallek.eu4parser.model.save.changeprices;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.TradeGood;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class ChangePriceGood {

    private final ClausewitzItem item;

    private final TradeGood tradeGood;

    public ChangePriceGood(ClausewitzItem item, Game game) {
        this.item = item;
        this.tradeGood = game.getTradeGood(getName());

        if (isValid()) {
            setCurrentPrice();
        }
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

    public void removeChangePrince(int id) {
        this.item.removeChild(id);

        if(isValid()) {
            setCurrentPrice();
        }
    }

    public ChangePrice addChangePrice(String key, int percent, LocalDate expiryDate) {
        ChangePrice.addToItem(this.item, key, percent, expiryDate);
        return getChangePrices().stream()
                                .filter(changePrice -> key.equalsIgnoreCase(changePrice.getKey()))
                                .findFirst()
                                .orElse(null);
    }

    public void setChangePrices(List<ChangePrice> changePrices) {
        changePrices.removeIf(changePrice -> getChangePrices().contains(changePrice));
        changePrices.forEach(changePrice -> addChangePrice(changePrice.getKey(), changePrice.getValue(), changePrice.getExpiryDate()));

        if(isValid()) {
            setCurrentPrice();
        }
    }

    public List<ChangePrice> getChangePrices() {
        return this.item.getChildren("change_price").stream().map(ChangePrice::new).toList();
    }

    private void setCurrentPrice() {
        double modifiersSum = getChangePrices().stream().mapToDouble(ChangePrice::getValue).sum();

        BigDecimal newPrice = BigDecimal.valueOf(getBasicPrice())
                                        .multiply(BigDecimal.valueOf(100).add(BigDecimal.valueOf(modifiersSum)))
                                        .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_EVEN)
                                        .setScale(3, RoundingMode.HALF_EVEN);

        this.item.setVariable("current_price", newPrice.doubleValue());
    }

}
