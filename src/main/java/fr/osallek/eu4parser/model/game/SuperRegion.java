package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SuperRegion extends Nodded {

    private final ClausewitzList list;

    private final Game game;

    public SuperRegion(ClausewitzList list, Game game) {
        this.list = list;
        this.game = game;
    }

    @Override
    public String getName() {
        return this.list.getName();
    }

    public void setName(String name) {
        this.list.setName(name);
    }

    public List<Region> getRegions() {
        return this.list.getValues().stream().filter(s -> !s.equalsIgnoreCase("restrict_charter")).map(game::getRegion).toList();
    }

    public void setRegions(List<String> regions) {
        if (CollectionUtils.isEmpty(regions)) {
            this.list.clear();
            return;
        }

        this.list.setAll(regions.stream().filter(Objects::nonNull).toArray(String[]::new));
    }

    public void addRegion(String region) {
        this.list.add(region);
        this.list.sort();
    }

    public void removeRegion(String region) {
        this.list.remove(region);
    }

    public boolean restrictCharter() {
        return this.list.contains("restrict_charter");
    }

    public void setRestrictCharter(boolean restrictCharter) {
        if (restrictCharter) {
            if (!this.list.contains("restrict_charter")) {
                this.list.add("restrict_charter");
            }
        } else {
            this.list.remove("restrict_charter");
        }
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.list.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SuperRegion superRegion)) {
            return false;
        }

        return Objects.equals(getName(), superRegion.getName());
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
