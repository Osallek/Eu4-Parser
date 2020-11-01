package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCulture {

    private String name;

    protected List<String> maleNames;

    protected List<String> femaleNames;

    protected List<String> dynastyNames;

    protected Modifiers countryModifiers;

    protected Modifiers provinceModifiers;

    public AbstractCulture(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzList list = item.getList("male_names");
        this.maleNames = list == null ? null : list.getValues();

        list = item.getList("female_names");
        this.femaleNames = list == null ? null : list.getValues();

        list = item.getList("dynasty_names");
        this.dynastyNames = list == null ? null : list.getValues();

        this.countryModifiers = new Modifiers(item.getChild("country"));
        this.provinceModifiers = new Modifiers(item.getChild("province"));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMaleNames() {
        return this.maleNames == null ? new ArrayList<>() : this.maleNames;
    }

    public abstract List<String> getPossibleMaleNames();

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

    public abstract List<String> getPossibleFemaleNames();

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

    public abstract List<String> getPossibleDynastyNames();

    public void setDynastyNames(List<String> dynastyNames) {
        this.dynastyNames = dynastyNames;
    }

    public void addDynastyName(String dynastyName) {
        this.dynastyNames.add(dynastyName);
    }

    public void removeDynastyName(String dynastyName) {
        this.dynastyNames.remove(dynastyName);
    }

    public Modifiers getCountryModifiers() {
        return countryModifiers;
    }

    public Modifiers getProvinceModifiers() {
        return provinceModifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractCulture)) {
            return false;
        }
        AbstractCulture that = (AbstractCulture) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
