package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.List;
import java.util.Objects;

public class Continent {

    private final String name;

    private String localizedName;

    private final List<Integer> provinces;

    public Continent(ClausewitzList list) {
        this.name = list.getName();
        this.provinces = list.getValuesAsInt();
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getProvinces() {
        return provinces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Continent)) {
            return false;
        }

        Continent area = (Continent) o;

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
