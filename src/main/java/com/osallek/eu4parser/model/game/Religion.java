package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.save.Color;

import java.util.Date;

public class Religion {

    private final ReligionGroup religionGroup;

    private final Papacy papacy;

    private String name;

    private String localizedName;

    private final Color color;

    private final boolean hreReligion;

    private final boolean hreHereticReligion;

    private final Date date;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.religionGroup = religionGroup;
        this.name = item.getName();
        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);
        this.hreReligion = Boolean.TRUE.equals(item.getVarAsBool("hre_religion"));
        this.hreHereticReligion = Boolean.TRUE.equals(item.getVarAsBool("hre_heretic_religion"));
        this.date = item.getVarAsDate("date");
        ClausewitzItem child = item.getChild("papacy");
        this.papacy = child == null ? null : new Papacy(child);
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName.substring(0, 1).toUpperCase() + localizedName.substring(1);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean hreReligion() {
        return this.hreReligion;
    }

    public boolean hreHereticReligion() {
        return this.hreHereticReligion;
    }

    public Date getDate() {
        return this.date;
    }

    public Papacy getPapacy() {
        return papacy;
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }
}
