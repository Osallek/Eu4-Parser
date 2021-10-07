package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Building {

    private final ClausewitzItem item;

    private final Game game;

    public Building(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile("GFX_" + getName());
    }

    public Integer getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(Integer cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Integer getTime() {
        return this.item.getVarAsInt("time");
    }

    public void setTime(Integer time) {
        if (time == null) {
            this.item.removeVariable("time");
        } else {
            this.item.setVariable("time", time);
        }
    }

    public String getMakeObsolete() {
        return this.item.getVarAsString("make_obsolete");
    }

    public void setMakeObsolete(String makeObsolete) {
        if (StringUtils.isBlank(makeObsolete)) {
            this.item.removeVariable("make_obsolete");
        } else {
            this.item.setVariable("make_obsolete", makeObsolete);
        }
    }

    public boolean makeObsolete() {
        return StringUtils.isNotBlank(getMakeObsolete());
    }

    public Building getMakeObsoleteBuilding() {
        return StringUtils.isBlank(getMakeObsolete()) ? null : this.game.getBuilding(getMakeObsolete());
    }

    public Boolean onePerCountry() {
        return this.item.getVarAsBool("one_per_country");
    }

    public void setOnePerCountry(Boolean oncePerCountry) {
        if (oncePerCountry == null) {
            this.item.removeVariable("one_per_country");
        } else {
            this.item.setVariable("one_per_country", oncePerCountry);
        }
    }

    public Boolean allowInGoldProvince() {
        return this.item.getVarAsBool("allow_in_gold_provinces");
    }

    public void setAllowInGoldProvinces(Boolean allowInGoldProvince) {
        if (allowInGoldProvince == null) {
            this.item.removeVariable("allow_in_gold_provinces");
        } else {
            this.item.setVariable("allow_in_gold_provinces", allowInGoldProvince);
        }
    }

    public Boolean indestructible() {
        return this.item.getVarAsBool("indestructible");
    }

    public void setIndestructible(Boolean indestructible) {
        if (indestructible == null) {
            this.item.removeVariable("indestructible");
        } else {
            this.item.setVariable("indestructible", indestructible);
        }
    }

    public Boolean onMap() {
        return this.item.getVarAsBool("onmap");
    }

    public void setOnMap(Boolean onMap) {
        if (onMap == null) {
            this.item.removeVariable("onmap");
        } else {
            this.item.setVariable("onmap", onMap);
        }
    }

    public Boolean influencingFort() {
        return this.item.getVarAsBool("influencing_fort");
    }

    public void setInfluencingFort(Boolean influencingFort) {
        if (influencingFort == null) {
            this.item.removeVariable("influencing_fort");
        } else {
            this.item.setVariable("influencing_fort", influencingFort);
        }
    }

    public List<TradeGood> getManufactoryFor() {
        ClausewitzList list = this.item.getList("manufactory");
        return list == null ? null : list.getValues().stream().map(this.game::getTradeGood).collect(Collectors.toList());
    }

    public void setManufactoryFor(List<String> manufactoryFor) {
        if (CollectionUtils.isEmpty(manufactoryFor)) {
            this.item.removeList("manufactory");
            return;
        }

        ClausewitzList list = this.item.getList("manufactory");

        if (list != null) {
            list.setAll(manufactoryFor.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("manufactory", manufactoryFor.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<TradeGood> getBonusManufactory() {
        ClausewitzList list = this.item.getList("bonus_manufactory");
        return list == null ? null : list.getValues().stream().map(this.game::getTradeGood).collect(Collectors.toList());
    }

    public void setBonusManufactory(List<String> bonusManufactory) {
        if (CollectionUtils.isEmpty(bonusManufactory)) {
            this.item.removeList("bonus_manufactory");
            return;
        }

        ClausewitzList list = this.item.getList("bonus_manufactory");

        if (list != null) {
            list.setAll(bonusManufactory.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("bonus_manufactory", bonusManufactory.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public Boolean governmentSpecific() {
        return this.item.getVarAsBool("government_specific");
    }

    public void setGovernmentSpecific(Boolean governmentSpecific) {
        if (governmentSpecific == null) {
            this.item.removeVariable("government_specific");
        } else {
            this.item.setVariable("government_specific", governmentSpecific);
        }
    }

    public Boolean showSeparate() {
        return this.item.getVarAsBool("show_separate");
    }

    public void setShowSeparate(Boolean showSeparate) {
        if (showSeparate == null) {
            this.item.removeVariable("show_separate");
        } else {
            this.item.setVariable("show_separate", showSeparate);
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public boolean onlyInPort() {
        Condition condition = getTrigger();
        return condition != null && condition.getCondition("has_port").equals("yes");
    }

    public boolean onlyNative() {
        Condition condition = getTrigger();

        if (condition == null) {
            return false;
        }

        List<Condition> conditions = condition.getScopes("owner");

        if (CollectionUtils.isEmpty(conditions)) {
            return false;
        }

        return conditions.stream().anyMatch(c -> c.getConditions().get("government").stream().anyMatch("native"::equals));
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("build_trigger");
        return child == null ? null : new Condition(child);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Building)) {
            return false;
        }

        Building building = (Building) o;

        return Objects.equals(getName(), building.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
