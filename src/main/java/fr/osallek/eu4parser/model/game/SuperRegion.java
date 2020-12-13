package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SuperRegion {

    private final String name;

    private String localizedName;

    private final List<Region> regions;

    public SuperRegion(ClausewitzList list, Game game) {
        this.name = list.getName();
        this.regions = list.getValues().stream().filter(s -> !s.equalsIgnoreCase("restrict_charter")).map(game::getRegion).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SuperRegion)) {
            return false;
        }

        SuperRegion area = (SuperRegion) o;

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
