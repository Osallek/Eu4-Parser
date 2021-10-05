package fr.osallek.eu4parser.model.game.todo;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.game.Condition;
import fr.osallek.eu4parser.model.game.Icon;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.Papacy;
import fr.osallek.eu4parser.model.game.ReligionGroup;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Religion {

    private final ReligionGroup religionGroup;

    private final Papacy papacy;

    private final ClausewitzItem item;

    private final Integer icon;

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

    private final boolean usesHarmony;

    private final boolean canHaveSecondaryReligion;

    private final boolean doom;

    private final List<String> allowedCenterConversion;

    private final List<String> aspects;

    private final List<String> blessings;

    private final List<String> heretic;

    private final List<Integer> holySites;

    private final List<Icon> icons;

    private final LocalDate date;

    private final String harmonizedModifier;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.religionGroup = religionGroup;
        this.item = item;
        this.icon = item.getVarAsInt("icon");
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
        this.usesHarmony = BooleanUtils.toBoolean(item.getVarAsBool("uses_harmony"));
        this.canHaveSecondaryReligion = BooleanUtils.toBoolean(item.getVarAsBool("can_have_secondary_religion"));
        this.doom = BooleanUtils.toBoolean(item.getVarAsBool("doom"));
        ClausewitzList list = item.getList("allowed_center_conversion");
        this.allowedCenterConversion = list == null ? null : list.getValues();
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
        this.harmonizedModifier = item.getVarAsString("harmonized_modifier");
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Color getColor() {
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList, true);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public Integer getIcon() {
        return icon;
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

    public boolean usesHarmony() {
        return usesHarmony;
    }

    public boolean canHaveSecondaryReligion() {
        return canHaveSecondaryReligion;
    }

    public boolean useDoom() {
        return doom;
    }

    public List<String> getAllowedCenterConversion() {
        return allowedCenterConversion;
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

    public LocalDate getDate() {
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

    public Modifiers getCountry() {
        return new Modifiers(this.item.getChild("country"));
    }

    public Modifiers getCountryAsSecondary() {
        return new Modifiers(item.getChild("country_as_secondary"));
    }

    public Condition getWillGetCenter() {
        ClausewitzItem child = this.item.getChild("will_get_center");
        return child == null ? null : new Condition(child);
    }

    public String getHarmonizedModifier() {
        return harmonizedModifier;
    }
}
