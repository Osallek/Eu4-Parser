package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.game.todo.Religion;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColonialRegion extends Noded {

    private final ClausewitzItem item;

    private final Game game;

    public ColonialRegion(ClausewitzItem item, FileNode fileNode, Game game) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Color getColor() {
        ClausewitzList list = this.item.getList("color");
        return list == null ? null : new Color(list);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public List<Integer> getProvinces() {
        ClausewitzList list = this.item.getList("provinces");
        return list == null ? null : list.getValuesAsInt();
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.item.removeList("provinces");
            return;
        }

        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        } else {
            this.item.addList("provinces", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        }
    }

    public void addProvince(int province) {
        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.add(province);
            list.sortInt();
        } else {
            this.item.addList("provinces", province);
        }
    }

    public void removeProvince(int province) {
        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.remove(String.valueOf(province));
        }
    }

    public Integer getTaxIncome() {
        return this.item.getVarAsInt("tax_income");
    }

    public void setTaxIncome(Integer taxIncome) {
        if (taxIncome == null) {
            this.item.removeVariable("tax_income");
        } else {
            this.item.setVariable("tax_income", taxIncome);
        }
    }

    public Integer getNativeSize() {
        return this.item.getVarAsInt("native_size");
    }

    public void setNativeSize(Integer nativeSize) {
        if (nativeSize == null) {
            this.item.removeVariable("native_size");
        } else {
            this.item.setVariable("native_size", nativeSize);
        }
    }

    public Integer getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(Integer nativeFerocity) {
        if (nativeFerocity == null) {
            this.item.removeVariable("native_ferocity");
        } else {
            this.item.setVariable("native_ferocity", nativeFerocity);
        }
    }

    public Integer getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public void setNativeHostileness(Integer nativeHostileness) {
        if (nativeHostileness == null) {
            this.item.removeVariable("native_hostileness");
        } else {
            this.item.setVariable("native_hostileness", nativeHostileness);
        }
    }

    public Map<TradeGood, Integer> getTradeGoods() {
        ClausewitzItem child = item.getChild("trade_goods");
        return child == null ? null : child.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(var -> game.getTradeGood(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Culture, Integer> getCultures() {
        ClausewitzItem child = item.getChild("culture");
        return child == null ? null : child.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(var -> game.getCulture(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Religion, Integer> getReligions() {
        ClausewitzItem child = item.getChild("religion");
        return child == null ? null : child.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(var -> game.getReligion(var.getName()), ClausewitzVariable::getAsInt));
    }

    public List<Names> getColonialNames() {
        List<ClausewitzItem> names = item.getChildren("names");
        return names.stream().map(Names::new).collect(Collectors.toList());
    }

    public boolean isRandom() {
        return CollectionUtils.isEmpty(getProvinces());
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ColonialRegion)) {
            return false;
        }

        ColonialRegion area = (ColonialRegion) o;

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
}
