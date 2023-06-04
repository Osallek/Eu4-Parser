package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class TradeNodeOutgoing {

    private final ClausewitzItem item;

    public TradeNodeOutgoing(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name").map(ClausewitzUtils::removeQuotes);
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public List<Integer> getPath() {
        return this.item.getList("path").map(ClausewitzList::getValuesAsInt).stream().flatMap(Collection::stream).toList();
    }

    public void setPath(List<Integer> path) {
        this.item.getList("path").get().setAll(path.stream().filter(Objects::nonNull).map(String::valueOf).toList());
    }

    public List<Pair<Double, Double>> getControl() {
        return this.item.getList("control")
                        .map(list -> IntStream.iterate(0, i -> i < list.size(), i -> i + 2)
                                              .mapToObj(i -> Pair.of(list.getAsDouble(i).get(), list.getAsDouble(i + 1).get()))
                                              .toList())
                        .orElse(new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeNodeOutgoing tradeNodeOutgoing)) {
            return false;
        }

        return Objects.equals(getName(), tradeNodeOutgoing.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName().orElse("");
    }
}
