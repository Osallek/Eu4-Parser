package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

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
        ClausewitzList list = this.item.getList("male_names");
        return list == null ? null : list.getValues();
    }

    public void setMaleNames(List<String> maleNames) {
        if (CollectionUtils.isEmpty(maleNames)) {
            this.item.removeList("male_names");
            return;
        }

        ClausewitzList list = this.item.getList("male_names");

        if (list != null) {
            list.setAll(maleNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("male_names", maleNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getFemaleNames() {
        ClausewitzList list = this.item.getList("female_names");
        return list == null ? null : list.getValues();
    }

    public void setFemaleNames(List<String> femaleNames) {
        if (CollectionUtils.isEmpty(femaleNames)) {
            this.item.removeList("female_names");
            return;
        }

        ClausewitzList list = this.item.getList("female_names");

        if (list != null) {
            list.setAll(femaleNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("female_names", femaleNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getDynastyNames() {
        ClausewitzList list = this.item.getList("dynasty_names");
        return list == null ? null : list.getValues();
    }

    public void setDynastyNames(List<String> dynastyNames) {
        if (CollectionUtils.isEmpty(dynastyNames)) {
            this.item.removeList("dynasty_names");
            return;
        }

        ClausewitzList list = this.item.getList("dynasty_names");

        if (list != null) {
            list.setAll(dynastyNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("dynasty_names", dynastyNames.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public Modifiers getCountryModifiers() {
        return new Modifiers(this.item.getChild("country"));
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province"));
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
