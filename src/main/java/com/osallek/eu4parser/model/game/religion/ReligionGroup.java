package com.osallek.eu4parser.model.game.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReligionGroup {

    private final ClausewitzItem item;

    private List<Religion> religions;

    public ReligionGroup(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public boolean defenderOfFaith() {
        return this.item.getVarAsBool("defender_of_faith");
    }

    public void setDefenderOfFaith(boolean defenderOfFaith) {
        this.item.setVariable("defender_of_faith ", defenderOfFaith);
    }

    public boolean canFormPersonalUnions() {
        return this.item.getVarAsBool("can_form_personal_unions");
    }

    public void setCanFormPersonalUnions(boolean canFormPersonalUnions) {
        this.item.setVariable("can_form_personal_unions", canFormPersonalUnions);
    }

    public Integer centerOfReligion() {
        return this.item.getVarAsInt("center_of_religion");
    }

    public void setCenterOfReligion(int centerOfReligion) {
        this.item.setVariable("center_of_religion", centerOfReligion);
    }

    public Integer getFlagsWithEmblemPercentage() {
        return this.item.getVarAsInt("flags_with_emblem_percentage");
    }

    public void setFlagsWithEmblemPercentage(int flagsWithEmblemPercentage) {
        if (flagsWithEmblemPercentage < 0) {
            flagsWithEmblemPercentage = 0;
        } else if (flagsWithEmblemPercentage > 100) {
            flagsWithEmblemPercentage = 100;
        }

        this.item.setVariable("flags_with_emblem_percentage", flagsWithEmblemPercentage);
    }

    public Map.Entry<Integer, Integer> getFlagEmblemIndexRange() {
        ClausewitzList list = this.item.getList("flag_emblem_index_range");

        if (list != null) {
            return new AbstractMap.SimpleEntry<>(list.getAsInt(0), list.getAsInt(1));
        }

        return null;
    }

    public void setFlagEmblemIndexRange(int flagEmblemIndexRangeMin, int flagEmblemIndexRangeMax) {
        ClausewitzList list = this.item.getList("flag_emblem_index_range");

        if (list != null) {
            list.set(0, flagEmblemIndexRangeMin);
            list.set(1, flagEmblemIndexRangeMax);
        } else {
            this.item.addList("flag_emblem_index_range", flagEmblemIndexRangeMin, flagEmblemIndexRangeMax);
        }
    }

    public String harmonizedModifier() {
        return this.item.getVarAsString("harmonized_modifier");
    }

    public void setHarmonizedModifier(String harmonizedModifier) {
        this.item.setVariable("harmonized_modifier", harmonizedModifier);
    }

    public String crusadeName() {
        return this.item.getVarAsString("crusade_name");
    }

    public void setCrusadeName(String crusadeName) {
        this.item.setVariable("crusade_name", crusadeName);
    }

    public List<Religion> getReligions() {
        return religions;
    }

    private void refreshAttributes() {
        this.religions = this.item.getChildren()
                                  .stream()
                                  .map(child -> new Religion(child, this))
                                  .collect(Collectors.toList());
    }
}
