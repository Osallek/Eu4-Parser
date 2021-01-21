package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Area {

    private final String name;

    private final List<Integer> provinces;

    private Region region;

    public Area(ClausewitzList list) {
        this.name = list.getName();
        this.provinces = list.getValuesAsInt();
    }

    public Area(ClausewitzItem item) {
        this.name = item.getName();
        this.provinces = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Integer> getProvinces() {
        return provinces;
    }

    public Region getRegion() {
        return region;
    }

    void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Area)) {
            return false;
        }

        Area area = (Area) o;

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
