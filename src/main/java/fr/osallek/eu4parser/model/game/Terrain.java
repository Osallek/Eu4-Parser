package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Terrain extends Noded {

    private final ClausewitzItem item;

    private final Game game;

    private final List<Color> colors;

    public Terrain(ClausewitzItem item, FileNode fileNode, Game game, List<Color> colors) {
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

    public Color getColor() {
        ClausewitzList list = this.item.getList("color");
        return list == null ? null : this.colors.get(list.getAsInt(0));
    }

    public void setColor(int index) {
        if (index < 0 || index >= this.colors.size()) {
            return;
        }

        ClausewitzList list = this.item.getList("color");
        list.set(0, index);
    }

    public String getCategoryName() {
        return this.item.getVarAsString("type");
    }

    public TerrainCategory getCategory() {
        return this.game.getTerrainCategory(getCategoryName());
    }

    public void setCategory(String category) {
        this.item.setVariable("type", category);
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

        if (!(o instanceof Terrain)) {
            return false;
        }

        Terrain area = (Terrain) o;

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
