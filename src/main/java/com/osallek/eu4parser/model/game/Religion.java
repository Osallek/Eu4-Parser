package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;

import java.util.Date;

public class Religion {

    private final ClausewitzItem item;

    private final ReligionGroup religionGroup;

    private Papacy papacy;

    private String localizedName;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.item = item;
        this.religionGroup = religionGroup;

        refreshAttributes();
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName.substring(0, 1).toUpperCase() + localizedName.substring(1);
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getColor() {
        ClausewitzList colorList = this.item.getList("color");

        return Eu4Utils.rgbToColor(colorList.getAsInt(0), colorList.getAsInt(1), colorList.getAsInt(2));
    }

    public boolean hreReligion() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("hre_religion"));
    }

    public boolean hreHereticReligion() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("hre_heretic_religion"));
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public Papacy getPapacy() {
        return papacy;
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }

    private void refreshAttributes() {
        ClausewitzItem papacyItem = this.item.getChild("papacy");

        if (papacyItem != null) {
            this.papacy = new Papacy(papacyItem);
        }
    }
}
