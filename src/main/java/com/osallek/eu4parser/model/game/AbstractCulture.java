package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCulture {

    protected final ClausewitzItem item;

    public AbstractCulture(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public List<String> getMaleNames() {
        ClausewitzList list = this.item.getList("male_names");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public abstract List<String> getPossibleMaleNames();

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

    public abstract List<String> getPossibleFemaleNames();

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

    public abstract List<String> getPossibleDynastyNames();

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
