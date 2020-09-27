package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Religion {

    private final ReligionGroup religionGroup;

    private final Papacy papacy;

    private String name;

    private String localizedName;

    private final Color color;

    private final boolean hreReligion;

    private final boolean hreHereticReligion;

    private final boolean useAuthority;

    private final boolean useReligiousReforms;

    private final boolean usesAnglicanPower;

    private final boolean usesHussitePower;

    private final boolean usesChurchPower;

    private final boolean useFervor;

    private final boolean hasPatriarchs;

    private final boolean misguidedHeretic;

    private final boolean useFetishistCult;

    private final boolean usesIsolationism;

    private final boolean usesKarma;

    private final boolean usePersonalDeity;

    private final boolean usesPiety;

    private final boolean canHaveSecondaryReligion;

    private final List<String> aspects;

    private final List<String> blessings;

    private final List<String> heretic;

    private final List<Integer> holySites;

    private final List<Icon> icons;

    private final Date date;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.religionGroup = religionGroup;
        this.name = item.getName();
        ClausewitzList list = item.getList("color");
        this.color = list == null ? null : new Color(list);
        this.hreReligion = BooleanUtils.toBoolean(item.getVarAsBool("hre_religion"));
        this.hreHereticReligion = BooleanUtils.toBoolean(item.getVarAsBool("hre_heretic_religion"));
        this.useAuthority = BooleanUtils.toBoolean(item.getVarAsBool("authority"));
        this.useReligiousReforms = BooleanUtils.toBoolean(item.getVarAsBool("religious_reforms"));
        this.usesAnglicanPower = BooleanUtils.toBoolean(item.getVarAsBool("uses_anglican_power"));
        this.usesHussitePower = BooleanUtils.toBoolean(item.getVarAsBool("uses_hussite_power"));
        this.usesChurchPower = BooleanUtils.toBoolean(item.getVarAsBool("uses_church_power"));
        this.useFervor = BooleanUtils.toBoolean(item.getVarAsBool("fervor"));
        this.hasPatriarchs = BooleanUtils.toBoolean(item.getVarAsBool("has_patriarchs"));
        this.misguidedHeretic = BooleanUtils.toBoolean(item.getVarAsBool("misguided_heretic"));
        this.useFetishistCult = BooleanUtils.toBoolean(item.getVarAsBool("fetishist_cult"));
        this.usesIsolationism = BooleanUtils.toBoolean(item.getVarAsBool("uses_isolationism"));
        this.usesKarma = BooleanUtils.toBoolean(item.getVarAsBool("uses_karma"));
        this.usePersonalDeity = BooleanUtils.toBoolean(item.getVarAsBool("personal_deity"));
        this.usesPiety = BooleanUtils.toBoolean(item.getVarAsBool("uses_piety"));
        this.canHaveSecondaryReligion = BooleanUtils.toBoolean(item.getVarAsBool("can_have_secondary_religion"));
        list = item.getList("aspects");
        this.aspects = list == null ? null : list.getValues();
        list = item.getList("heretic");
        this.heretic = list == null ? null : list.getValues();
        list = item.getList("blessings");
        this.blessings = list == null ? null : list.getValues();
        list = item.getList("holy_sites");
        this.holySites = list == null ? null : list.getValuesAsInt();
        this.date = item.getVarAsDate("date");
        ClausewitzItem child = item.getChild("papacy");
        this.papacy = child == null ? null : new Papacy(child);
        child = item.getChild("orthodox_icons");
        this.icons = child == null ? null : child.getChildren().stream().map(Icon::new).collect(Collectors.toList());
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

    public boolean useAuthority() {
        return useAuthority;
    }

    public boolean useReligiousReforms() {
        return useReligiousReforms;
    }

    public boolean usesAnglicanPower() {
        return usesAnglicanPower;
    }

    public boolean usesHussitePower() {
        return usesHussitePower;
    }

    public boolean usesChurchPower() {
        return usesChurchPower;
    }

    public boolean useFervor() {
        return useFervor;
    }

    public boolean hasPatriarchs() {
        return hasPatriarchs;
    }

    public boolean misguidedHeretic() {
        return misguidedHeretic;
    }

    public boolean useFetishistCult() {
        return useFetishistCult;
    }

    public boolean usesIsolationism() {
        return usesIsolationism;
    }

    public boolean usesKarma() {
        return usesKarma;
    }

    public boolean usePersonalDeity() {
        return usePersonalDeity;
    }

    public boolean usesPiety() {
        return usesPiety;
    }

    public boolean canHaveSecondaryReligion() {
        return canHaveSecondaryReligion;
    }

    public List<String> getAspects() {
        return aspects;
    }

    public List<String> getBlessings() {
        return blessings;
    }

    public List<String> getHeretic() {
        return heretic;
    }

    public List<Integer> getHolySites() {
        return holySites;
    }

    public Date getDate() {
        return this.date;
    }

    public Papacy getPapacy() {
        return papacy;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }
}
