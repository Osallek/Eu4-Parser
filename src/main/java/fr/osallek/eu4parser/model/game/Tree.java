package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Tree extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    private final List<Color> fileColors;

    public Tree(ClausewitzItem item, FileNode fileNode, Game game, List<Color> fileColors) {
        super(fileNode);
        this.item = item;
        this.game = game;
        this.fileColors = fileColors;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public List<Color> getFileColors() {
        return this.item.getList("color").map(ClausewitzList::getValuesAsInt).stream().flatMap(Collection::stream).map(this.fileColors::get).toList();
    }

    public void setColor(List<Integer> indexes) {
        indexes.removeIf(index -> index < 0 || index >= this.fileColors.size());

        this.item.getList("color").get().setAll(indexes.toArray(Integer[]::new));
    }

    public Optional<String> getTerrainName() {
        return this.item.getVarAsString("terrain");
    }

    public Optional<TerrainCategory> getTerrain() {
        return getTerrainName().map(this.game::getTerrainCategory);
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

        if (!(o instanceof Tree tree)) {
            return false;
        }

        return Objects.equals(getName(), tree.getName());
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
