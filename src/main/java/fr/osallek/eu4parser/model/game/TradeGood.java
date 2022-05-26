package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

import java.util.Objects;

public class TradeGood {

    private final ClausewitzItem item;

    private ClausewitzItem priceItem;

    public TradeGood(ClausewitzItem item) {
        this.item = item;
    }

    public void setPriceItem(ClausewitzItem priceItem) {
        this.priceItem = priceItem;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Color getColor() {
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList, true);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province"));
    }

    public Double getBasePrice() {
        return this.priceItem.getVarAsDouble("base_price");
    }

    public void setBasePrice(Double basePrice) {
        if (basePrice == null) {
            this.priceItem.removeVariable("base_price");
        } else {
            this.priceItem.setVariable("base_price", basePrice);
        }
    }

    public Boolean isGoldType() {
        return this.priceItem.getVarAsBool("goldtype");
    }

    public void setGoldType(Boolean goldType) {
        if (goldType == null) {
            this.priceItem.removeVariable("goldtype");
        } else {
            this.priceItem.setVariable("goldtype", goldType);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeGood tradeGood)) {
            return false;
        }

        return Objects.equals(getName(), tradeGood.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
