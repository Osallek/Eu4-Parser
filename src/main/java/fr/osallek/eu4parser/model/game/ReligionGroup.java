package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReligionGroup {

    private final ClausewitzItem item;

    private final Game game;

    private List<ReligiousSchool> religiousSchools;

    public ReligionGroup(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public boolean defenderOfFaith() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("defender_of_faith"));
    }

    public void setDefenderOfFaith(Boolean primitive) {
        if (primitive == null) {
            this.item.removeVariable("defender_of_faith");
        } else {
            this.item.setVariable("defender_of_faith", primitive);
        }
    }

    public boolean canFormPersonalUnions() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("can_form_personal_unions"));
    }

    public void setCanFormPersonalUnions(Boolean primitive) {
        if (primitive == null) {
            this.item.removeVariable("can_form_personal_unions");
        } else {
            this.item.setVariable("can_form_personal_unions", primitive);
        }
    }

    public Integer getCenterOfReligion() {
        return this.item.getVarAsInt("center_of_religion");
    }

    public void setCenterOfReligion(Integer centerOfReligion) {
        if (centerOfReligion == null) {
            this.item.removeVariable("center_of_religion");
        } else {
            this.item.setVariable("center_of_religion", centerOfReligion);
        }
    }

    public Integer getFlagsWithEmblemPercentage() {
        return this.item.getVarAsInt("flags_with_emblem_percentage");
    }

    public void setFlagsWithEmblemPercentage(Integer flagsWithEmblemPercentage) {
        if (flagsWithEmblemPercentage == null) {
            this.item.removeVariable("flags_with_emblem_percentage");
        } else {
            if (flagsWithEmblemPercentage < 0) {
                flagsWithEmblemPercentage = 0;
            } else if (flagsWithEmblemPercentage > 100) {
                flagsWithEmblemPercentage = 100;
            }

            this.item.setVariable("flags_with_emblem_percentage", flagsWithEmblemPercentage);
        }
    }

    public Pair<Integer, Integer> getFlagEmblemIndexRange() {
        ClausewitzList list = this.item.getList("flag_emblem_index_range");
        return list == null ? null : Pair.of(list.getAsInt(0), list.getAsInt(1));
    }

    public void setFlagEmblemIndexRange(Integer flagEmblemIndexRangeMin, Integer flagEmblemIndexRangeMax) {
        if (flagEmblemIndexRangeMin == null || flagEmblemIndexRangeMax == null) {
            this.item.removeList("flag_emblem_index_range");
        } else {
            ClausewitzList list = this.item.getList("flag_emblem_index_range");

            if (list != null) {
                list.set(0, flagEmblemIndexRangeMin);
                list.set(1, flagEmblemIndexRangeMax);
            } else {
                this.item.addList("flag_emblem_index_range", flagEmblemIndexRangeMin, flagEmblemIndexRangeMax);
            }
        }
    }

    public String getHarmonizedModifier() {
        return this.item.getVarAsString("harmonized_modifier");
    }

    public void setHarmonizedModifier(String harmonizedModifier) {
        if (StringUtils.isBlank(harmonizedModifier)) {
            this.item.removeVariable("harmonized_modifier");
        } else {
            this.item.setVariable("harmonized_modifier", harmonizedModifier);
        }
    }

    public String getCrusadeName() {
        return this.item.getVarAsString("crusade_name");
    }

    public void setCrusadeName(String crusadeName) {
        if (StringUtils.isBlank(crusadeName)) {
            this.item.removeVariable("crusade_name");
        } else {
            this.item.setVariable("crusade_name", crusadeName);
        }
    }

    public List<Religion> getReligions() {
        return this.item.getChildrenNot("religious_schools")
                        .stream()
                        .map(child -> new Religion(child, this))
                        .toList();
    }

    public List<ReligiousSchool> getReligiousSchools() {
        if (this.religiousSchools == null) {
            this.religiousSchools = Optional.ofNullable(this.item.getChild("religious_schools"))
                                            .map(i -> i.getChildren().stream().map(ii -> new ReligiousSchool(ii, this.game)).toList())
                                            .orElse(new ArrayList<>());
        }

        return this.religiousSchools;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ReligionGroup religionGroup)) {
            return false;
        }

        return Objects.equals(getName(), religionGroup.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
