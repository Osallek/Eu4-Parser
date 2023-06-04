package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TradeCompany extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public TradeCompany(ClausewitzItem item, FileNode fileNode, Game game) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public Game getGame() {
        return game;
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
        }, () -> Color.addToItem(this.item, "color", color));
    }

    public List<Names> getNames() {
        List<ClausewitzItem> namesItems = this.item.getChildren("names");
        return CollectionUtils.isEmpty(namesItems) ? new ArrayList<>() : namesItems.stream().map(Names::new).toList();
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

        if (!(o instanceof TradeCompany tradeCompany)) {
            return false;
        }

        return Objects.equals(getName(), tradeCompany.getName());
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
