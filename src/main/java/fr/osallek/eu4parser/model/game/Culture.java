package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Stream;

public class Culture extends AbstractCulture {

    private final CultureGroup cultureGroup;

    public Culture(Game game, ClausewitzItem item, CultureGroup cultureGroup) {
        super(game, item);
        this.cultureGroup = cultureGroup;
    }

    public CultureGroup getCultureGroup() {
        return cultureGroup;
    }

    @Override
    public List<String> getPossibleMaleNames() {
        return Stream.concat(getMaleNames().stream(), this.cultureGroup.getMaleNames().stream())
                     .toList();
    }

    @Override
    public List<String> getPossibleFemaleNames() {
        return Stream.concat(getFemaleNames().stream(), this.cultureGroup.getFemaleNames().stream())
                     .toList();
    }

    @Override
    public List<String> getPossibleDynastyNames() {
        return Stream.concat(getDynastyNames().stream(), this.cultureGroup.getDynastyNames().stream())
                     .toList();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
