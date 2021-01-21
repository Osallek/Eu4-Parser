package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

public class CenterOfTrade implements Comparable<CenterOfTrade> {

    private final String name;

    private final int level;

    private final Integer cost;

    private final CenterOfTradeType type;

    private final Modifiers provinceModifiers;

    private final Modifiers stateModifiers;

    private final Modifiers globalModifiers;

    public CenterOfTrade(ClausewitzItem item) {
        this.name = item.getName();
        this.level = item.getVarAsInt("level");
        this.cost = item.getVarAsInt("cost");
        this.type = CenterOfTradeType.valueOf(item.getVarAsString("type").toUpperCase());
        this.provinceModifiers = new Modifiers(item.getChild("province_modifiers"));
        this.stateModifiers = new Modifiers(item.getChild("state_modifiers"));
        this.globalModifiers = new Modifiers(item.getChild("global_modifiers"));
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Integer getCost() {
        return cost;
    }

    public CenterOfTradeType getType() {
        return type;
    }

    public Modifiers getProvinceModifiers() {
        return provinceModifiers;
    }

    public Modifiers getStateModifiers() {
        return stateModifiers;
    }

    public Modifiers getGlobalModifiers() {
        return globalModifiers;
    }

    public boolean isValid(SaveProvince province) {
        return province.getCenterOfTradeLevel() != null && province.getCenterOfTradeLevel().equals(this.level)
               && (province.isPort() ? CenterOfTradeType.COASTAL.equals(this.type) : CenterOfTradeType.INLAND.equals(this.type));
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

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NotNull CenterOfTrade o) {
        return Comparator.comparing(CenterOfTrade::getType).thenComparingInt(CenterOfTrade::getLevel).compare(this, o);
    }
}
