package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.lang3.BooleanUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReligionGroup {

    private String name;

    private String localizedName;

    private final List<Religion> religions;

    private boolean defenderOfFaith;

    private boolean canFormPersonalUnions;

    private Integer centerOfReligion;

    private Integer flagsWithEmblemPercentage;

    private Map.Entry<Integer, Integer> flagEmblemIndexRange;

    private String harmonizedModifier;

    private String crusadeName;

    public ReligionGroup(ClausewitzItem item) {
        this.name = item.getName();
        this.religions = item.getChildren()
                             .stream()
                             .map(child -> new Religion(child, this))
                             .collect(Collectors.toList());
        this.defenderOfFaith = BooleanUtils.toBoolean(item.getVarAsBool("defender_of_faith"));
        this.canFormPersonalUnions = BooleanUtils.toBoolean(item.getVarAsBool("can_form_personal_unions"));
        this.centerOfReligion = item.getVarAsInt("center_of_religion");
        this.flagsWithEmblemPercentage = item.getVarAsInt("flags_with_emblem_percentage");
        ClausewitzList list = item.getList("flag_emblem_index_range");
        this.flagEmblemIndexRange = list == null ? null : new AbstractMap.SimpleEntry<>(list.getAsInt(0), list.getAsInt(1));
        this.harmonizedModifier = item.getVarAsString("harmonized_modifier");
        this.crusadeName = item.getVarAsString("crusade_name");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean defenderOfFaith() {
        return this.defenderOfFaith;
    }

    public void setDefenderOfFaith(boolean defenderOfFaith) {
        this.defenderOfFaith = defenderOfFaith;
    }

    public boolean canFormPersonalUnions() {
        return this.canFormPersonalUnions;
    }

    public void setCanFormPersonalUnions(boolean canFormPersonalUnions) {
        this.canFormPersonalUnions = canFormPersonalUnions;
    }

    public Integer centerOfReligion() {
        return this.centerOfReligion;
    }

    public void setCenterOfReligion(int centerOfReligion) {
        this.centerOfReligion = centerOfReligion;
    }

    public Integer getFlagsWithEmblemPercentage() {
        return this.flagsWithEmblemPercentage;
    }

    public void setFlagsWithEmblemPercentage(int flagsWithEmblemPercentage) {
        if (flagsWithEmblemPercentage < 0) {
            flagsWithEmblemPercentage = 0;
        } else if (flagsWithEmblemPercentage > 100) {
            flagsWithEmblemPercentage = 100;
        }

        this.flagsWithEmblemPercentage = flagsWithEmblemPercentage;
    }

    public Map.Entry<Integer, Integer> getFlagEmblemIndexRange() {
        return this.flagEmblemIndexRange;
    }

    public void setFlagEmblemIndexRange(int flagEmblemIndexRangeMin, int flagEmblemIndexRangeMax) {
        this.flagEmblemIndexRange = new AbstractMap.SimpleEntry<>(flagEmblemIndexRangeMin, flagEmblemIndexRangeMax);
    }

    public String harmonizedModifier() {
        return this.harmonizedModifier;
    }

    public void setHarmonizedModifier(String harmonizedModifier) {
        this.harmonizedModifier = harmonizedModifier;
    }

    public String crusadeName() {
        return this.crusadeName;
    }

    public void setCrusadeName(String crusadeName) {
        this.crusadeName = crusadeName;
    }

    public List<Religion> getReligions() {
        return religions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReligionGroup)) {
            return false;
        }
        ReligionGroup that = (ReligionGroup) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
