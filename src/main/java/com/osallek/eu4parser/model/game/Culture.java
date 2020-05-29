package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Culture extends AbstractCulture {

    private final CultureGroup cultureGroup;

    private String localizedName;

    public Culture(ClausewitzItem item, CultureGroup cultureGroup) {
        super(item);
        this.cultureGroup = cultureGroup;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<String> getMaleNames() {
        ClausewitzList list = this.item.getList("male_names");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    @Override
    public List<String> getPossibleMaleNames() {
        return Stream.concat(getMaleNames().stream(), this.cultureGroup.getMaleNames().stream())
                     .collect(Collectors.toList());
    }

    public void setMaleNames(List<String> maleNames) {
        ClausewitzList list = this.item.getList("male_names");
        list.clear();

        maleNames.stream().map(ClausewitzUtils::addQuotes).filter(tag -> tag.length() == 5).forEach(list::add);
    }

    public void addMaleName(String maleName) {
        ClausewitzList list = this.item.getList("male_names");

        maleName = ClausewitzUtils.addQuotes(maleName);

        if (maleName.length() == 5 && !list.contains(maleName)) {
            list.add(maleName);
        }
    }

    public void removeMaleName(String maleName) {
        ClausewitzList list = this.item.getList("male_names");

        if (list != null) {
            list.remove(maleName);
        }
    }

    public List<String> getFemaleNames() {
        ClausewitzList list = this.item.getList("female_names");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    @Override
    public List<String> getPossibleFemaleNames() {
        return Stream.concat(getFemaleNames().stream(), this.cultureGroup.getFemaleNames().stream())
                     .collect(Collectors.toList());
    }

    public void setFemaleNames(List<String> femaleNames) {
        ClausewitzList list = this.item.getList("female_names");
        list.clear();

        femaleNames.stream().map(ClausewitzUtils::addQuotes).filter(tag -> tag.length() == 5).forEach(list::add);
    }

    public void addFemaleName(String femaleName) {
        ClausewitzList list = this.item.getList("female_names");

        femaleName = ClausewitzUtils.addQuotes(femaleName);

        if (femaleName.length() == 5 && !list.contains(femaleName)) {
            list.add(femaleName);
        }
    }

    public void removeFemaleName(String femaleName) {
        ClausewitzList list = this.item.getList("female_names");

        if (list != null) {
            list.remove(femaleName);
        }
    }

    public List<String> getDynastyNames() {
        ClausewitzList list = this.item.getList("dynasty_names");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    @Override
    public List<String> getPossibleDynastyNames() {
        return Stream.concat(getDynastyNames().stream(), this.cultureGroup.getDynastyNames().stream())
                     .collect(Collectors.toList());
    }

    public void setDynastyNames(List<String> dynastyNames) {
        ClausewitzList list = this.item.getList("dynasty_names");
        list.clear();

        dynastyNames.stream().map(ClausewitzUtils::addQuotes).filter(tag -> tag.length() == 5).forEach(list::add);
    }

    public void addDynastyName(String dynastyName) {
        ClausewitzList list = this.item.getList("dynasty_names");

        dynastyName = ClausewitzUtils.addQuotes(dynastyName);

        if (dynastyName.length() == 5 && !list.contains(dynastyName)) {
            list.add(dynastyName);
        }
    }

    public void removeDynastyName(String dynastyName) {
        ClausewitzList list = this.item.getList("dynasty_names");

        if (list != null) {
            list.remove(dynastyName);
        }
    }
}
