package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;

import java.util.Objects;

public class TradeGood {

    private String name;

    private String localizedName;

    private Color color;

    private Modifiers modifiers;

    private Modifiers provinceModifiers;

    private Double basePrice;

    public TradeGood(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list, true);
        this.modifiers = new Modifiers(item.getChild("modifier"));
        this.provinceModifiers = new Modifiers(item.getChild("province"));
    }

    void setPriceItem(ClausewitzItem priceItem) {
        this.basePrice = priceItem.getVarAsDouble("base_price");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Modifiers getModifiers() {
        return this.modifiers;
    }

    public void addModifier(String modifier, String quantity) {
        if (this.modifiers == null) {
            this.modifiers = new Modifiers();
        }

        this.modifiers.add(modifier, quantity);
    }

    public void removeModifier(String modifier) {
        if (this.modifiers != null) {
            this.modifiers.removeModifier(modifier);
        }
    }

    public Modifiers getProvinceModifiers() {
        return this.provinceModifiers;
    }

    public void addProvinceModifier(String modifier, String quantity) {
        if (this.provinceModifiers == null) {
            this.provinceModifiers = new Modifiers();
        }

        this.provinceModifiers.add(modifier, quantity);
    }

    public void removeProvinceModifier(String modifier) {
        if (this.provinceModifiers != null) {
            this.provinceModifiers.removeModifier(modifier);
        }
    }

    public Double getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeGood)) {
            return false;
        }

        TradeGood tradeGood = (TradeGood) o;
        return Objects.equals(getName(), tradeGood.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
