package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeNode {

    private final String name;

    private String localizedName;

    private final Integer location;

    private final Color color;

    private final boolean inland;

    private final boolean aiWillPropagateThroughTrade;

    private final boolean end;

    private final List<Integer> provinces;

    private final List<TradeNodeOutgoing> outgoings;

    public TradeNode(ClausewitzItem item) {
        this.name = item.getName();
        this.location = item.getVarAsInt("location");

        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);
        this.inland = BooleanUtils.toBoolean(item.getVarAsBool("inland"));
        this.aiWillPropagateThroughTrade = BooleanUtils.toBoolean(item.getVarAsBool("ai_will_propagate_through_trade"));
        this.end = BooleanUtils.toBoolean(item.getVarAsBool("end"));

        list = item.getList("members");
        this.provinces = list == null ? null : list.getValuesAsInt();

        this.outgoings = item.getChildren("outgoing").stream().map(TradeNodeOutgoing::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Integer getLocation() {
        return location;
    }

    public Color getColor() {
        return color;
    }

    public boolean isInland() {
        return inland;
    }

    public boolean isAiWillPropagateThroughTrade() {
        return aiWillPropagateThroughTrade;
    }

    public boolean isEnd() {
        return end;
    }

    public List<Integer> getProvinces() {
        return provinces;
    }

    public List<TradeNodeOutgoing> getOutgoings() {
        return outgoings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeNode)) {
            return false;
        }

        TradeNode area = (TradeNode) o;

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
