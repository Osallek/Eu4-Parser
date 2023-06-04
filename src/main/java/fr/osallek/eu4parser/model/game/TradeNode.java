package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TradeNode extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public TradeNode(ClausewitzItem item, FileNode fileNode, Game game) {
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

    public Optional<Integer> getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(int location) {
        this.item.setVariable("location", location);
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
        }, () -> Color.addToItem(this.item, "color", color));
    }

    public boolean isInland() {
        return this.item.getVarAsBool("inland").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setInland(Boolean inland) {
        if (inland == null) {
            this.item.removeVariable("inland");
        } else {
            this.item.setVariable("inland", inland);
        }
    }

    public boolean isAiWillPropagateThroughTrade() {
        return this.item.getVarAsBool("ai_will_propagate_through_trade").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setAiWillPropagateThroughTrade(Boolean aiWillPropagateThroughTrade) {
        if (aiWillPropagateThroughTrade == null) {
            this.item.removeVariable("ai_will_propagate_through_trade");
        } else {
            this.item.setVariable("ai_will_propagate_through_trade", aiWillPropagateThroughTrade);
        }
    }

    public boolean isEnd() {
        return this.item.getVarAsBool("end").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setEnd(Boolean end) {
        if (end == null) {
            this.item.removeVariable("end");
        } else {
            this.item.setVariable("end", end);
        }
    }

    public List<Integer> getProvinces() {
        return this.item.getList("members").map(ClausewitzList::getValuesAsInt).stream().flatMap(Collection::stream).toList();
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.item.removeList("members");
            return;
        }

        this.item.getList("members")
                 .ifPresentOrElse(list -> list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new)),
                                  () -> this.item.addList("members", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new)));
    }

    public void addProvince(int province) {
        this.item.getList("members").ifPresentOrElse(list -> {
            list.add(province);
            list.sortInt();
        }, () -> this.item.addList("members", province));
    }

    public void removeProvince(int province) {
        this.item.getList("members").ifPresent(l -> l.remove(String.valueOf(province)));
    }

    public List<TradeNodeOutgoing> getOutgoings() {
        return this.item.getChildren("outgoing").stream().map(TradeNodeOutgoing::new).toList();
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeNode tradeNode)) {
            return false;
        }

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
