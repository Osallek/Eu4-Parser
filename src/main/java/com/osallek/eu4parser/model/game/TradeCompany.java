package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeCompany {

    private final String name;

    private String localizedName;

    private final Color color;

    private final List<Names> names;

    private final List<Integer> provinces;

    public TradeCompany(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);

        List<ClausewitzItem> namesItems = item.getChildren("names");
        this.names = namesItems.stream().map(Names::new).collect(Collectors.toList());

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

    public List<Names> getNames() {
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

        if (!(o instanceof TradeCompany)) {
            return false;
        }

        TradeCompany area = (TradeCompany) o;

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
