package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TradeNodeOutgoing {

    private final String name;

    private final List<Integer> path;

    private final List<Pair<Double, Double>> control = new ArrayList<>();

    public TradeNodeOutgoing(ClausewitzItem item) {
        this.name = ClausewitzUtils.removeQuotes(item.getVarAsString("name"));

        ClausewitzList list = item.getList("path");
        this.path = list == null ? null : list.getValuesAsInt();
        list = item.getList("control");

        if (list != null) {
            for (int i = 0; i < list.size(); i += 2) {
                this.control.add(Pair.of(list.getAsDouble(i), list.getAsDouble(i + 1)));
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPath() {
        return path;
    }

    public List<Pair<Double, Double>> getControl() {
        return control;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeNodeOutgoing)) {
            return false;
        }

        TradeNodeOutgoing area = (TradeNodeOutgoing) o;

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
