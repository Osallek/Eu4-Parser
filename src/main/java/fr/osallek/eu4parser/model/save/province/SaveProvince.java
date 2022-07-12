package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.UnitType;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.CenterOfTrade;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.GameModifier;
import fr.osallek.eu4parser.model.game.GreatProject;
import fr.osallek.eu4parser.model.game.ImperialReform;
import fr.osallek.eu4parser.model.game.Institution;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.game.StaticModifier;
import fr.osallek.eu4parser.model.game.StaticModifiers;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.game.TradeNode;
import fr.osallek.eu4parser.model.game.TradePolicy;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.AbstractRegiment;
import fr.osallek.eu4parser.model.save.country.Army;
import fr.osallek.eu4parser.model.save.country.CountryState;
import fr.osallek.eu4parser.model.save.country.Navy;
import fr.osallek.eu4parser.model.save.country.Regiment;
import fr.osallek.eu4parser.model.save.country.SaveArea;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveInvestment;
import fr.osallek.eu4parser.model.save.country.SaveModifier;
import fr.osallek.eu4parser.model.save.country.Ship;
import fr.osallek.eu4parser.model.save.trade.TradeNodeCountry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveProvince extends Province {

    //Todo DECOLONIZE PROVINCE: REMOVE: IS_CITY, CLAIMS, CORES, CONTROLLER, OWNER (add natives), TRADE_GOODS = unknown

    private final ClausewitzItem item;

    private final Save save;

    private SaveCountry country;

    private SaveArea area;

    private ListOfDates flags;

    private Id occupyingRebelFaction;

    private SeatInParliament seatInParliament;

    private List<ProvinceBuilding> buildings;

    private boolean buildingsUpdated;

    private SaveProvinceHistory history;

    private List<Army> armies;

    private List<Navy> navies;

    private ListOfDates discoveryDates;

    private ListOfDates discoveryReligionDates;

    private Map<String, Integer> improveCount;

    private Map<String, SaveModifier> modifiers;

    private Id rebelFaction;

    private ProvinceConstruction buildingConstruction;

    private ProvinceConstruction colonyConstruction;

    private ProvinceConstruction missionaryConstruction;

    private Double localAutonomy;

    public SaveProvince(ClausewitzItem item, Province province, Save save) {
        super(province);
        this.item = item;
        this.save = save;
        this.country = this.save.getCountry(ClausewitzUtils.removeQuotes(getOwnerTag()));
        refreshAttributes();
    }

    public Save getSave() {
        return save;
    }

    public SaveCountry getOwner() {
        return country;
    }

    //Not override because prefer using the parent when possible
    public int getId() {
        return Math.abs(Integer.parseInt(this.item.getName()));
    }

    public SaveArea getSaveArea() {
        return area;
    }

    public void setSaveArea(SaveArea area) {
        this.area = area;
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

    public String getOwnerTag() {
        return this.item.getVarAsString("owner");
    }

    public void setOwner(SaveCountry owner) {
        if (!Objects.equals(this.country, owner)) {
            if (this.country != null) {
                this.country.removeOwnedProvince(this);
            }

            this.item.setVariable("owner", ClausewitzUtils.addQuotes(owner.getTag()));
            this.country = owner;
            this.country.addOwnedProvince(this);
            this.history.addEvent(this.save.getDate(), "owner", ClausewitzUtils.addQuotes(owner.getTag()));
        }
    }

    public String getControllerTag() {
        return this.item.getVarAsString("controller");
    }

    public SaveCountry getController() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getControllerTag()));
    }

    public void setController(SaveCountry controller) {
        if (getController() != null) {
            getController().removeControlledProvince(this);
            setPreviousController(getController());
        }

        this.item.setVariable("controller", ClausewitzUtils.addQuotes(controller.getTag()));
        controller.addControlledProvince(this);
        this.history.addEvent(this.save.getDate(), "controller", "tag", ClausewitzUtils.addQuotes(controller.getTag()));
    }

    public String getPreviousControllerTag() {
        return this.item.getVarAsString("previous_controller");
    }

    public SaveCountry getPreviousController() {
        return this.save.getCountry(getPreviousControllerTag());
    }

    public void setPreviousController(SaveCountry previousController) {
        this.item.setVariable("previous_controller", ClausewitzUtils.addQuotes(previousController.getTag()));
    }

    public String getOriginalColoniserTag() {
        return this.item.getVarAsString("original_coloniser");
    }

    public SaveCountry getOriginalColoniser() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getOriginalColoniserTag()));
    }

    public void setOriginalColoniser(SaveCountry originalColoniser) {
        this.item.setVariable("original_coloniser", ClausewitzUtils.addQuotes(originalColoniser.getTag()));
    }

    public Id getOccupyingRebelFaction() {
        return occupyingRebelFaction;
    }

    public SeatInParliament getSeatInParliament() {
        return this.seatInParliament;
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

    public void setInstitutionProgress(Institution institution, double progress) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            list.set(institution.getIndex(), progress);
        }
    }

    public LocalDate getExploitDate() {
        return this.item.getVarAsDate("exploit_date");
    }

    public void setExploitDate(LocalDate exploitDate) {
        this.item.setVariable("exploit_date", exploitDate);
    }

    public List<String> getCoresTags() {
        ClausewitzList list = this.item.getList("cores");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<SaveCountry> getCores() {
        return getCoresTags().stream().map(this.save::getCountry).toList();
    }

    public void setCores(List<SaveCountry> countries) {
        getCores().forEach(core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove,
                                                                                                              () -> removeCore(core)));

        countries.forEach(this::addCore);
    }

    public void addCore(SaveCountry country) {
        ClausewitzList list = this.item.getList("cores");

        if (!list.contains(country.getTag())) {
            list.add(country.getTag());
            country.addCoreProvince(this);
            this.history.addEvent(this.save.getDate(), "add_core", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public void removeCore(SaveCountry country) {
        ClausewitzList list = this.item.getList("cores");

        if (list != null) {
            list.remove(country.getTag());
            country.removeCoreProvince(this);
            this.history.addEvent(this.save.getDate(), "remove_core", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public List<String> getClaimsTags() {
        ClausewitzList list = this.item.getList("claims");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<SaveCountry> getClaims() {
        return getClaimsTags().stream().map(this.save::getCountry).toList();
    }

    public void setClaims(List<SaveCountry> countries) {
        getClaims().forEach(core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove,
                                                                                                               () -> removeClaim(core)));

        countries.forEach(this::addClaim);
    }

    public void addClaim(SaveCountry country) {
        ClausewitzList list = this.item.getList("claims");

        if (!list.contains(country.getTag())) {
            list.add(country.getTag());
            country.addClaimProvince(this);
            this.history.addEvent(this.save.getDate(), "add_claim", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public void removeClaim(SaveCountry country) {
        ClausewitzList list = this.item.getList("claims");

        if (list != null) {
            list.remove(country.getTag());
            country.removeClaimProvince(this);
            this.history.addEvent(this.save.getDate(), "remove_claim", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public String getTrade() {
        return this.item.getVarAsString("trade");
    }

    public TradeNode getTradeNode() {
        return this.save.getGame().getTradeNode(ClausewitzUtils.removeQuotes(getTrade()));
    }

    public void setTradeNode(TradeNode tradeNode) {
        this.item.setVariable("trade", ClausewitzUtils.addQuotes(tradeNode.getName()));
    }

    public List<Army> getArmies() {
        return armies;
    }

    public List<Navy> getNavies() {
        return navies;
    }

    public int getArmySize() {
        return this.armies == null ? 0 : this.armies.stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public int getNavySize() {
        return this.navies == null ? 0 : this.navies.stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public List<Regiment> getInfantry() {
        return this.armies == null ? new ArrayList<>() : this.armies.stream()
                                                                    .map(Army::getRegiments)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.INFANTRY.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Regiment> getCavalry() {
        return this.armies == null ? new ArrayList<>() : this.armies.stream()
                                                                    .map(Army::getRegiments)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.CAVALRY.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Regiment> getArtillery() {
        return this.armies == null ? new ArrayList<>() : this.armies.stream()
                                                                    .map(Army::getRegiments)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.ARTILLERY.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Ship> getHeavyShips() {
        return this.navies == null ? new ArrayList<>() : this.navies.stream()
                                                                    .map(Navy::getShips)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.HEAVY_SHIP.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Ship> getLightShips() {
        return this.navies == null ? new ArrayList<>() : this.navies.stream()
                                                                    .map(Navy::getShips)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.LIGHT_SHIP.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Ship> getGalleys() {
        return this.navies == null ? new ArrayList<>() : this.navies.stream()
                                                                    .map(Navy::getShips)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.GALLEY.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<Ship> getTransports() {
        return this.navies == null ? new ArrayList<>() : this.navies.stream()
                                                                    .map(Navy::getShips)
                                                                    .flatMap(Collection::stream)
                                                                    .filter(regiment -> UnitType.TRANSPORT.equals(regiment.getUnitType()))
                                                                    .toList();
    }

    public List<AbstractRegiment> getUnits() {
        if (this.navies == null && this.armies == null) {
            return new ArrayList<>();
        } else if (this.navies == null) {
            return this.armies.stream().map(Army::getRegiments).flatMap(Collection::stream).collect(Collectors.toList());
        } else if (this.armies == null) {
            return this.navies.stream().map(Navy::getShips).flatMap(Collection::stream).collect(Collectors.toList());
        } else {
            return Stream.concat(this.navies.stream().map(Navy::getShips).flatMap(Collection::stream),
                                 this.armies.stream().map(Army::getRegiments).flatMap(Collection::stream)).toList();
        }
    }

    public long getNbRegimentOf(String type) {
        return this.armies == null ? 0 : this.armies.stream()
                                                    .mapToLong(army -> army.getRegiments()
                                                                           .stream()
                                                                           .filter(regiment -> type.equals(regiment.getTypeName()))
                                                                           .count())
                                                    .sum();
    }

    public long getNbRegimentOfCategory(int category) {
        return this.armies == null ? 0 : this.armies.stream()
                                                    .mapToLong(army -> army.getRegiments()
                                                                           .stream()
                                                                           .filter(regiment -> Objects.equals(regiment.getCategory(), category))
                                                                           .count())
                                                    .sum();
    }

    public Boolean activeTradeCompany() {
        return this.item.getVarAsBool("active_trade_company");
    }

    public void setActiveTradeCompany(boolean activeTradeCompany) {
        this.item.setVariable("active_trade_company", activeTradeCompany);
    }

    public boolean centerOfReligion() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("center_of_religion"));
    }

    public void setCenterOfReligion(boolean centerOfReligion) {
        this.item.setVariable("center_of_religion", centerOfReligion);
    }

    public String getOriginalCultureName() {
        return this.item.getVarAsString("original_culture");
    }

    public Culture getOriginalCulture() {
        return this.save.getGame().getCulture(getOriginalCultureName());
    }

    public void setOriginalCulture(Culture originalCulture) {
        this.item.setVariable("original_culture", originalCulture.getName());
    }

    public String getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(getCultureName());
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public String getOriginalReligionName() {
        return this.item.getVarAsString("original_religion");
    }

    public SaveReligion getOriginalReligion() {
        return this.save.getReligions().getReligion(getOriginalReligionName());
    }

    public void setOriginalReligion(SaveReligion originalReligion) {
        this.item.setVariable("original_religion", originalReligion.getName());
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(getReligionName());
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
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
        return getOwner() != null;
    }

    public boolean isCity() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("is_city"));
    }

    public boolean isColony() {
        return !isCity() && getOwner() != null;
    }

    public void colonize(SaveCountry country) {
        if (!isCity() && getColonySize() == null) {
            setOwner(country);
            setController(country);
            setOriginalColoniser(country);
            setOriginalCulture(country.getPrimaryCulture());
            setCulture(country.getPrimaryCulture());
            setReligion(country.getReligion());
            setColonySize(1);
            this.history.addEvent(this.save.getDate(), "owner", country.getTag());
            this.history.addEvent(this.save.getDate(), "culture", country.getPrimaryCulture().getName());
            this.history.addEvent(this.save.getDate(), "religion", country.getReligion().getName());
        }
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

    public Double getDevelopment() {
        return NumbersUtils.doubleOrDefault(getBaseManpower()) + NumbersUtils.doubleOrDefault(getBaseProduction())
               + NumbersUtils.doubleOrDefault(getBaseTax());
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

        if (devastation == 0) {
            this.item.removeVariable("devastation");
        } else {
            this.item.setVariable("devastation", devastation);
        }
    }

    public Double getLocalAutonomy() { //Fixme get country modifier min_autonomy
        if (this.localAutonomy == null) {
            this.localAutonomy = Math.max(NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("min_local_autonomy"))),
                                          NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("local_autonomy")));
        }

        return this.localAutonomy;
    }

    public Double getTrueLocalAutonomy() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("local_autonomy"));
    }

    public void setLocalAutonomy(double localAutonomy) {
        this.item.setVariable("local_autonomy", localAutonomy);
    }

    public Boolean ub() {
        return this.item.getVarAsBool("ub");
    }

    public boolean inHre() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("hre"));
    }

    public void setInHre(boolean inHre) {
        this.item.setVariable("hre", inHre);
    }

    public boolean blockade() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("blockade"));
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
                                           .toList();
            this.buildingsUpdated = true;
        }

        return this.buildings;
    }

    public List<Building> getAvailableBuildings() {
        List<Building> availableBuildings = new ArrayList<>();

        if (this.country != null) {
            this.save.getGame().getBuildings().forEach(building -> {
                if (building.onlyNative() && this.country != null
                    && !"native".equals(this.country.getGovernment().getType().getName())) {
                    return;
                }

                if (building.onlyInPort() && !isPort()) {
                    return;
                }

                if (!building.getManufactoryFor().isEmpty() && !building.getManufactoryFor().contains(null) //Null = all goods
                    && !building.getManufactoryFor().contains(getTradeGood())) {
                    return;
                }

                availableBuildings.add(building);
            });
        }

        return availableBuildings;
    }

    public List<List<Building>> getAvailableBuildingsTree() {
        return Eu4Utils.buildingsTree(getAvailableBuildings());
    }

    public void setBuildings(List<Building> newBuildings) {
        Iterator<ProvinceBuilding> iterator = this.buildings.iterator();

        while (iterator.hasNext()) {
            ProvinceBuilding provinceBuilding = iterator.next();

            if (!newBuildings.contains(provinceBuilding)) {
                removeBuildingNoRefresh(provinceBuilding.getName());
                iterator.remove();
            }

            newBuildings.remove(provinceBuilding);
        }

        newBuildings.forEach(building -> addBuilding(building.getName(), getControllerTag()));
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

            if (!buildingsItem.hasVar(name)) {
                buildingsItem.addVariable(name, true);
                buildingsBuildersItem.addVariable(name, ClausewitzUtils.addQuotes(builder));
                this.history.addEvent(this.save.getDate(), name, true);
            }

            refreshAttributes();
        }
    }

    private void removeBuildingNoRefresh(String name) {
        ClausewitzItem buildingsBuildersItem = this.item.getChild("building_builders");
        ClausewitzItem buildingsItem = this.item.getChild("buildings");

        if (buildingsBuildersItem != null) {
            buildingsBuildersItem.removeVariable(name);
        }

        if (buildingsItem != null) {
            buildingsItem.removeVariable(name);
        }
    }

    public void removeBuilding(String name) {
        removeBuildingNoRefresh(name);
        refreshAttributes();
    }

    public List<GreatProject> getGreatProjects() {
        ClausewitzList list = this.item.getList("great_projects");

        if (list != null) {
            return list.getValues().stream().map(s -> this.save.getGame().getGreatProject(s)).filter(Objects::nonNull).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public SaveProvinceHistory getHistory() {
        return history;
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

    public List<SaveCountry> getDiscoveredBy() {
        ClausewitzList list = this.item.getList("discovered_by");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void setDiscoveredBy(List<SaveCountry> countries) {
        getDiscoveredBy().forEach(core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove,
                                                                                                                     () -> removeDiscoveredBy(core)));

        countries.forEach(this::addDiscoveredBy);
    }

    public void addDiscoveredBy(SaveCountry country) {
        ClausewitzList list = this.item.getList("discovered_by");

        if (!list.contains(country.getTag())) {
            list.add(country.getTag());
            this.history.addEvent(this.save.getDate(), "discovered_by", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public void removeDiscoveredBy(SaveCountry country) {
        ClausewitzList list = this.item.getList("discovered_by");

        if (list != null) {
            list.remove(country.getTag());
        }
    }

    public Map<String, Integer> getImproveCount() {
        return this.improveCount;
    }

    public int getTotalImproveCount() {
        return this.improveCount == null ? 0 : this.improveCount.values().stream().mapToInt(Integer::intValue).sum();
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

    public Integer getNativeSize() {
        Double value = this.item.getVarAsDouble("native_size");

        if (value != null) {
            return (int) (value * 100);
        }

        return null;
    }

    public void setNativeSize(Integer nativeSize) {
        if (nativeSize == null || nativeSize == 0) {
            this.item.removeVariable("native_size");
        } else {
            this.item.setVariable("native_size", (Math.ceil((double) nativeSize / 100)));
        }
    }

    public Integer getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public void setNativeHostileness(Integer nativeHostileness) {
        if (nativeHostileness == null || nativeHostileness == 0) {
            this.item.removeVariable("native_hostileness");
        } else {
            this.item.setVariable("native_hostileness", nativeHostileness);
        }
    }

    public Integer getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(Integer nativeFerocity) {
        if (nativeFerocity == null || nativeFerocity == 0) {
            this.item.removeVariable("native_ferocity");
        } else {
            this.item.setVariable("native_ferocity", nativeFerocity);
        }
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

    public Map<String, SaveModifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(String modifier, LocalDate date) {
        addModifier(modifier, date, null);
    }

    public void addModifier(String modifier, LocalDate date, Boolean hidden) {
        SaveModifier.addToItem(this.item, modifier, date, hidden);
        refreshAttributes();
    }

    public void removeModifier(int index) {
        this.item.removeChild("modifier", index);
        refreshAttributes();
    }

    public void removeModifier(GameModifier modifier) {
        removeModifier(modifier.getName());
    }

    public void removeModifier(String modifier) {
        Integer index = null;
        modifier = ClausewitzUtils.addQuotes(modifier);
        List<SaveModifier> saveModifiers = new ArrayList<>(this.modifiers.values());

        for (int i = 0; i < saveModifiers.size(); i++) {
            if (saveModifiers.get(i).getModifierName().equalsIgnoreCase(modifier)) {
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
        return BooleanUtils.toBoolean(this.item.getVarAsBool("mothball_command"));
    }

    public void setFortMothballed(boolean fortMothballed) {
        this.item.setVariable("mothball_command", fortMothballed);
    }

    public int getFortLevel() {
        if (fortMothballed()) {
            return 0;
        }

        OptionalDouble fortLevel = getBuildings().stream()
                                                 .map(b -> b.getBuilding().getModifiers())
                                                 .filter(m -> m.getModifier("fort_level") != null)
                                                 .mapToDouble(m -> m.getModifier("fort_level"))
                                                 .max();

        if (fortLevel.isEmpty()) {
            return 0;
        } else {
            return (int) (fortLevel.getAsDouble() + ((getOwner() != null && getOwner().getCapital().equals(this)) ? 1 : 0));
        }
    }

    public Double getTradePower() {
        return this.item.getVarAsDouble("trade_power");
    }

    public Id getRebelFaction() {
        return rebelFaction;
    }

    public boolean userChangedName() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("user_changed_name"));
    }

    public boolean hreLiberated() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("hre_liberated"));
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

    public LocalDate getLastLooted() {
        return this.item.getVarAsDate("last_looted");
    }

    public void setLastLooted(LocalDate lastLooted) {
        this.item.setVariable("last_looted", lastLooted);
    }

    public LocalDate getLastRazed() {
        return this.item.getVarAsDate("last_razed");
    }

    public void setLastRazed(LocalDate lastRazed) {
        this.item.setVariable("last_razed", lastRazed);
    }

    public SaveCountry getLastRazedBy() {
        return this.save.getCountry(this.item.getVarAsString("last_razed_by"));
    }

    public void setLastRazedBy(SaveCountry lastRazedBy) {
        this.item.setVariable("last_razed_by", ClausewitzUtils.addQuotes(lastRazedBy.getTag()));
    }

    public boolean isRazed() {
        SaveModifier modifier = getModifiers().get("province_razed");

        if (modifier != null) {
            return modifier.getDate().isAfter(this.save.getDate());
        }

        return false;
    }

    public boolean isSlavesRaided() {
        SaveModifier modifier = getModifiers().get("slaves_raided");

        if (modifier != null) {
            return modifier.getDate().isAfter(this.save.getDate());
        }

        return false;
    }

    public String getLastNativeUprising() {
        return this.item.getVarAsString("last_native_uprising");
    }

    public void setLastNativeUprising(String lastNativeUprising) {
        this.item.setVariable("last_native_uprising", ClausewitzUtils.addQuotes(lastNativeUprising));
    }

    public Integer getCenterOfTradeLevel() {
        return this.item.getVarAsInt("center_of_trade");
    }

    public CenterOfTrade getCenterOfTrade() {
        return this.save.getGame().getCentersOfTrade().stream().filter(centerOfTrade -> centerOfTrade.isValid(this)).findFirst().orElse(null);
    }

    public void setCenterOfTrade(CenterOfTrade centerOfTrade) {
        if (centerOfTrade == null) {
            this.item.removeVariable("center_of_trade");
        } else {
            this.item.setVariable("center_of_trade", centerOfTrade.getLevel());
        }
    }

    public void setCenterOfTrade(Integer centerOfTrade) {
        if (centerOfTrade == null || centerOfTrade == 0) {
            this.item.removeVariable("center_of_trade");
        } else {
            this.item.setVariable("center_of_trade", centerOfTrade);
        }
    }

    public Double getFortFlipProgress() {
        return this.item.getVarAsDouble("fort_flip_progress");
    }

    public Integer getFortFlipperProv() {
        return this.item.getVarAsInt("fort_flipper_prov");
    }

    public ProvinceConstruction getBuildingConstruction() {
        return buildingConstruction;
    }

    public ProvinceConstruction getColonyConstruction() {
        return colonyConstruction;
    }

    public ProvinceConstruction getMissionaryConstruction() {
        return missionaryConstruction;
    }

    public double getTolerance() {
        if (getOwner() == null || !getOwner().isAlive()) {
            return 0;
        } else if (getReligion().equals(getOwner().getReligion())) {
            return getOwner().getToleranceOwn();
        } else if (getReligion().getReligionGroup().equals(getOwner().getReligion().getReligionGroup())) {
            return getOwner().getToleranceHeretic();
        } else {
            return getOwner().getToleranceHeathen();
        }
    }

    public double getLandForceLimit() {
        if (getOwner() != null && getOwner().isAlive() && getOwner().getOwnedProvinces().size() < 5) { //DON'T ASK MY WHY
            if (getTradeGood().getProvinceModifiers().hasModifier(ModifiersUtils.getModifier("land_forcelimit"))) {
                return getTradeGood().getProvinceModifiers().getModifier(ModifiersUtils.getModifier("land_forcelimit"));
            } else {
                return 0;
            }
        } else {
            return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("land_forcelimit")))
                   * (1 + getModifier(ModifiersUtils.getModifier("land_forcelimit_modifier")));
        }
    }

    public double getNavalForceLimit() {
        if (!isPort() || getOwner() != null && getOwner().isAlive() && getOwner().getOwnedProvinces().size() < 5) { //DON'T ASK MY WHY
            if (getTradeGood().getProvinceModifiers().hasModifier(ModifiersUtils.getModifier("naval_forcelimit"))) {
                return getTradeGood().getProvinceModifiers().getModifier(ModifiersUtils.getModifier("naval_forcelimit"));
            } else {
                return 0;
            }
        } else {
            return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("naval_forcelimit")))
                   * (1 + getModifier(ModifiersUtils.getModifier("naval_forcelimit_modifier")));
        }
    }

    public Double getModifier(Modifier modifier) {
        List<Double> list = new ArrayList<>();
        list.add(StaticModifiers.applyToModifiersProvince(this, modifier));

        if (CollectionUtils.isNotEmpty(getBuildings())) {
            list.addAll(getBuildings().stream()
                                      .map(b -> b.getBuilding().getModifiers())
                                      .filter(Objects::nonNull)
                                      .filter(m -> m.hasModifier(modifier))
                                      .map(m -> m.getModifier(modifier))
                                      .toList());
        }

        if (MapUtils.isNotEmpty(getModifiers())) {
            list.addAll(getModifiers().values()
                                      .stream()
                                      .filter(m -> !StaticModifier.class.equals(m.getModifier().getClass()))
                                      .map(m -> m.getModifiers(this, modifier))
                                      .filter(Objects::nonNull)
                                      .toList());
        }

        if (getCulture() != null && getCulture().getProvinceModifiers().hasModifier(modifier)) {
            list.add(getCulture().getProvinceModifiers().getModifier(modifier));
        }

        if (getTradeGood() != null && getTradeGood().getProvinceModifiers().hasModifier(modifier)) {
            list.add(getTradeGood().getProvinceModifiers().getModifier(modifier));
        }

        if (inHre() && !this.save.getHre().dismantled()) {
            list.addAll(this.save.getHre()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getProvinceModifiers)
                                 .filter(Objects::nonNull)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .toList());
        }

        if (getOwner() != null && getOwner().isAlive() && !this.save.getCelestialEmpire().dismantled()
            && getOwner().equals(this.save.getCelestialEmpire().getEmperor())) {
            list.addAll(this.save.getCelestialEmpire()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getProvinceModifiers)
                                 .filter(Objects::nonNull)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .toList());
        }

        if (CollectionUtils.isNotEmpty(getGreatProjects())) {
            list.addAll(getGreatProjects().stream()
                                          .map(GreatProject::getModifiers)
                                          .filter(Objects::nonNull)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());
        }

        if (getSaveArea() != null && getOwner() != null && getOwner().isAlive()) {
            if (getSaveArea().getCountriesStates() != null) {
                CountryState countryState = getSaveArea().getCountryState(getOwner());

                if (countryState != null) {
                    if (countryState.getActiveEdict() != null
                        && countryState.getActiveEdict().getWhich().getModifiers().getProvinceModifiers().hasModifier(modifier)) {
                        list.add(countryState.getActiveEdict().getWhich().getModifiers().getProvinceModifiers().getModifier(modifier));
                    }

                    if (countryState.getHolyOrder() != null
                        && countryState.getHolyOrder().getModifiers().getProvinceModifiers().hasModifier(modifier)) {
                        list.add(countryState.getHolyOrder().getModifiers().getProvinceModifiers().getModifier(modifier));
                    }
                }
            }

            SaveInvestment investment = getSaveArea().getInvestment(getOwner());

            if (investment != null && CollectionUtils.isNotEmpty(investment.getInvestments())) {
                list.addAll(investment.getInvestments()
                                      .stream()
                                      .map(Investment::getAreaModifier)
                                      .filter(Objects::nonNull)
                                      .filter(m -> m.hasModifier(modifier))
                                      .map(m -> m.getModifier(modifier))
                                      .toList());

                if (BooleanUtils.toBoolean(activeTradeCompany())) {
                    list.addAll(investment.getInvestments()
                                          .stream()
                                          .map(Investment::getCompanyProvinceAreaModifier)
                                          .filter(Objects::nonNull)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());

                    getOwner().getTradeCompanies()
                              .stream()
                              .filter(company -> company.getProvinces().contains(this))
                              .findFirst()
                              .ifPresent(company -> list.addAll(company.getProvinces()
                                                                       .stream()
                                                                       .map(SaveProvince::getSaveArea)
                                                                       .distinct()
                                                                       .filter(Objects::nonNull)
                                                                       .map(saveArea -> saveArea.getInvestment(getOwner()))
                                                                       .filter(Objects::nonNull)
                                                                       .map(SaveInvestment::getInvestments)
                                                                       .flatMap(Collection::stream)
                                                                       .map(Investment::getCompanyRegionModifier)
                                                                       .filter(Objects::nonNull)
                                                                       .filter(m -> m.hasModifier(modifier))
                                                                       .map(m -> m.getModifier(modifier))
                                                                       .toList()));
                }
            }

            getSaveArea().getProvinces().stream().filter(saveProvince -> saveProvince.getCenterOfTrade() != null).findFirst().ifPresent(saveProvince -> {
                if (saveProvince.getCenterOfTrade().getStateModifiers().hasModifier(modifier)) {
                    list.add(saveProvince.getCenterOfTrade().getStateModifiers().getModifier(modifier));
                }
            });
        }

        if (getOwner() != null && getOwner().isAlive()) {
            TradeNodeCountry tradeNodeCountry = this.save.getTradeNode(ClausewitzUtils.removeQuotes(getTrade())).getCountry(getOwner());

            if (tradeNodeCountry != null) {
                TradePolicy tradePolicy = tradeNodeCountry.getTradePolicy();

                if (tradePolicy != null) {
                    if (tradePolicy.getNodeProvinceModifier().getProvinceModifiers().hasModifier(modifier)) {
                        list.add(tradePolicy.getNodeProvinceModifier().getProvinceModifiers().getModifier(modifier));
                    }

                    if (tradePolicy.getTradePower().getProvinceModifiers().hasModifier(modifier)) {
                        list.add(tradePolicy.getTradePower().getProvinceModifiers().getModifier(modifier));
                    }
                }
            }
        }

        if (getCenterOfTrade() != null && getCenterOfTrade().getProvinceModifiers().hasModifier(modifier)) {
            list.add(getCenterOfTrade().getProvinceModifiers().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, list);
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

        ClausewitzItem seatInParliamentItem = this.item.getChild("seat_in_parliament");

        if (seatInParliamentItem != null) {
            this.seatInParliament = new SeatInParliament(seatInParliamentItem);
        }

        ClausewitzItem historyItem = this.item.getChild("history");

        if (historyItem != null) {
            this.history = new SaveProvinceHistory(historyItem, this);
        }

        ClausewitzItem buildersItem = this.item.getChild("building_builders");
        this.buildings = new ArrayList<>(0);

        if (buildersItem != null) {
            buildersItem.getVariables().forEach(var -> {
                if (historyItem == null) {
                    this.buildings.add(new ProvinceBuilding(var.getName(), var.getValue(), null, this.save.getGame().getBuilding(var.getName())));
                } else {
                    historyItem.getChildren()
                               .stream()
                               .filter(child -> child.hasVar(var.getName()))
                               .findFirst()
                               .ifPresentOrElse(child -> this.buildings.add(new ProvinceBuilding(var.getName(),
                                                                                                 var.getValue(),
                                                                                                 Eu4Utils.stringToDate(child.getName()),
                                                                                                 this.save.getGame().getBuilding(var.getName()))),
                                                () -> this.buildings.add(new ProvinceBuilding(var.getName(),
                                                                                              var.getValue(),
                                                                                              null,
                                                                                              this.save.getGame().getBuilding(var.getName()))));
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

        ClausewitzItem improveCountItem = this.item.getChild("country_improve_count");

        if (improveCountItem != null) {
            this.improveCount = new HashMap<>();
            for (int i = 0; i < improveCountItem.getVariables().size() - 1; i += 2) {
                this.improveCount.put(ClausewitzUtils.removeQuotes(improveCountItem.getVariables().get(i).getValue()),
                                      improveCountItem.getVariables().get(i + 1).getAsInt());
            }
        }

        List<ClausewitzItem> modifierItems = this.item.getChildren("modifier");
        this.modifiers = modifierItems.stream()
                                      .map(child -> new SaveModifier(child, this.save.getGame()))
                                      .collect(Collectors.toMap(modifier -> ClausewitzUtils.removeQuotes(modifier.getModifierName()), Function.identity()));

        ClausewitzItem rebelFactionItem = this.item.getChild("rebel_faction");

        if (rebelFactionItem != null) {
            this.rebelFaction = new Id(rebelFactionItem);
        }

        ClausewitzItem child = this.item.getChild("building_construction");

        if (child != null) {
            this.buildingConstruction = new ProvinceConstruction(child, this);
        }

        child = this.item.getChild("colony_construction");

        if (child != null) {
            this.colonyConstruction = new ProvinceConstruction(child, this);
        }

        child = this.item.getChild("missionary_construction");

        if (child != null) {
            this.missionaryConstruction = new ProvinceConstruction(child, this);
        }

        List<ClausewitzItem> unitsItems = this.item.getChildren("unit");

        if (!unitsItems.isEmpty()) {
            this.armies = new ArrayList<>(0);
            this.navies = new ArrayList<>(0);
            unitsItems.stream().map(Id::new).forEach(unitId -> {
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

        this.localAutonomy = null;
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

    @Override
    public String toString() {
        return getName();
    }
}
