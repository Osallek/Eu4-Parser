package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProvinceHistoryItem {

    private ClausewitzItem item;

    private final Game game;

    private final String owner;

    private final String controller;

    private final List<String> cores;

    private final List<String> removeCores;

    private final Boolean isCity;

    private final String culture;

    private final String religion;

    private final Integer baseTax;

    private final Integer baseProduction;

    private final Integer baseManpower;

    private final String tradeGoods;

    private final Boolean hre;

    private final String capital;

    private final List<String> discoveredBy;

    private final Boolean reformationCenter;

    private final Integer unrest;

    private final Integer centerOfTrade;

    private final Integer extraCost;

    private List<ModifierApply> permanentModifier;

    private List<ModifierApply> removeModifier;

    private ProvinceRevolt revolt;

    private final List<String> buildings;

    public ProvinceHistoryItem(ClausewitzItem item, Game game, Map<String, Building> buildings) {
        this.item = item;
        this.game = game;
        this.owner = item.getVarAsString("owner");
        this.controller = item.getVarAsString("controller");
        this.cores = item.getVarsAsStrings("add_core");
        this.removeCores = item.getVarsAsStrings("remove_core");
        this.isCity = item.getVarAsBool("is_city");
        this.culture = item.getVarAsString("culture");
        this.religion = item.getVarAsString("religion");
        this.baseTax = item.getVarAsInt("base_tax");
        this.baseProduction = item.getVarAsInt("base_production");
        this.baseManpower = item.getVarAsInt("base_manpower");
        this.tradeGoods = item.getVarAsString("trade_goods");
        this.hre = item.getVarAsBool("hre");
        this.capital = ClausewitzUtils.removeQuotes(item.getVarAsString("capital"));
        this.discoveredBy = item.getVarsAsStrings("discovered_by");
        this.reformationCenter = item.getVarAsBool("reformation_center");
        this.unrest = item.getVarAsInt("unrest");
        this.centerOfTrade = item.getVarAsInt("center_of_trade");
        this.extraCost = item.getVarAsInt("extra_cost");

        if (item.hasChild("add_permanent_province_modifier")) {
            this.permanentModifier = item.getChildren("add_permanent_province_modifier").stream().map(ModifierApply::new).collect(Collectors.toList());
        }

        if (item.hasChild("remove_province_modifier")) {
            this.removeModifier = item.getChildren("remove_province_modifier").stream().map(ModifierApply::new).collect(Collectors.toList());
        }

        if (item.hasChild("revolt")) {
            this.revolt = new ProvinceRevolt(item.getChild("revolt"));
        }

        this.buildings = buildings.keySet().stream().filter(item::hasChild).collect(Collectors.toList());
    }

    public Country getOwner() {
        ClausewitzVariable variable = this.item.getVar("owner");
        return variable == null ? null : this.game.getCountry(variable.getValue());
    }

    public void setOwner(Country owner) {
        setOwner(owner.getTag());
    }

    public void setOwner(String owner) {
        this.item.setVariable("owner", owner);
    }

    public Country getController() {
        ClausewitzVariable variable = this.item.getVar("controller");
        return variable == null ? null : this.game.getCountry(variable.getValue());
    }

    public void setController(Country controller) {
        setController(controller.getTag());
    }

    public void setController(String controller) {
        this.item.setVariable("controller", controller);
    }

    public List<String> getCores() {
        return cores;
    }

    public List<String> getRemoveCores() {
        return removeCores;
    }

    public Boolean getCity() {
        return isCity;
    }

    public String getCulture() {
        return culture;
    }

    public String getReligion() {
        return religion;
    }

    public Integer getBaseTax() {
        return baseTax;
    }

    public Integer getBaseProduction() {
        return baseProduction;
    }

    public Integer getBaseManpower() {
        return baseManpower;
    }

    public String getTradeGoods() {
        return tradeGoods;
    }

    public Boolean getHre() {
        return hre;
    }

    public String getCapital() {
        return capital;
    }

    public List<String> getDiscoveredBy() {
        return discoveredBy;
    }

    public Boolean getReformationCenter() {
        return reformationCenter;
    }

    public Integer getUnrest() {
        return unrest;
    }

    public Integer getCenterOfTrade() {
        return centerOfTrade;
    }

    public Integer getExtraCost() {
        return extraCost;
    }

    public List<ModifierApply> getPermanentModifier() {
        return permanentModifier;
    }

    public List<ModifierApply> getRemoveModifier() {
        return removeModifier;
    }

    public ProvinceRevolt getRevolt() {
        return revolt;
    }

    public List<String> getBuildings() {
        return buildings;
    }
}
