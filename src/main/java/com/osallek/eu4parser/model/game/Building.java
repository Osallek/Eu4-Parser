package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Building {

    private final Game game;

    private String name;

    private String localizedName;

    private Integer cost;

    private Integer time;

    //Do this because manufactories cost/time/modifier are in another object, so we still want them but not written in the object
    private Integer internalCost;

    private Integer internalTime;

    private String makeObsolete;

    private boolean oncePerCountry;

    private boolean allowInGoldProvince;

    private boolean indestructible;

    private boolean onMap;

    private boolean influencingFort;

    private final List<TradeGood> manufactoryFor;

    private final List<TradeGood> bonusManufactory;

    private boolean governmentSpecific;

    private boolean showSeparate;

    private final Map<String, String> modifiers;

    private Map<String, String> internalModifiers;

    private final boolean onlyInPort;

    private final boolean onlyNatives;

    private final Condition trigger;

    public Building(Building other) {
        this.game = other.game;
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.cost = other.cost;
        this.time = other.time;
        this.internalCost = other.internalCost;
        this.internalTime = other.internalTime;
        this.makeObsolete = other.makeObsolete;
        this.oncePerCountry = other.oncePerCountry;
        this.allowInGoldProvince = other.allowInGoldProvince;
        this.indestructible = other.indestructible;
        this.onMap = other.onMap;
        this.influencingFort = other.influencingFort;
        this.manufactoryFor = other.manufactoryFor;
        this.bonusManufactory = other.bonusManufactory;
        this.governmentSpecific = other.governmentSpecific;
        this.showSeparate = other.showSeparate;
        this.modifiers = other.modifiers;
        this.internalModifiers = other.internalModifiers;
        this.onlyInPort = other.onlyInPort;
        this.onlyNatives = other.onlyNatives;
        this.trigger = other.trigger;
    }

    public Building(ClausewitzItem item, Game game) {
        this.game = game;
        this.name = item.getName();
        this.cost = item.getVarAsInt("cost");
        this.time = item.getVarAsInt("time");
        this.makeObsolete = item.getVarAsString("make_obsolete");
        this.oncePerCountry = BooleanUtils.toBoolean(item.getVarAsBool("one_per_country"));
        this.allowInGoldProvince = BooleanUtils.toBoolean(item.getVarAsBool("allow_in_gold_provinces"));
        this.indestructible = BooleanUtils.toBoolean(item.getVarAsBool("indestructible"));
        this.onMap = BooleanUtils.toBoolean(item.getVarAsBool("onmap"));
        this.influencingFort = BooleanUtils.toBoolean(item.getVarAsBool("influencing_fort"));

        ClausewitzList list = item.getList("manufactory");
        this.manufactoryFor = list == null ? new ArrayList<>() :
                              list.getValues().stream().map(this.game::getTradeGood).collect(Collectors.toList());

        list = item.getList("bonus_manufactory");
        this.bonusManufactory = list == null ? new ArrayList<>() :
                                list.getValues().stream().map(this.game::getTradeGood).collect(Collectors.toList());
        this.governmentSpecific = BooleanUtils.toBoolean(item.getVarAsBool("government_specific"));
        this.showSeparate = BooleanUtils.toBoolean(item.getVarAsBool("show_separate"));

        ClausewitzItem child = item.getChild("modifier");

        this.modifiers = child == null ? new LinkedHashMap<>()
                                       : child.getVariables()
                                              .stream()
                                              .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                        ClausewitzVariable::getValue,
                                                                        (a, b) -> b,
                                                                        LinkedHashMap::new));

        child = item.getChild("build_trigger");
        this.onlyInPort = child != null && BooleanUtils.toBoolean(child.getVarAsBool("has_port"));
        this.trigger = child == null ? null : new Condition(child);

        child = item.getChild("build_trigger");
        this.onlyNatives = child != null
                           && (child = child.getChild("owner")) != null
                           && "native".equals(child.getVarAsString("government"));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile("GFX_" + getName());
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Integer getCost() {
        return this.cost == null ? this.internalCost : this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    void setInternalCost(int cost) {
        this.internalCost = cost;
    }

    public Integer getTime() {
        return this.time == null ? this.internalTime : this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    void setInternalTime(int time) {
        this.internalTime = time;
    }

    public String getMakeObsolete() {
        return this.makeObsolete;
    }

    public boolean makeObsolete() {
        return getMakeObsolete() != null;
    }

    public Building getMakeObsoleteBuilding() {
        return getMakeObsolete() == null ? null : this.game.getBuilding(getMakeObsolete());
    }

    public void setMakeObsolete(String makeObsolete) {
        this.makeObsolete = makeObsolete;
    }

    public boolean onePerCountry() {
        return this.oncePerCountry;
    }

    public void setOnePerCountry(boolean onePerCountry) {
        this.oncePerCountry = onePerCountry;
    }

    public boolean allowInGoldProvinces() {
        return this.allowInGoldProvince;
    }

    public void setAllowInGoldProvinces(boolean allowInGoldProvinces) {
        this.allowInGoldProvince = allowInGoldProvinces;
    }

    public boolean indestructible() {
        return this.indestructible;
    }

    public void setIndestructible(boolean indestructible) {
        this.indestructible = indestructible;
    }

    public boolean onMap() {
        return this.onMap;
    }

    public void setOnMap(boolean onMap) {
        this.onMap = onMap;
    }

    public boolean influencingFort() {
        return this.influencingFort;
    }

    public void setInfluencingFort(boolean influencingFort) {
        this.influencingFort = influencingFort;
    }

    public List<TradeGood> getManufactoryFor() {
        return this.manufactoryFor;
    }

    public void addManufactoryFor(TradeGood tradeGood) {
        for (Building building : this.game.getBuildings()) {
            if (!building.equals(this) && building.getManufactoryFor().contains(tradeGood)) {
                building.removeManufactoryFor(tradeGood);
                break; //Each trade good can only have one manufactory
            }
        }

        this.manufactoryFor.add(tradeGood);
    }

    public void removeManufactoryFor(TradeGood tradeGood) {
        this.manufactoryFor.remove(tradeGood);
    }

    public List<TradeGood> getBonusManufactory() {
        return this.bonusManufactory;
    }

    public void addBonusManufactory(TradeGood tradeGood) {
        for (Building building : this.game.getBuildings()) {
            if (!building.equals(this) && building.getBonusManufactory().contains(tradeGood)) {
                building.removeBonusManufactory(tradeGood);
                break; //Each trade good can only have one bonus
            }
        }

        this.bonusManufactory.add(tradeGood);
    }

    public void removeBonusManufactory(TradeGood tradeGood) {
        this.bonusManufactory.remove(tradeGood);
    }

    public boolean governmentSpecific() {
        return this.governmentSpecific;
    }

    public void setGovernmentSpecific(boolean governmentSpecific) {
        this.governmentSpecific = governmentSpecific;
    }

    public boolean showSeparate() {
        return this.showSeparate;
    }

    public void setShowSeparate(boolean showSeparate) {
        this.showSeparate = showSeparate;
    }

    public Map<String, String> getModifiers() {
        Map<String, String> map = this.internalModifiers == null ? new LinkedHashMap<>()
                                                                 : new LinkedHashMap<>(this.internalModifiers);
        map.putAll(this.modifiers);
        return map;
    }

    public void addModifier(String modifier, String quantity) {
        this.modifiers.put(modifier, quantity);
    }

    public void setInternalModifiers(Map<String, String> internalModifiers) {
        this.internalModifiers = internalModifiers;
    }

    public void removeModifier(String modifier) {
        this.modifiers.remove(modifier);
    }

    public boolean onlyInPort() {
        return this.onlyInPort;
    }

    public boolean onlyNative() {
        return this.onlyNatives;
    }

    public Condition getTrigger() {
        return trigger;
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
