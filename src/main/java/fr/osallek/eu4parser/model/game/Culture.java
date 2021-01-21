package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Culture extends AbstractCulture {

    private final CultureGroup cultureGroup;

    private String localizedName;

    public Culture(ClausewitzItem item, CultureGroup cultureGroup) {
        super(item);
        this.cultureGroup = cultureGroup;
        ClausewitzList list = item.getList("male_names");
        this.maleNames = list == null ? null : list.getValues();
        list = item.getList("female_names");
        this.femaleNames = list == null ? null : list.getValues();
        list = item.getList("dynasty_names");
        this.dynastyNames = list == null ? null : list.getValues();
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public CultureGroup getCultureGroup() {
        return cultureGroup;
    }

    @Override
    public List<String> getPossibleMaleNames() {
        return Stream.concat(getMaleNames().stream(), this.cultureGroup.getMaleNames().stream())
                     .collect(Collectors.toList());
    }

    @Override
    public List<String> getPossibleFemaleNames() {
        return Stream.concat(getFemaleNames().stream(), this.cultureGroup.getFemaleNames().stream())
                     .collect(Collectors.toList());
    }

    @Override
    public List<String> getPossibleDynastyNames() {
        return Stream.concat(getDynastyNames().stream(), this.cultureGroup.getDynastyNames().stream())
                     .collect(Collectors.toList());
    }
}
