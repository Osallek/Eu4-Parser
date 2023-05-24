package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ColonialRegion extends Nodded {

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

    public Optional<Color> getColor() {
        return this.item.getList("color").map(Color::new);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        this.item.getList("color").ifPresentOrElse(list -> {
                                                       Color actualColor = new Color(list);
                                                       actualColor.setRed(color.getRed());
                                                       actualColor.setGreen(color.getGreen());
                                                       actualColor.setBlue(color.getBlue());
                                                   },
                                                   () -> Color.addToItem(this.item, "color", color));
    }

    public List<Integer> getProvinces() {
        return this.item.getList("provinces").map(ClausewitzList::getValuesAsInt).orElse(new ArrayList<>());
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.item.removeList("provinces");
            return;
        }

        this.item.getList("provinces")
                 .ifPresentOrElse(list -> list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new)),
                                  () -> this.item.addList("provinces", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new)));
    }

    public void addProvince(int province) {
        this.item.getList("provinces").ifPresentOrElse(list -> {
            list.add(province);
            list.sortInt();
        }, () -> this.item.addList("provinces", province));
    }

    public void removeProvince(int province) {
        this.item.getList("provinces").ifPresent(list -> list.remove(String.valueOf(province)));
    }

    public Optional<Integer> getTaxIncome() {
        return this.item.getVarAsInt("tax_income");
    }

    public void setTaxIncome(Integer taxIncome) {
        if (taxIncome == null) {
            this.item.removeVariable("tax_income");
        } else {
            this.item.setVariable("tax_income", taxIncome);
        }
    }

    public Optional<Integer> getNativeSize() {
        return this.item.getVarAsInt("native_size");
    }

    public void setNativeSize(Integer nativeSize) {
        if (nativeSize == null) {
            this.item.removeVariable("native_size");
        } else {
            this.item.setVariable("native_size", nativeSize);
        }
    }

    public Optional<Integer> getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(Integer nativeFerocity) {
        if (nativeFerocity == null) {
            this.item.removeVariable("native_ferocity");
        } else {
            this.item.setVariable("native_ferocity", nativeFerocity);
        }
    }

    public Optional<Integer> getNativeHostileness() {
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
        return this.item.getChild("trade_goods")
                        .map(ClausewitzItem::getVariables)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(variable -> this.game.getTradeGood(variable.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Culture, Integer> getCultures() {
        return this.item.getChild("culture")
                        .map(ClausewitzItem::getVariables)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(variable -> this.game.getCulture(variable.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Religion, Integer> getReligions() {
        return this.item.getChild("religion")
                        .map(ClausewitzItem::getVariables)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(variable -> this.game.getReligion(variable.getName()), ClausewitzVariable::getAsInt));
    }

    public List<Names> getColonialNames() {
        return this.item.getChildren("names").stream().map(Names::new).toList();
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

        if (!(o instanceof ColonialRegion area)) {
            return false;
        }

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
