package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Building {

    private final ClausewitzItem item;

    private final Game game;

    private String localizedName;

    //Do this because manufactories cost/time/modifier are in another object, so we still want them but not written in the object
    private Integer internalCost;

    private Integer internalTime;

    private Map<String, Double> internalModifiers;

    public Building(Building other) {
        this.item = other.item;
        this.game = other.game;
        this.localizedName = other.localizedName;
        this.internalCost = other.internalCost;
        this.internalTime = other.internalTime;
        this.internalModifiers = other.internalModifiers;
    }

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
        SpriteType spriteType = this.game.getSpriteType("GFX_" + getName());

        if (spriteType == null) {
            return null;
        }

        File file = new File(this.game.getGameFolderPath() + File.separator
                             + ClausewitzUtils.removeQuotes(spriteType.getTextureFile()));

        if (file.exists()) {
            return file;
        }

        //Fix some time files are not rightly registered (I don't know how the game loads them...)
        if (file.toString().endsWith(".tga")) {
            return new File(file.toString().replace(".tga", ".dds"));
        } else if (file.toString().endsWith(".dds")) {
            return new File(file.toString().replace(".dds", ".tga"));
        }

        return null;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Integer getCost() {
        Integer cost = this.item.getVarAsInt("cost");

        if (cost == null) {
            return this.internalCost;
        }

        return cost;
    }

    public void setCost(int cost) {
        this.item.setVariable("cost", cost);
    }

    void setInternalCost(int cost) {
        this.internalCost = cost;
    }

    public Integer getTime() {
        Integer time = this.item.getVarAsInt("time");

        if (time == null) {
            return this.internalTime;
        }

        return time;
    }

    public void setTime(int time) {
        this.item.setVariable("time", time);
    }

    void setInternalTime(int time) {
        this.internalTime = time;
    }

    public String getMakeObsolete() {
        return this.item.getVarAsString("make_obsolete");
    }

    public boolean makeObsolete() {
        return getMakeObsolete() != null;
    }

    public Building getMakeObsoleteBuilding() {
        return getMakeObsolete() == null ? null : this.game.getBuilding(getMakeObsolete());
    }

    public void setMakeObsolete(int makeObsolete) {
        this.item.setVariable("make_obsolete", makeObsolete);
    }

    public boolean onePerCountry() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("one_per_country"));
    }

    public void setOnePerCountry(boolean onePerCountry) {
        this.item.setVariable("one_per_country", onePerCountry);
    }

    public boolean allowInGoldProvinces() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("allow_in_gold_provinces"));
    }

    public void setAllowInGoldProvinces(boolean allowInGoldProvinces) {
        this.item.setVariable("allow_in_gold_provinces", allowInGoldProvinces);
    }

    public boolean indestructible() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("indestructible"));
    }

    public void setIndestructible(boolean indestructible) {
        this.item.setVariable("indestructible", indestructible);
    }

    public boolean onMap() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("onmap"));
    }

    public void setOnMap(boolean onMap) {
        this.item.setVariable("onmap", onMap);
    }

    public boolean influencingFort() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("influencing_fort"));
    }

    public void setInfluencingFort(boolean influencingFort) {
        this.item.setVariable("influencing_fort", influencingFort);
    }

    public List<TradeGood> getManufactoryFor() {
        ClausewitzList list = this.item.getList("manufactory");

        if (list != null) {
            return list.getValues().stream().map(this.game::getTradeGood).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public void addManufactoryFor(TradeGood tradeGood) {
        ClausewitzList list = this.item.getList("manufactory");

        if (list != null) {
            for (Building building : this.game.getBuildings()) {
                if (!building.equals(this) && building.getManufactoryFor().contains(tradeGood)) {
                    building.removeManufactoryFor(tradeGood);
                    break; //Each trade good can only have one manufactory
                }
            }

            list.add(tradeGood.getName());
        }
    }

    public void removeManufactoryFor(TradeGood tradeGood) {
        ClausewitzList list = this.item.getList("manufactory");

        if (list != null) {
            list.remove(tradeGood.getName());
        }
    }

    public boolean governmentSpecific() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("government_specific"));
    }

    public void setGovernmentSpecific(boolean governmentSpecific) {
        this.item.setVariable("government_specific", governmentSpecific);
    }

    public boolean showSeparate() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("show_separate"));
    }

    public void setShowSeparate(boolean showSeparate) {
        this.item.setVariable("show_separate", showSeparate);
    }

    public Map<String, Double> getModifiers() {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            return modifiersItem.getVariables()
                                .stream()
                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                          ClausewitzVariable::getAsDouble,
                                                          (a, b) -> b,
                                                          LinkedHashMap::new));
        }

        return this.internalModifiers == null ? new LinkedHashMap<>() : this.internalModifiers;
    }

    public void addModifier(String modifier, Double quantity) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.setVariable(modifier, quantity);
        }
    }

    public void setInternalModifiers(Map<String, Double> internalModifiers) {
        this.internalModifiers = internalModifiers;
    }

    public void removeModifier(String modifier) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.removeVariable(modifier);
        }
    }

    public boolean onlyInPort() {
        ClausewitzItem clausewitzItem = this.item.getChild("trigger");

        return clausewitzItem != null && Boolean.TRUE.equals(clausewitzItem.getVarAsBool("has_port"));
    }

    public boolean onlyNative() {
        ClausewitzItem clausewitzItem = this.item.getChild("trigger");

        if (clausewitzItem != null) {
            ClausewitzItem ownerChild = clausewitzItem.getChild("owner");

            if (ownerChild != null) {
                return "native".equals(ownerChild.getVarAsString("government"));
            }
        }

        return false;
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
