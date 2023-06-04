package fr.osallek.eu4parser.model.save.changeprices;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.TradeGood;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Optional<Double> getCurrentPrice() {
        return this.item.getVarAsDouble("current_price");
    }

    public Double getBasicPrice() {
        return this.tradeGood.getBasePrice();
    }

    public void removeChangePrince(int id) {
        this.item.removeChild(id);
        refreshAttributes();
    }

    public ChangePrice addChangePrice(String key, int percent, LocalDate expiryDate) {
        ChangePrice.addToItem(this.item, key, percent, expiryDate);
        refreshAttributes();
        return this.changePrices.stream()
                                .filter(changePrice -> changePrice.getKey().isPresent() && key.equalsIgnoreCase(changePrice.getKey().get()))
                                .findFirst()
                                .orElse(null);
    }

    public void setChangePrices(List<ChangePrice> changePrices) {
        changePrices.removeIf(change -> this.changePrices.contains(change));
        changePrices.forEach(change -> addChangePrice(change.getKey().orElse(null), change.getValue(), change.getExpiryDate().orElse(null)));
        refreshAttributes();
    }

    public List<ChangePrice> getChangePrices() {
        return changePrices;
    }

    private void refreshAttributes() {
        this.changePrices = this.item.getChildren("change_price").stream().map(changePriceItem -> new ChangePrice(changePriceItem)).toList();

        if (isValid()) {
            setCurrentPrice();
        }
    }

    private void setCurrentPrice() {
        double modifiersSum = this.changePrices.stream().mapToDouble(ChangePrice::getValue).sum();

        BigDecimal newPrice = BigDecimal.valueOf(getBasicPrice())
                                        .multiply(BigDecimal.valueOf(100).add(BigDecimal.valueOf(modifiersSum)))
                                        .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_EVEN)
                                        .setScale(3, RoundingMode.HALF_EVEN);

        this.item.setVariable("current_price", newPrice.doubleValue());
    }

}
