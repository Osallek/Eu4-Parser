package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeGood {

    private final ClausewitzItem item;

    ClausewitzItem priceItem;

    String localizedName;

    public TradeGood(ClausewitzItem item) {
        this.item = item;
    }

    void setPriceItem(ClausewitzItem priceItem) {
        this.priceItem = priceItem;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Color getColor() {
        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            return new Color(list);
        }

        return null;
    }

    public void setColor(Color color) {
        ClausewitzList list = this.item.getList("color");

        if (list == null) {
            Color.addToItem(this.item, "color", color);
        } else {
            list.set(0, color.getRed());
            list.set(1, color.getGreen());
            list.set(2, color.getBlue());
        }
    }

    public Map<String, Double> getModifiers() {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            return modifiersItem.getVariables()
                              .stream()
                              .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                        ClausewitzVariable::getAsDouble,
                                                        (a, b) -> b,
                                                        LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addModifier(String modifier, Double quantity) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.setVariable(modifier, quantity);
        }
    }

    public void removeModifier(String modifier) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.removeVariable(modifier);
        }
    }
    
    public Map<String, Double> getProvinceModifiers() {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            return provincesItem.getVariables()
                              .stream()
                              .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                        ClausewitzVariable::getAsDouble,
                                                        (a, b) -> b,
                                                        LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addProvinceModifier(String modifier, Double quantity) {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            provincesItem.setVariable(modifier, quantity);
        }
    }

    public void removeProvinceModifier(String modifier) {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            provincesItem.removeVariable(modifier);
        }
    }

    public Double getBasePrice() {
        return this.priceItem.getVarAsDouble("base_price");
    }

    public void setBasePrice(double basePrice) {
        this.priceItem.setVariable("base_price", basePrice);
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
