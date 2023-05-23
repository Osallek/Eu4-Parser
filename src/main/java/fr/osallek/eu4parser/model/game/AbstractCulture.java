package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCulture {

    protected final Game game;

    protected final ClausewitzItem item;

    protected AbstractCulture(Game game, ClausewitzItem item) {
        this.game = game;
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public abstract List<String> getPossibleMaleNames();

    public abstract List<String> getPossibleFemaleNames();

    public abstract List<String> getPossibleDynastyNames();

    public List<String> getMaleNames() {
        return this.item.getList("male_names").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setMaleNames(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            this.item.removeList("male_names");
            return;
        }

        this.item.getList("male_names")
                 .ifPresentOrElse(list -> list.setAll(names.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("male_names", names.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getFemaleNames() {
        return this.item.getList("female_names").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setFemaleNames(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            this.item.removeList("female_names");
            return;
        }

        this.item.getList("female_names")
                 .ifPresentOrElse(list -> list.setAll(names.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("female_names", names.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getDynastyNames() {
        return this.item.getList("dynasty_names").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setDynastyNames(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            this.item.removeList("dynasty_names");
            return;
        }

        this.item.getList("dynasty_names")
                 .ifPresentOrElse(list -> list.setAll(names.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("dynasty_names", names.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Optional<Modifiers> getCountryModifiers() {
        return this.item.getChild("country").map(Modifiers::new);
    }

    public Optional<Modifiers> getProvinceModifiers() {
        return this.item.getChild("province").map(Modifiers::new);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractCulture abstractCulture)) {
            return false;
        }

        return Objects.equals(getName(), abstractCulture.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
