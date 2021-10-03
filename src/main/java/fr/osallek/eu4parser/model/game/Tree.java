package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tree extends Noded {

    private final ClausewitzItem item;

    private final Game game;

    private final List<Color> colors;

    public Tree(ClausewitzItem item, FileNode fileNode, Game game, List<Color> colors) {
        super(fileNode);
        this.item = item;
        this.game = game;
        this.colors = colors;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public List<Color> getColors() {
        ClausewitzList list = this.item.getList("color");
        return list == null ? null : list.getValuesAsInt().stream().map(this.colors::get).collect(Collectors.toList());
    }

    public void setColor(List<Integer> indexes) {
        indexes.removeIf(index -> index < 0 || index >= this.colors.size());

        ClausewitzList list = this.item.getList("color");
        list.setAll(indexes.toArray(Integer[]::new));
    }

    public String getTerrainName() {
        return this.item.getVarAsString("terrain");
    }

    public TerrainCategory getTerrain() {
        return this.game.getTerrainCategory(getTerrainName());
    }

    public void setTerrain(String terrain) {
        this.item.setVariable("terrain", terrain);
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

        if (!(o instanceof Tree)) {
            return false;
        }

        Tree area = (Tree) o;

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
