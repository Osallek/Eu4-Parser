package com.osallek.eu4parser.model.save.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.game.Building;
import com.osallek.eu4parser.model.game.Province;
import com.osallek.eu4parser.model.game.TradeGood;
import com.osallek.eu4parser.model.game.Culture;
import com.osallek.eu4parser.model.game.Religion;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.ListOfDates;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Army;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.country.Modifier;
import com.osallek.eu4parser.model.save.country.Navy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SaveProvince extends com.osallek.eu4parser.model.game.Province {

    //TODO DECOLONIZE PROVINCE: REMOVE: IS_CITY, CLAIMS, CORES, CONTROLLER, OWNER (add natives), TRADE_GOODS = unknown

    private final ClausewitzItem item;

    private final Save save;

    private Country country;

    private ListOfDates flags;

    private Id occupyingRebelFaction;

    private List<ProvinceBuilding> buildings = new ArrayList<>();

    private boolean buildingsUpdated;

    private History history;

    private List<Army> armies;

    private List<Navy> navies;

    private ListOfDates discoveryDates;

    private ListOfDates discoveryReligionDates;

    private List<Modifier> modifiers;

    private Id rebelFaction;

    public SaveProvince(ClausewitzItem item, Province province, Save save) {
        super(province);
        this.item = item;
        this.save = save;
        this.country = this.save.getCountry(ClausewitzUtils.removeQuotes(getOwner()));
        refreshAttributes();
    }

    public Save getSave() {
        return save;
    }

    public Country getCountry() {
        return country;
    }

    //Not override because prefer using the parent when possible
    public int getId() {
        return Math.abs(Integer.parseInt(this.item.getName()));
    }

    @Override
    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public ListOfDates getFlags() {
        return flags;
    }

    public String getTerritorialCore() {
        return this.item.getVarAsString("territorial_core");
    }

    public String getOwner() {
        return this.item.getVarAsString("owner");
    }

    public void setOwner(String owner) {
        owner = ClausewitzUtils.addQuotes(owner);

        if (owner.length() == 5) {
            this.item.setVariable("owner", owner);
            this.country = this.save.getCountry(ClausewitzUtils.removeQuotes(owner));
        }
    }

    public String getControllerTag() {
        return this.item.getVarAsString("controller");
    }

    public Country getController() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getControllerTag()));
    }

    public void setController(String controller) {
        controller = ClausewitzUtils.addQuotes(controller);

        if (controller.length() == 5) {
            if (getPreviousController() != null) {
                setPreviousController(getPreviousController());
            }

            this.item.setVariable("controller", controller);
        }
    }

    public String getPreviousController() {
        return this.item.getVarAsString("previous_controller");
    }

    public void setPreviousController(String previousController) {
        previousController = ClausewitzUtils.addQuotes(previousController);

        if (previousController.length() == 5) {
            this.item.setVariable("previous_controller", previousController);
        }
    }

    public String getOriginalColoniser() {
        return this.item.getVarAsString("original_coloniser");
    }

    public void setOriginalColoniser(String originalColoniser) {
        originalColoniser = ClausewitzUtils.addQuotes(originalColoniser);

        if (originalColoniser.length() == 5) {
            this.item.setVariable("original_coloniser", originalColoniser);
        }
    }

    public Id getOccupyingRebelFaction() {
        return occupyingRebelFaction;
    }

    public List<Double> getInstitutionsProgress() {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return list.getValuesAsDouble();
        }

        return new ArrayList<>();
    }

    public Double getInstitutionsProgress(int institution) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return list.getAsDouble(institution);
        }

        return null;
    }

    public void setInstitutionProgress(int institution, double progress) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            list.set(institution, progress);
        }
    }

    public Integer getEstate() {
        return this.item.getVarAsInt("estate");
    }

    public void setEstate(int estate) {
        this.item.setVariable("estate", estate);
    }

    public void removeEstate() {
        this.item.removeVariable("estate");
        this.save.getCountry(ClausewitzUtils.removeQuotes(this.getOwner()))
                 .getEstates()
                 .forEach(estate -> estate.removeProvince(this.getId()));
    }

    public Date lastEstateGrant() {
        return this.item.getVarAsDate("last_estate_grant");
    }

    public void setLastEstateGrant(Date lastEstateGrant) {
        this.item.setVariable("last_estate_grant", lastEstateGrant);
    }

    public Date getExploitDate() {
        return this.item.getVarAsDate("exploit_date");
    }

    public void setExploitDate(Date exploitDate) {
        this.item.setVariable("exploit_date", exploitDate);
    }

    public List<String> getCoresTags() {
        ClausewitzList list = this.item.getList("cores");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<Country> getCores() {
        return getCoresTags().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public void setCores(List<String> tags) {
        ClausewitzList list = this.item.getList("cores");
        list.clear();

        tags.stream().map(ClausewitzUtils::addQuotes).filter(tag -> tag.length() == 5).forEach(list::add);
    }

    public void addCore(String tag) {
        ClausewitzList list = this.item.getList("cores");

        tag = ClausewitzUtils.addQuotes(tag);

        if (tag.length() == 5 && !list.contains(tag)) {
            list.add(tag);
        }
    }

    public void removeCore(String tag) {
        ClausewitzList list = this.item.getList("cores");

        if (list != null) {
            list.remove(tag);
        }
    }

    public List<String> getClaimsTags() {
        ClausewitzList list = this.item.getList("claims");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<Country> getClaims() {
        return getClaimsTags().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public void setClaims(List<String> tags) {
        ClausewitzList list = this.item.getList("claims");
        list.clear();

        tags.stream().map(ClausewitzUtils::addQuotes).filter(tag -> tag.length() == 5).forEach(list::add);
    }

    public void addClaim(String tag) {
        ClausewitzList list = this.item.getList("claims");

        tag = ClausewitzUtils.addQuotes(tag);

        if (tag.length() == 5 && !list.contains(tag)) {
            list.add(tag);
        }
    }

    public void removeClaim(String tag) {
        ClausewitzList list = this.item.getList("claims");

        if (list != null) {
            list.remove(tag);
        }
    }

    public String getTrade() {
        return this.item.getVarAsString("trade");
    }

    public void setTrade(String trade) {
        trade = ClausewitzUtils.addQuotes(trade);

        this.item.setVariable("trade", trade);
    }

    public List<Army> getArmies() {
        return armies;
    }

    public List<Navy> getNavies() {
        return navies;
    }

    public Boolean activeTradeCompany() {
        return this.item.getVarAsBool("active_trade_company");
    }

    public void setActiveTradeCompany(boolean activeTradeCompany) {
        this.item.setVariable("active_trade_company", activeTradeCompany);
    }

    public Boolean centerOfReligion() {
        return this.item.getVarAsBool("center_of_religion");
    }

    public void setCenterOfReligion(boolean centerOfReligion) {
        this.item.setVariable("center_of_religion", centerOfReligion);
    }

    public String getOriginalCulture() {
        return this.item.getVarAsString("original_culture");
    }

    public void setOriginalCulture(String originalCulture) {
        this.item.setVariable("original_culture", originalCulture);
    }

    public String getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(getCultureName());
    }

    public void setCulture(String culture) {
        this.item.setVariable("culture", culture);
    }

    public String getOriginalReligion() {
        return this.item.getVarAsString("original_religion");
    }

    public void setOriginalReligion(String originalReligion) {
        this.item.setVariable("original_religion", originalReligion);
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public Religion getReligion() {
        return this.save.getGame().getReligion(getReligionName());
    }

    public void setReligion(String religion) {
        this.item.setVariable("religion", religion);
    }

    public String getCapital() {
        return this.item.getVarAsString("capital");
    }

    public void setCapital(String capital) {
        if (getCapital() != null) {
            this.item.setVariable("capital", ClausewitzUtils.addQuotes(capital));
        }
    }

    public boolean isOccupied() {
        return getCountry() != null;
    }

    public boolean isCity() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("is_city"));
    }

    public Double getColonySize() {
        return this.item.getVarAsDouble("colonysize");
    }

    public void setColonySize(double colonySize) {
        if (colonySize < 0d) {
            colonySize = 0d;
        } else if (colonySize > 1000d) {
            colonySize = 1000d;
        }

        this.item.setVariable("colonysize", colonySize);
    }

    public Integer getNativeSizeBeforeMigration() {
        Double value = this.item.getVarAsDouble("native_size_before_migration");

        if (value != null) {
            return value.intValue();
        }

        return null;
    }

    public void setNativeSizeBeforeMigration(int nativeSizeBeforeMigration) {
        this.item.setVariable("native_size_before_migration", (double) nativeSizeBeforeMigration);
    }

    public Double getGarrison() {
        return this.item.getVarAsDouble("garrison");
    }

    public void setGarrison(double garrison) {
        this.item.setVariable("garrison", garrison);
    }

    public Integer getSiege() {
        Double value = this.item.getVarAsDouble("siege");

        if (value != null) {
            return value.intValue();
        }

        return null;
    }

    public void setSiege(int siege) {
        this.item.setVariable("siege", (double) siege);
    }

    public Double getBaseTax() {
        return this.item.getVarAsDouble("base_tax");
    }

    public void setBaseTax(double baseTax) {
        this.item.setVariable("base_tax", baseTax);
    }

    public Double getOriginalTax() {
        return this.item.getVarAsDouble("original_tax");
    }

    public void setOriginalTax(double originalTax) {
        this.item.setVariable("original_tax", originalTax);
    }

    public Double getBaseProduction() {
        return this.item.getVarAsDouble("base_production");
    }

    public void setBaseProduction(double baseProduction) {
        this.item.setVariable("base_production", baseProduction);
    }

    public Double getBaseManpower() {
        return this.item.getVarAsDouble("base_manpower");
    }

    public void setBaseManpower(double baseManpower) {
        this.item.setVariable("base_manpower", baseManpower);
    }

    public Double getUnrest() {
        return this.item.getVarAsDouble("unrest");
    }

    public String getRebels() {
        return this.item.getVarAsString("likely_rebels");
    }

    public Double getMissionaryProgress() {
        return this.item.getVarAsDouble("missionary_progress");
    }

    public void setMissionaryProgress(double missionaryProgress) {
        if (missionaryProgress < 0) {
            missionaryProgress = 0d;
        } else if (missionaryProgress > 100d) {
            missionaryProgress = 100d;
        }

        this.item.setVariable("missionary_progress", missionaryProgress);
    }

    public String getTradeGoods() {
        return this.item.getVarAsString("trade_goods");
    }

    public TradeGood getTradeGood() {
        return this.save.getGame().getTradeGood(getTradeGoods());
    }

    public void setTradeGoods(String tradeGoods) {
        this.item.setVariable("trade_goods", tradeGoods);
    }

    public String getLatentTradeGoods() {
        ClausewitzList list = this.item.getList("latent_trade_goods");

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public TradeGood getLatentTradeGood() {
        return this.save.getGame().getTradeGood(getLatentTradeGoods());
    }

    public void setLatentTradeGoods(String latentTradeGoods) {
        ClausewitzList list = this.item.getList("latent_trade_goods");

        if (list == null) {
            this.item.addList("latent_trade_goods", latentTradeGoods);
        } else {
            list.set(0, latentTradeGoods);
        }
    }

    public Double getDevastation() {
        return this.item.getVarAsDouble("devastation");
    }

    public void setDevastation(double devastation) {
        if (devastation < 0) {
            devastation = 0d;
        } else if (devastation > 100d) {
            devastation = 100d;
        }

        this.item.setVariable("devastation", devastation);
    }

    public Double getLocalAutonomy() {
        return this.item.getVarAsDouble("local_autonomy");
    }

    public void setLocalAutonomy(double localAutonomy) {
        this.item.setVariable("local_autonomy", localAutonomy);
    }

    public Boolean ub() {
        return this.item.getVarAsBool("ub");
    }

    public boolean inHre() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("hre"));
    }

    public void setInHre(boolean inHre) {
        this.item.setVariable("hre", inHre);
    }

    public boolean blockade() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("blockade"));
    }

    public Double getBlockadeEfficiency() {
        return this.item.getVarAsDouble("blockade_efficiency");
    }

    public List<ProvinceBuilding> getBuildings() {
        if (!this.buildingsUpdated) {
            this.buildings = this.buildings.stream()
                                           .map(building -> new ProvinceBuilding(building,
                                                                                 this.save.getGame()
                                                                                          .getBuilding(building.getName())))
                                           .collect(Collectors.toList());
            this.buildingsUpdated = true;
        }

        return this.buildings;
    }

    public List<Building> getAvailableBuildings() {
        List<Building> availableBuildings = new ArrayList<>();

        this.save.getGame().getBuildings().forEach(building -> {
            if (building.onlyNative() && this.country != null && !"native".equals(this.country.getGovernment().getType())) {
                return;
            }

            if (building.onlyInPort() && !isPort()) {
                return;
            }

            if (!building.getManufactoryFor().isEmpty() && !building.getManufactoryFor().contains(getTradeGood())) {
                return;
            }

            availableBuildings.add(building);
        });

        return availableBuildings;
    }

    public List<List<Building>> getAvailableBuildingsTree() {
        return Eu4Utils.buildingsTree(getAvailableBuildings());
    }

    public void addBuilding(String name, String builder) {
        if (isColonizable()) {
            ClausewitzItem buildingsBuildersItem = this.item.getChild("building_builders");
            ClausewitzItem buildingsItem = this.item.getChild("buildings");

            if (buildingsBuildersItem == null) {
                buildingsBuildersItem = this.item.addChild("building_builders");
            }

            if (buildingsItem == null) {
                buildingsItem = this.item.addChild("buildings");
            }

            if (buildingsItem.getVar(name) == null) {
                buildingsItem.addVariable(name, true);
                buildingsBuildersItem.addVariable(name, ClausewitzUtils.addQuotes(builder));
            }

            refreshAttributes();
        }
    }

    public void removeBuilding(String name) {
        ClausewitzItem buildingsBuildersItem = this.item.getChild("building_builders");
        ClausewitzItem buildingsItem = this.item.getChild("buildings");

        if (buildingsBuildersItem != null) {
            buildingsBuildersItem.removeVariable(name);
        }

        if (buildingsItem != null) {
            buildingsItem.removeVariable(name);
        }

        refreshAttributes();
    }

    public History getHistory() {
        return history;
    }

    public List<String> greatProjects() {
        ClausewitzList list = this.item.getList("great_projects");

        if (list != null) {
            return list.getValues();
        } else {
            return new ArrayList<>();
        }
    }

    public Integer getPatrol() {
        return this.item.getVarAsInt("patrol");
    }

    public ListOfDates getDiscoveryDates() {
        return discoveryDates;
    }

    public ListOfDates getDiscoveryReligionDates() {
        return discoveryReligionDates;
    }

    public List<String> getDiscoveredBy() {
        ClausewitzList list = this.item.getList("discovered_by");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public void addDiscoveredBy(String countryId) {
        ClausewitzList list = this.item.getList("discovered_by");

        if (!list.contains(countryId)) {
            list.add(countryId);
        }
    }

    public void removeDiscoveredBy(String countryId) {
        ClausewitzList list = this.item.getList("discovered_by");

        list.remove(countryId);
    }

    public Integer getImproveCount() {
        return this.item.getVarAsInt("improve_count");
    }

    public List<String> getTriggeredModifiers() {
        return this.item.getVarsAsStrings("triggered_modifier");
    }

    public void addTriggeredModifier(String triggeredModifier) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("triggered_modifier");

        if (!ignoreDecisions.contains(triggeredModifier)) {
            this.item.addVariable("triggered_modifier", ClausewitzUtils.addQuotes(triggeredModifier));
        }
    }

    public void removeTriggeredModifier(int index) {
        this.item.removeVariable("triggered_modifier", index);
    }

    public void removeTriggeredModifier(String triggeredModifier) {
        this.item.removeVariable("triggered_modifier", triggeredModifier);
    }

    public List<String> getAppliedTriggeredModifiers() {
        return this.item.getVarsAsStrings("applied_triggered_modifier");
    }

    public void addAppliedTriggeredModifiers(String appliedTriggeredModifier) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("applied_triggered_modifier");

        if (!ignoreDecisions.contains(appliedTriggeredModifier)) {
            this.item.addVariable("applied_triggered_modifier", ClausewitzUtils.addQuotes(appliedTriggeredModifier));
        }
    }

    public void removeAppliedTriggeredModifiers(int index) {
        this.item.removeVariable("applied_triggered_modifier", index);
    }

    public void removeAppliedTriggeredModifiers(String appliedTriggeredModifier) {
        this.item.removeVariable("applied_triggered_modifier", appliedTriggeredModifier);
    }

    public Integer getFormerNativeSize() {
        Double value = this.item.getVarAsDouble("former_native_size");

        if (value != null) {
            return (int) (value * 1000);
        }

        return null;
    }

    public void setFormerNativeSize(int formerNativeSize) {
        this.item.setVariable("former_native_size", ((double) formerNativeSize / 1000));
    }

    public Integer getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public void setNativeHostileness(int nativeHostileness) {
        this.item.setVariable("native_hostileness", nativeHostileness);
    }

    public Integer getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(int nativeFerocity) {
        this.item.setVariable("native_ferocity", nativeFerocity);
    }

    public Integer getNationalism() {
        return this.item.getVarAsInt("nationalism");
    }

    public void setNationalism(int nationalism) {
        this.item.setVariable("nationalism", nationalism);
    }

    public Integer getWinterLevel() {
        return this.item.getVarAsInt("winter");
    }

    public Integer getPreviousWinter() {
        return this.item.getVarAsInt("previous_winter");
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(String modifier, Date date) {
        addModifier(modifier, date, null);
    }

    public void addModifier(String modifier, Date date, Boolean hidden) {
        Modifier.addToItem(this.item, modifier, date, hidden);
        refreshAttributes();
    }

    public void removeModifier(int index) {
        this.item.removeChild("modifier", index);
        refreshAttributes();
    }

    public void removeModifier(String modifier) {
        Integer index = null;
        modifier = ClausewitzUtils.addQuotes(modifier);

        for (int i = 0; i < this.modifiers.size(); i++) {
            if (this.modifiers.get(i).getModifier().equalsIgnoreCase(modifier)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("modifier", index);
            refreshAttributes();
        }
    }

    public Integer getFortInfluencing() {
        return this.item.getVarAsInt("fort_influencing");
    }

    public boolean fortMothballed() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("mothball_command"));
    }

    public void setFortMothballed(boolean fortMothballed) {
        this.item.setVariable("mothball_command", fortMothballed);
    }

    public Double getTradePower() {
        return this.item.getVarAsDouble("trade_power");
    }

    public Id getRebelFaction() {
        return rebelFaction;
    }

    public boolean userChangedName() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("user_changed_name"));
    }

    public boolean hreLiberated() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("hre_liberated"));
    }

    public void setHreLiberated(boolean hreLiberated) {
        this.item.setVariable("hre_liberated", hreLiberated);
    }

    public Double getLootRemaining() {
        return this.item.getVarAsDouble("loot_remaining");
    }

    public void setLootRemaining(double lootRemaining) {
        if (lootRemaining < 0) {
            lootRemaining = 0d;
        } else if (lootRemaining > 1d) {
            lootRemaining = 1d;
        }

        this.item.setVariable("loot_remaining", lootRemaining);
    }

    public Date getLastLooted() {
        return this.item.getVarAsDate("last_looted");
    }

    public void setLastLooted(Date lastLooted) {
        this.item.setVariable("last_looted", lastLooted);
    }

    public Date getLastRazed() {
        return this.item.getVarAsDate("last_razed");
    }

    public void setLastRazed(Date lastRazed) {
        this.item.setVariable("last_razed", lastRazed);
    }

    public String getLastRazedBy() {
        return this.item.getVarAsString("last_razed_by");
    }

    public void setLastRazedBy(String lastRazedBy) {
        this.item.setVariable("last_razed_by", ClausewitzUtils.addQuotes(lastRazedBy));
    }

    public String getLastNativeUprising() {
        return this.item.getVarAsString("last_native_uprising");
    }

    public void setLastNativeUprising(String lastNativeUprising) {
        this.item.setVariable("last_native_uprising", ClausewitzUtils.addQuotes(lastNativeUprising));
    }

    public Integer getCenterOfTrade() {
        return this.item.getVarAsInt("center_of_trade");
    }

    public void setCenterOfTrade(int centerOfTrade) {
        this.item.setVariable("center_of_trade", centerOfTrade);
    }

    public Double getFortFlipProgress() {
        return this.item.getVarAsDouble("fort_flip_progress");
    }

    public Integer getFortFlipperProv() {
        return this.item.getVarAsInt("fort_flipper_prov");
    }

    private void refreshAttributes() {
        ClausewitzItem flagsItem = this.item.getChild("flags");

        if (flagsItem != null) {
            this.flags = new ListOfDates(flagsItem);
        }

        ClausewitzItem occupyingRebelFactionItem = this.item.getChild("occupying_rebel_faction");

        if (occupyingRebelFactionItem != null) {
            this.occupyingRebelFaction = new Id(occupyingRebelFactionItem);
        }

        ClausewitzItem historyItem = this.item.getChild("history");

        if (historyItem != null) {
            this.history = new History(historyItem, this);
        }

        ClausewitzItem buildersItem = this.item.getChild("building_builders");

        if (buildersItem != null) {
            buildersItem.getVariables().forEach(var -> {
                if (historyItem == null) {
                    this.buildings.add(new ProvinceBuilding(var.getName(), var.getValue(), null));
                } else {
                    historyItem.getChildren()
                               .stream()
                               .filter(child -> child.getVar(var.getName()) != null)
                               .findFirst()
                               .ifPresent(child -> this.buildings.add(new ProvinceBuilding(var.getName(), var.getValue(),
                                                                                           Eu4Utils.stringToDate(child.getName()))));
                }
            });
            this.buildings.sort(ProvinceBuilding::compareTo);
        }

        ClausewitzItem discoveryDatesItem = this.item.getChild("discovery_dates2");

        if (discoveryDatesItem != null) {
            this.discoveryDates = new ListOfDates(discoveryDatesItem);
        }

        ClausewitzItem discoveryReligionDatesItem = this.item.getChild("discovery_religion_dates2");

        if (discoveryReligionDatesItem != null) {
            this.discoveryReligionDates = new ListOfDates(discoveryReligionDatesItem);
        }

        List<ClausewitzItem> modifierItems = this.item.getChildren("modifier");
        this.modifiers = modifierItems.stream()
                                      .map(Modifier::new)
                                      .collect(Collectors.toList());

        ClausewitzItem rebelFactionItem = this.item.getChild("rebel_faction");

        if (rebelFactionItem != null) {
            this.rebelFaction = new Id(rebelFactionItem);
        }

        List<ClausewitzItem> unitsItems = this.item.getChildren("unit");

        if (!unitsItems.isEmpty()) {
            this.armies = new ArrayList<>();
            this.navies = new ArrayList<>();
            unitsItems.stream().map(Id::new).map(Id::getId).forEach(unitId -> {
                this.save.getCountries()
                         .values()
                         .stream()
                         .map(c -> c.getArmy(unitId))
                         .filter(Objects::nonNull)
                         .findFirst()
                         .ifPresent(this.armies::add);
                this.save.getCountries()
                         .values()
                         .stream()
                         .map(c -> c.getNavy(unitId))
                         .filter(Objects::nonNull)
                         .findFirst()
                         .ifPresent(this.navies::add);
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SaveProvince)) {
            return false;
        }

        SaveProvince province = (SaveProvince) o;
        return Objects.equals(getId(), province.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
