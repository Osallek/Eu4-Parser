package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Culture extends AbstractCulture {

    private final CultureGroup cultureGroup;

    private String localizedName;

    private List<String> maleNames;

    private List<String> femaleNames;

    private List<String> dynastyNames;

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

    public List<String> getMaleNames() {
        return this.maleNames == null ? new ArrayList<>() : this.maleNames;
    }

    @Override
    public List<String> getPossibleMaleNames() {
        return Stream.concat(getMaleNames().stream(), this.cultureGroup.getMaleNames().stream())
                     .collect(Collectors.toList());
    }

    public void setMaleNames(List<String> maleNames) {
        this.maleNames = maleNames;
    }

    public void addMaleName(String maleName) {
        this.maleNames.add(maleName);
    }

    public void removeMaleName(String maleName) {
        this.maleNames.remove(maleName);
    }

    public List<String> getFemaleNames() {
        return this.femaleNames == null ? new ArrayList<>() : this.femaleNames;
    }

    @Override
    public List<String> getPossibleFemaleNames() {
        return Stream.concat(getFemaleNames().stream(), this.cultureGroup.getFemaleNames().stream())
                     .collect(Collectors.toList());
    }

    public void setFemaleNames(List<String> femaleNames) {
        this.femaleNames = femaleNames;
    }

    public void addFemaleName(String femaleName) {
        this.femaleNames.add(femaleName);
    }

    public void removeFemaleName(String femaleName) {
        this.femaleNames.remove(femaleName);
    }

    public List<String> getDynastyNames() {
        return this.dynastyNames == null ? new ArrayList<>() : this.dynastyNames;
    }

    @Override
    public List<String> getPossibleDynastyNames() {
        return Stream.concat(getDynastyNames().stream(), this.cultureGroup.getDynastyNames().stream())
                     .collect(Collectors.toList());
    }

    public void setDynastyNames(List<String> dynastyNames) {
        this.dynastyNames = dynastyNames;
    }

    public void addDynastyName(String dynastyName) {
        this.dynastyNames.add(dynastyName);
    }

    public void removeDynastyName(String dynastyName) {
        this.dynastyNames.remove(dynastyName);
    }
}
