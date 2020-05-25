package com.osallek.eu4parser.model.game.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Religion {

    private final ClausewitzItem item;

    private final ReligionGroup religionGroup;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.item = item;
        this.religionGroup = religionGroup;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }
}
