package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeGood {

    private String name;

    private String localizedName;

    private Color color;

    private Map<String, Double> modifiers;

    private Map<String, Double> provinceModifiers;

    private Double basePrice;

    public TradeGood(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list, true);
        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                               ClausewitzVariable::getAsDouble,
                                                                               (a, b) -> b,
                                                                               LinkedHashMap::new));
        child = item.getChild("province");
        this.provinceModifiers = child == null ? null : child.getVariables()
                                                             .stream()
                                                             .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                       ClausewitzVariable::getAsDouble,
                                                                                       (a, b) -> b,
                                                                                       LinkedHashMap::new));
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

    public Map<String, Double> getModifiers() {
        return this.modifiers == null ? new LinkedHashMap<>() : this.modifiers;
    }

    public void addModifier(String modifier, Double quantity) {
        if (this.modifiers == null) {
            this.modifiers = new LinkedHashMap<>();
        }

        this.modifiers.put(modifier, quantity);
    }

    public void removeModifier(String modifier) {
        if (this.modifiers != null) {
            this.modifiers.remove(modifier);
        }
    }

    public Map<String, Double> getProvinceModifiers() {
        return this.provinceModifiers == null ? new LinkedHashMap<>() : this.provinceModifiers;
    }

    public void addProvinceModifier(String modifier, Double quantity) {
        if (this.provinceModifiers == null) {
            this.provinceModifiers = new LinkedHashMap<>();
        }

        this.provinceModifiers.put(modifier, quantity);
    }

    public void removeProvinceModifier(String modifier) {
        if (this.provinceModifiers != null) {
            this.provinceModifiers.remove(modifier);
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
