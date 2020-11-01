package com.osallek.eu4parser.model.save.changeprices;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;

import java.time.LocalDate;
import java.util.Objects;

public class ChangePrice {

    private final ClausewitzItem item;

    private final String localizedName;

    public ChangePrice(String key, int percent, LocalDate expiryDate) {
        this.item = ChangePrice.addToItem(null, key, percent, expiryDate);
        this.localizedName = key;
    }

    public ChangePrice(ClausewitzItem item, Game game) {
        this.item = item;
        this.localizedName = game.getLocalisationClean(ClausewitzUtils.removeQuotes(getKey()));
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public int getValue() {
        return (int) (this.item.getVarAsDouble("value") * 100);
    }

    public void setValue(int percent) {
        this.item.setVariable("value", (double) percent / 100);
    }

    public LocalDate getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String key, int percent, LocalDate expiryDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "change_price", parent == null ? 0 : parent.getOrder() + 1);
        toItem.addVariable("key", key.toUpperCase());
        toItem.addVariable("value", (double) percent / 100);
        toItem.addVariable("expiry_date", expiryDate);

        if (parent != null) {
            parent.addChild(toItem);
        }

        return toItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChangePrice)) {
            return false;
        }

        ChangePrice that = (ChangePrice) o;
        return getKey().equalsIgnoreCase(that.getKey()) && getValue() == that.getValue() && getExpiryDate().equals(that.getExpiryDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(), getExpiryDate());
    }
}
