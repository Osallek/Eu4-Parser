package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeNode extends Nodded {

    private final ClausewitzItem item;

    public TradeNode(ClausewitzItem item, FileNode fileNode) {
        super(fileNode);
        this.item = item;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(int location) {
        this.item.setVariable("location", location);
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

    public boolean isInland() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("inland"));
    }

    public void setInland(Boolean inland) {
        if (inland == null) {
            this.item.removeVariable("inland");
        } else {
            this.item.setVariable("inland", inland);
        }
    }

    public boolean isAiWillPropagateThroughTrade() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("ai_will_propagate_through_trade"));
    }

    public void setAiWillPropagateThroughTrade(Boolean aiWillPropagateThroughTrade) {
        if (aiWillPropagateThroughTrade == null) {
            this.item.removeVariable("ai_will_propagate_through_trade");
        } else {
            this.item.setVariable("ai_will_propagate_through_trade", aiWillPropagateThroughTrade);
        }
    }

    public boolean isEnd() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("end"));
    }

    public void setEnd(Boolean end) {
        if (end == null) {
            this.item.removeVariable("end");
        } else {
            this.item.setVariable("end", end);
        }
    }

    public List<Integer> getProvinces() {
        ClausewitzList list = this.item.getList("members");
        return list == null ? null : list.getValuesAsInt();
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.item.removeList("members");
            return;
        }

        ClausewitzList list = this.item.getList("members");

        if (list != null) {
            list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        } else {
            this.item.addList("members", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        }
    }

    public void addProvince(int province) {
        ClausewitzList list = this.item.getList("members");

        if (list != null) {
            list.add(province);
            list.sortInt();
        } else {
            this.item.addList("members", province);
        }
    }

    public void removeProvince(int province) {
        ClausewitzList list = this.item.getList("members");

        if (list != null) {
            list.remove(String.valueOf(province));
        }
    }

    public List<TradeNodeOutgoing> getOutgoings() {
        return this.item.getChildren("outgoing").stream().map(TradeNodeOutgoing::new).collect(Collectors.toList());
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

        if (!(o instanceof TradeNode)) {
            return false;
        }

        TradeNode tradeNode = (TradeNode) o;

        return Objects.equals(getName(), tradeNode.getName());
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
