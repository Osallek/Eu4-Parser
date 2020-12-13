package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColonialRegion {

    private final String name;

    private String localizedName;

    private final Color color;

    private final Integer taxIncome;

    private final Integer nativeSize;

    private final Integer nativeFerocity;

    private final Integer nativeHostileness;

    private final Map<TradeGood, Integer> tradeGoods;

    private final Map<Culture, Integer> cultures;

    private final Map<Religion, Integer> religions;

    private final List<Names> names;

    private final List<Integer> provinces;

    public ColonialRegion(ClausewitzItem item, Game game) {
        this.name = item.getName();

        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);
        this.taxIncome = item.getVarAsInt("tax_income");
        this.nativeSize = item.getVarAsInt("native_size");
        this.nativeFerocity = item.getVarAsInt("native_ferocity");
        this.nativeHostileness = item.getVarAsInt("native_hostileness");

        ClausewitzItem child = item.getChild("trade_goods");
        this.tradeGoods = child == null ? null : child.getVariables()
                                                      .stream()
                                                      .collect(Collectors.toMap(var -> game.getTradeGood(var.getName()), ClausewitzVariable::getAsInt));

        child = item.getChild("religion");
        this.religions = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.toMap(var -> game.getReligion(var.getName()), ClausewitzVariable::getAsInt));
        child = item.getChild("culture");
        this.cultures = child == null ? null : child.getVariables()
                                                    .stream()
                                                    .collect(Collectors.toMap(var -> game.getCulture(var.getName()), ClausewitzVariable::getAsInt));

        List<ClausewitzItem> names = item.getChildren("names");
        this.names = names.stream().map(Names::new).collect(Collectors.toList());

        list = item.getList("provinces");
        this.provinces = list == null ? null : list.getValuesAsInt();
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Color getColor() {
        return color;
    }

    public Integer getTaxIncome() {
        return taxIncome;
    }

    public Integer getNativeSize() {
        return nativeSize;
    }

    public Integer getNativeFerocity() {
        return nativeFerocity;
    }

    public Integer getNativeHostileness() {
        return nativeHostileness;
    }

    public Map<TradeGood, Integer> getTradeGoods() {
        return tradeGoods;
    }

    public Map<Culture, Integer> getCultures() {
        return cultures;
    }

    public Map<Religion, Integer> getReligions() {
        return religions;
    }

    public List<Names> getColonialNames() {
        return names;
    }

    public List<Integer> getProvinces() {
        return provinces;
    }

    public boolean isRandom() {
        return CollectionUtils.isEmpty(this.provinces);
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
}
