package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Comparator;
import java.util.Objects;

public class CenterOfTrade implements Comparable<CenterOfTrade> {

    private final ClausewitzItem item;

    public CenterOfTrade(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getLevel() {
        return this.item.getVarAsInt("level");
    }

    public void setLevel(int level) {
        this.item.setVariable("level", level);
    }

    public Integer getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(Integer cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Integer getDevelopment() {
        return this.item.getVarAsInt("development");
    }

    public void setDevelopment(Integer development) {
        if (development == null) {
            this.item.removeVariable("development");
        } else {
            this.item.setVariable("development", development);
        }
    }

    public CenterOfTradeType getType() {
        return CenterOfTradeType.valueOf(this.item.getVarAsString("type").toUpperCase());
    }

    public void setType(CenterOfTradeType type) {
        this.item.setVariable("type", type.name().toLowerCase());
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province_modifiers"));
    }

    public Modifiers getStateModifiers() {
        return new Modifiers(this.item.getChild("state_modifiers"));
    }

    public Modifiers getGlobalModifiers() {
        return new Modifiers(this.item.getChild("global_modifiers"));
    }

    public boolean isValid(SaveProvince province) {
        return province.getCenterOfTradeLevel() != null && province.getCenterOfTradeLevel().equals(getLevel())
               && (province.isPort() ? CenterOfTradeType.COASTAL.equals(getType()) : CenterOfTradeType.INLAND.equals(getType()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CenterOfTrade)) {
            return false;
        }

        CenterOfTrade area = (CenterOfTrade) o;

        return Objects.equals(getName(), area.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(CenterOfTrade o) {
        return Comparator.comparing(CenterOfTrade::getType).thenComparingInt(CenterOfTrade::getLevel).compare(this, o);
    }
}
