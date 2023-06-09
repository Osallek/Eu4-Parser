package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.UnitType;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.CenterOfTrade;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.GameModifier;
import fr.osallek.eu4parser.model.game.GreatProjectTier;
import fr.osallek.eu4parser.model.game.HolyOrder;
import fr.osallek.eu4parser.model.game.ImperialReform;
import fr.osallek.eu4parser.model.game.Institution;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.Names;
import fr.osallek.eu4parser.model.game.ParliamentBribe;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.game.StateEdict;
import fr.osallek.eu4parser.model.game.StaticModifier;
import fr.osallek.eu4parser.model.game.StaticModifiers;
import fr.osallek.eu4parser.model.game.TradeCompany;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.game.TradeNode;
import fr.osallek.eu4parser.model.game.TradePolicy;
import fr.osallek.eu4parser.model.game.localisation.Eu4Language;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveGreatProject;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.AbstractRegiment;
import fr.osallek.eu4parser.model.save.country.Army;
import fr.osallek.eu4parser.model.save.country.CountryState;
import fr.osallek.eu4parser.model.save.country.Edict;
import fr.osallek.eu4parser.model.save.country.Navy;
import fr.osallek.eu4parser.model.save.country.Regiment;
import fr.osallek.eu4parser.model.save.country.SaveArea;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveInvestment;
import fr.osallek.eu4parser.model.save.country.SaveModifier;
import fr.osallek.eu4parser.model.save.country.SaveTradeCompany;
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
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Predicate;
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
        this.country = getOwnerTag().map(this.save::getCountry).orElse(null);
        refreshAttributes();
    }

    public Save getSave() {
        return save;
    }

    public Optional<SaveCountry> getOwner() {
        return Optional.ofNullable(this.country);
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
        return this.item.getVarAsString("name").orElse("");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public ListOfDates getFlags() {
        return flags;
    }

    public Map<String, Double> getVariables() {
        return this.item.getChild("variables")
                        .map(i -> i.getVariables().stream().collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsDouble)))
                        .orElse(null);
    }

    public Optional<String> getTerritorialCore() {
        return this.item.getVarAsString("territorial_core");
    }

    public Optional<String> getOwnerTag() {
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

    public Optional<String> getControllerTag() {
        return this.item.getVarAsString("controller");
    }

    public Optional<SaveCountry> getController() {
        return getControllerTag().map(this.save::getCountry);
    }

    public void setController(SaveCountry controller) {
        getController().ifPresent(country -> {
            country.removeControlledProvince(this);
            setPreviousController(country);
        });

        this.item.setVariable("controller", ClausewitzUtils.addQuotes(controller.getTag()));
        controller.addControlledProvince(this);
        this.history.addEvent(this.save.getDate(), "controller", "tag", ClausewitzUtils.addQuotes(controller.getTag()));
    }

    public Optional<String> getPreviousControllerTag() {
        return this.item.getVarAsString("previous_controller");
    }

    public Optional<SaveCountry> getPreviousController() {
        return getPreviousControllerTag().map(this.save::getCountry);
    }

    public void setPreviousController(SaveCountry previousController) {
        this.item.setVariable("previous_controller", ClausewitzUtils.addQuotes(previousController.getTag()));
    }

    public Optional<String> getOriginalColoniserTag() {
        return this.item.getVarAsString("original_coloniser");
    }

    public Optional<SaveCountry> getOriginalColoniser() {
        return getOriginalColoniserTag().map(this.save::getCountry);
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

    public void addSeatInParliament(ParliamentBribe bribe) {
        SeatInParliament.addToItem(this.item, bribe.getName());
        refreshAttributes();
    }

    public void removeSeatInParliament() {
        this.item.removeChild("seat_in_parliament");
        refreshAttributes();
    }

    public List<Double> getInstitutionsProgress() {
        return this.item.getList("institutions").map(ClausewitzList::getValuesAsDouble).orElse(new ArrayList<>());
    }

    public Optional<Double> getInstitutionsProgress(int institution) {
        return this.item.getList("institutions").flatMap(list -> list.getAsDouble(institution));
    }

    public void setInstitutionProgress(int institution, double progress) {
        this.item.getList("institutions").ifPresent(list -> list.set(institution, progress));
    }

    public void setInstitutionProgress(Institution institution, double progress) {
        setInstitutionProgress(institution.getIndex(), progress);
    }

    public Optional<LocalDate> getExploitDate() {
        return this.item.getVarAsDate("exploit_date");
    }

    public void setExploitDate(LocalDate exploitDate) {
        this.item.setVariable("exploit_date", exploitDate);
    }

    public List<String> getCoresTags() {
        return this.item.getList("cores").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<SaveCountry> getCores() {
        return getCoresTags().stream().map(this.save::getCountry).toList();
    }

    public void setCores(List<SaveCountry> countries) {
        getCores().forEach(core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove, () -> removeCore(core)));

        countries.forEach(this::addCore);
    }

    public void addCore(SaveCountry country) {
        Optional<ClausewitzList> clausewitzList = this.item.getList("cores");

        if (clausewitzList.isPresent()) {
            if (clausewitzList.get().contains(country.getTag())) {
                return;
            }

            clausewitzList.get().add(country.getTag());
        } else {
            this.item.addList("cores", country.getTag());
        }

        country.addCoreProvince(this);
        this.history.addEvent(this.save.getDate(), "add_core", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public void removeCore(SaveCountry country) {
        this.item.getList("cores").ifPresent(list -> {
            list.remove(country.getTag());
            country.removeCoreProvince(this);
            this.history.addEvent(this.save.getDate(), "remove_core", ClausewitzUtils.addQuotes(country.getTag()));
        });
    }

    public List<String> getClaimsTags() {
        return this.item.getList("claims").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<SaveCountry> getClaims() {
        return getClaimsTags().stream().map(this.save::getCountry).toList();
    }

    public void setClaims(List<SaveCountry> countries) {
        getClaims().forEach(core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove, () -> removeClaim(core)));

        countries.forEach(this::addClaim);
    }

    public void addClaim(SaveCountry country) {
        Optional<ClausewitzList> clausewitzList = this.item.getList("claims");

        if (clausewitzList.isPresent()) {
            if (clausewitzList.get().contains(country.getTag())) {
                return;
            }

            clausewitzList.get().add(country.getTag());
        } else {
            this.item.addList("claims", country.getTag());
        }

        country.addClaimProvince(this);
        this.history.addEvent(this.save.getDate(), "add_claim", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public void removeClaim(SaveCountry country) {
        this.item.getList("claims").ifPresent(list -> {
            list.remove(country.getTag());
            country.removeClaimProvince(this);
            this.history.addEvent(this.save.getDate(), "remove_claim", ClausewitzUtils.addQuotes(country.getTag()));
        });
    }

    public Optional<String> getTrade() {
        return this.item.getVarAsString("trade");
    }

    public Optional<TradeNode> getTradeNode() {
        return getTrade().map(s -> this.save.getGame().getTradeNode(s));
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

    public Optional<Boolean> activeTradeCompany() {
        return this.item.getVarAsBool("active_trade_company");
    }

    public void setActiveTradeCompany(boolean activeTradeCompany, Eu4Language language) {
        Optional<SaveCountry> owner = getOwner();

        if (owner.isEmpty()) {
            return;
        }

        if (BooleanUtils.toBoolean(activeTradeCompany().orElse(false)) == activeTradeCompany) {
            return;
        }

        if (BooleanUtils.toBoolean(activeTradeCompany().orElse(false))) {
            getOwner().map(SaveCountry::getTradeCompanies).ifPresent(companies -> companies.forEach(c -> c.removeProvince(this)));
        } else {
            Optional<TradeCompany> company = this.save.getGame().getTradeCompanies().stream().filter(c -> c.getProvinces().contains(getId())).findFirst();

            if (company.isEmpty()) {
                return;
            }

            Optional<SaveTradeCompany> tradeCompany = getSaveTradeCompany();

            if (tradeCompany.isPresent()) {
                tradeCompany.get().addProvince(this);
            } else {
                String key = company.get()
                                    .getNames()
                                    .stream()
                                    .filter(names -> names.getTrigger().isEmpty() || names.getTrigger().get().apply(owner.get(), owner.get()))
                                    .findFirst()
                                    .flatMap(Names::getName)
                                    .map(ClausewitzUtils::removeQuotes)
                                    .orElse(company.get().getName());

                this.save.addTradeCompany(this.save.getGame().getComputedLocalisation(getSave(), getOwner(), key, language), this);
            }
        }

        this.item.setVariable("active_trade_company", activeTradeCompany);
    }

    public Optional<SaveTradeCompany> getSaveTradeCompany() {
        Optional<SaveCountry> owner = getOwner();

        if (owner.isEmpty()) {
            return Optional.empty();
        }

        if (!BooleanUtils.toBoolean(activeTradeCompany().orElse(false))) {
            return Optional.empty();
        }

        Optional<TradeCompany> company = this.save.getGame().getTradeCompanies().stream().filter(c -> c.getProvinces().contains(getId())).findFirst();

        return company.flatMap(tradeCompany -> owner.get()
                                                    .getTradeCompanies()
                                                    .stream()
                                                    .filter(c -> CollectionUtils.containsAny(tradeCompany.getProvinces(), c.getProvinces()))
                                                    .findFirst());

    }

    public boolean centerOfReligion() {
        return this.item.getVarAsBool("center_of_religion").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setCenterOfReligion(boolean centerOfReligion) {
        this.item.setVariable("center_of_religion", centerOfReligion);
    }

    public Optional<String> getOriginalCultureName() {
        return this.item.getVarAsString("original_culture");
    }

    public Optional<Culture> getOriginalCulture() {
        return getOriginalCultureName().map(s -> this.save.getGame().getCulture(s));
    }

    public void setOriginalCulture(Culture originalCulture) {
        this.item.setVariable("original_culture", originalCulture.getName());
    }

    public Optional<String> getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public Optional<Culture> getCulture() {
        return getCultureName().map(s -> this.save.getGame().getCulture(s));
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public Optional<String> getOriginalReligionName() {
        return this.item.getVarAsString("original_religion");
    }

    public Optional<SaveReligion> getOriginalReligion() {
        return getOriginalReligionName().map(s -> this.save.getReligions().getReligion(s));
    }

    public void setOriginalReligion(SaveReligion originalReligion) {
        this.item.setVariable("original_religion", originalReligion.getName());
    }

    public Optional<String> getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public Optional<SaveReligion> getReligion() {
        return getReligionName().map(s -> this.save.getReligions().getReligion(s));
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public Optional<String> getCapital() {
        return this.item.getVarAsString("capital");
    }

    public void setCapital(String capital) {
        getCapital().ifPresent(s -> this.item.setVariable("capital", ClausewitzUtils.addQuotes(capital)));
    }

    public boolean isOccupied() {
        return getOwner().isPresent();
    }

    public boolean isCity() {
        return this.item.getVarAsBool("is_city").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean isColony() {
        return !isCity() && isOccupied();
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

    public Optional<Double> getColonySize() {
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

    public Optional<Integer> getNativeSizeBeforeMigration() {
        return this.item.getVarAsDouble("native_size_before_migration").map(Double::intValue);
    }

    public void setNativeSizeBeforeMigration(int nativeSizeBeforeMigration) {
        this.item.setVariable("native_size_before_migration", (double) nativeSizeBeforeMigration);
    }

    public Optional<Double> getGarrison() {
        return this.item.getVarAsDouble("garrison");
    }

    public void setGarrison(double garrison) {
        this.item.setVariable("garrison", garrison);
    }

    public Optional<Integer> getSiege() {
        return this.item.getVarAsDouble("siege").map(Double::intValue);
    }

    public void setSiege(int siege) {
        this.item.setVariable("siege", (double) siege);
    }

    public Optional<Double> getBaseTax() {
        return this.item.getVarAsDouble("base_tax");
    }

    public void setBaseTax(double baseTax) {
        this.item.setVariable("base_tax", baseTax);
    }

    public Optional<Double> getOriginalTax() {
        return this.item.getVarAsDouble("original_tax");
    }

    public void setOriginalTax(double originalTax) {
        this.item.setVariable("original_tax", originalTax);
    }

    public Optional<Double> getBaseProduction() {
        return this.item.getVarAsDouble("base_production");
    }

    public void setBaseProduction(double baseProduction) {
        this.item.setVariable("base_production", baseProduction);
    }

    public Optional<Double> getBaseManpower() {
        return this.item.getVarAsDouble("base_manpower");
    }

    public void setBaseManpower(double baseManpower) {
        this.item.setVariable("base_manpower", baseManpower);
    }

    public Optional<Double> getDevelopment() {
        return getBaseManpower().flatMap(manpower -> getBaseProduction().map(prod -> prod + manpower))
                                .flatMap(aDouble -> getBaseTax().map(tax -> tax + aDouble));
    }

    public Optional<Double> getUnrest() {
        return this.item.getVarAsDouble("unrest");
    }

    public Optional<String> getRebels() {
        return this.item.getVarAsString("likely_rebels");
    }

    public Optional<Double> getMissionaryProgress() {
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

    public Optional<String> getTradeGoods() {
        return this.item.getVarAsString("trade_goods");
    }

    public Optional<TradeGood> getTradeGood() {
        return getTradeGoods().map(s -> this.save.getGame().getTradeGood(s));
    }

    public void setTradeGoods(String tradeGoods) {
        this.item.setVariable("trade_goods", tradeGoods);
    }

    public Optional<String> getLatentTradeGoods() {
        return this.item.getList("latent_trade_goods").flatMap(list -> list.get(0));
    }

    public Optional<TradeGood> getLatentTradeGood() {
        return getLatentTradeGoods().map(s -> this.save.getGame().getTradeGood(s));
    }

    public void setLatentTradeGoods(String latentTradeGoods) {
        this.item.getList("latent_trade_goods")
                 .ifPresentOrElse(list -> list.set(0, latentTradeGoods), () -> this.item.addList("latent_trade_goods", latentTradeGoods));
    }

    public Optional<Double> getDevastation() {
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

    public double getLocalAutonomy() { //Fixme get country modifier min_autonomy
        if (this.localAutonomy == null) {
            this.localAutonomy = Math.max(NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("min_local_autonomy"))),
                                          this.item.getVarAsDouble("local_autonomy").orElse(0d));
        }

        return this.localAutonomy;
    }

    public Optional<Double> getTrueLocalAutonomy() {
        return this.item.getVarAsDouble("local_autonomy");
    }

    public void setLocalAutonomy(double localAutonomy) {
        this.item.setVariable("local_autonomy", localAutonomy);
    }

    public Optional<Boolean> ub() {
        return this.item.getVarAsBool("ub");
    }

    public boolean inHre() {
        return this.item.getVarAsBool("hre").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setInHre(boolean inHre) {
        this.item.setVariable("hre", inHre);
    }

    public boolean blockade() {
        return this.item.getVarAsBool("blockade").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<Double> getBlockadeEfficiency() {
        return this.item.getVarAsDouble("blockade_efficiency");
    }

    public List<ProvinceBuilding> getBuildings() {
        if (!this.buildingsUpdated) {
            this.buildings = this.buildings.stream()
                                           .map(building -> new ProvinceBuilding(building, this.save.getGame().getBuilding(building.getName())))
                                           .collect(Collectors.toList()); //Mutable list
            this.buildingsUpdated = true;
        }

        return this.buildings;
    }

    public List<Building> getAvailableBuildings() {
        List<Building> availableBuildings = new ArrayList<>();

        if (this.country != null) {
            this.save.getGame().getBuildings().forEach(building -> {
                if (building.onlyNative() && this.country != null && !"native".equals(this.country.getGovernment().getType().getName())) {
                    return;
                }

                if (building.onlyInPort() && !isPort()) {
                    return;
                }

                if (CollectionUtils.isNotEmpty(building.getManufactoryFor()) && !building.getManufactoryFor().contains("all") && !building.getManufactoryFor()
                                                                                                                                          .contains(
                                                                                                                                                  getTradeGoods())) {
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

    public void setBuildings(Collection<Building> newBuildings) {
        setBuildings(newBuildings.stream().map(Building::getName).toList());
    }

    public void setBuildings(List<String> newBuildings) {
        Iterator<ProvinceBuilding> iterator = this.buildings.iterator();

        while (iterator.hasNext()) {
            ProvinceBuilding provinceBuilding = iterator.next();

            if (!newBuildings.contains(provinceBuilding.getName())) {
                removeBuildingNoRefresh(provinceBuilding.getName());
                iterator.remove();
            }

            newBuildings.remove(provinceBuilding.getName());
        }

        getControllerTag().ifPresent(s -> newBuildings.forEach(building -> addBuilding(building, s)));
    }

    public void addBuilding(String name, String builder) {
        if (isColonizable()) {
            ClausewitzItem buildingsBuildersItem = this.item.getChild("building_builders").orElse(this.item.addChild("building_builders"));
            ClausewitzItem buildingsItem = this.item.getChild("buildings").orElse(this.item.addChild("buildings"));

            if (!buildingsItem.hasVar(name)) {
                buildingsItem.addVariable(name, true);
                buildingsBuildersItem.addVariable(name, ClausewitzUtils.addQuotes(builder));
                this.history.addEvent(this.save.getDate(), name, true);
            }

            refreshAttributes();
        }
    }

    private void removeBuildingNoRefresh(String name) {
        this.item.getChild("building_builders").ifPresent(item -> item.removeVariable(name));
        this.item.getChild("buildings").ifPresent(item -> item.removeVariable(name));
    }

    public void removeBuilding(String name) {
        removeBuildingNoRefresh(name);
        refreshAttributes();
    }

    public List<SaveGreatProject> getGreatProjects() {
        return this.item.getList("great_projects")
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this.save::getGreatProject)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public SaveProvinceHistory getHistory() {
        return history;
    }

    public Optional<Integer> getPatrol() {
        return this.item.getVarAsInt("patrol");
    }

    public ListOfDates getDiscoveryDates() {
        return discoveryDates;
    }

    public ListOfDates getDiscoveryReligionDates() {
        return discoveryReligionDates;
    }

    public List<SaveCountry> getDiscoveredBy() {
        return this.item.getList("discovered_by").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    public void setDiscoveredBy(List<SaveCountry> countries) {
        getDiscoveredBy().forEach(
                core -> countries.stream().filter(c -> c.equals(core)).findFirst().ifPresentOrElse(countries::remove, () -> removeDiscoveredBy(core)));

        countries.forEach(this::addDiscoveredBy);
    }

    public void addDiscoveredBy(SaveCountry country) {
        this.item.getList("discovered_by").ifPresent(list -> {
            if (!list.contains(country.getTag())) {
                list.add(country.getTag());
                this.history.addEvent(this.save.getDate(), "discovered_by", ClausewitzUtils.addQuotes(country.getTag()));
            }
        });
    }

    public void removeDiscoveredBy(SaveCountry country) {
        this.item.getList("discovered_by").ifPresent(list -> list.remove(country.getTag()));
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

    public Optional<Integer> getFormerNativeSize() {
        return this.item.getVarAsDouble("former_native_size").map(value -> (int) (value * 1000));
    }

    public void setFormerNativeSize(int formerNativeSize) {
        this.item.setVariable("former_native_size", ((double) formerNativeSize / 1000));
    }

    public Optional<Integer> getNativeSize() {
        return this.item.getVarAsDouble("native_size").map(value -> (int) (value * 1000));
    }

    public void setNativeSize(Integer nativeSize) {
        if (nativeSize == null || nativeSize == 0) {
            this.item.removeVariable("native_size");
        } else {
            this.item.setVariable("native_size", (Math.ceil((double) nativeSize / 100)));
        }
    }

    public Optional<Integer> getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public void setNativeHostileness(Integer nativeHostileness) {
        if (nativeHostileness == null || nativeHostileness == 0) {
            this.item.removeVariable("native_hostileness");
        } else {
            this.item.setVariable("native_hostileness", nativeHostileness);
        }
    }

    public Optional<Integer> getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(Integer nativeFerocity) {
        if (nativeFerocity == null || nativeFerocity == 0) {
            this.item.removeVariable("native_ferocity");
        } else {
            this.item.setVariable("native_ferocity", nativeFerocity);
        }
    }

    public Optional<Integer> getNationalism() {
        return this.item.getVarAsInt("nationalism");
    }

    public void setNationalism(int nationalism) {
        this.item.setVariable("nationalism", nationalism);
    }

    public Optional<Integer> getWinterLevel() {
        return this.item.getVarAsInt("winter");
    }

    public Optional<Integer> getPreviousWinter() {
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

    public Optional<Integer> getFortInfluencing() {
        return this.item.getVarAsInt("fort_influencing");
    }

    public boolean fortMothballed() {
        return this.item.getVarAsBool("mothball_command").map(BooleanUtils::toBoolean).orElse(false);
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
                                                 .filter(Optional::isPresent)
                                                 .map(Optional::get)
                                                 .filter(m -> m.getModifier("fort_level") != null)
                                                 .mapToDouble(m -> m.getModifier("fort_level"))
                                                 .max();

        if (fortLevel.isEmpty()) {
            return 0;
        } else {
            return (int) (fortLevel.getAsDouble() + ((getOwner().isPresent() && getOwner().get().getCapital().equals(this)) ? 1 : 0));
        }
    }

    public Optional<Double> getTradePower() {
        return this.item.getVarAsDouble("trade_power");
    }

    public Id getRebelFaction() {
        return rebelFaction;
    }

    public boolean userChangedName() {
        return this.item.getVarAsBool("user_changed_name").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean hreLiberated() {
        return this.item.getVarAsBool("hre_liberated").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setHreLiberated(boolean hreLiberated) {
        this.item.setVariable("hre_liberated", hreLiberated);
    }

    public Optional<Double> getLootRemaining() {
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

    public Optional<LocalDate> getLastLooted() {
        return this.item.getVarAsDate("last_looted");
    }

    public void setLastLooted(LocalDate lastLooted) {
        this.item.setVariable("last_looted", lastLooted);
    }

    public Optional<LocalDate> getLastRazed() {
        return this.item.getVarAsDate("last_razed");
    }

    public void setLastRazed(LocalDate lastRazed) {
        this.item.setVariable("last_razed", lastRazed);
    }

    public Optional<SaveCountry> getLastRazedBy() {
        return this.item.getVarAsString("last_razed_by").map(this.save::getCountry);
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

    public Optional<String> getLastNativeUprising() {
        return this.item.getVarAsString("last_native_uprising");
    }

    public void setLastNativeUprising(String lastNativeUprising) {
        this.item.setVariable("last_native_uprising", ClausewitzUtils.addQuotes(lastNativeUprising));
    }

    public Optional<Integer> getCenterOfTradeLevel() {
        return this.item.getVarAsInt("center_of_trade");
    }

    public Optional<CenterOfTrade> getCenterOfTrade() {
        return this.save.getGame().getCentersOfTrade().stream().filter(centerOfTrade -> centerOfTrade.isValid(this)).findFirst();
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

    public Optional<Integer> getExpandInfrastructure() {
        return this.item.getVarAsInt("expand_infrastructure");
    }

    public void setExpandInfrastructure(Integer expandInfrastructure) {
        if (expandInfrastructure == null || expandInfrastructure == 0) {
            this.item.removeVariable("expand_infrastructure");
        } else {
            this.item.setVariable("expand_infrastructure", expandInfrastructure);
        }
    }

    public Optional<Double> getFortFlipProgress() {
        return this.item.getVarAsDouble("fort_flip_progress");
    }

    public Optional<Integer> getFortFlipperProv() {
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
        Optional<SaveCountry> owner = getOwner();
        Optional<SaveReligion> religion = getReligion();

        if (owner.filter(Predicate.not(SaveCountry::isAlive)).isEmpty() || religion.isEmpty()) {
            return 0;
        } else if (religion.get().equals(owner.get().getReligion())) {
            return owner.get().getToleranceOwn();
        } else if (religion.get().getReligionGroup().equals(owner.get().getReligion().getReligionGroup())) {
            return owner.get().getToleranceHeretic();
        } else {
            return owner.get().getToleranceHeathen();
        }
    }

    public double getLandForceLimit() {
        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("land_forcelimit"))) * (100 - getLocalAutonomy()) / 100) *
               (1 + getModifier(ModifiersUtils.getModifier("land_forcelimit_modifier")));
    }

    public double getNavalForceLimit() {
        Optional<SaveCountry> owner = getOwner();
        if (!isPort() || owner.isPresent() && owner.get().isAlive() && owner.get().getOwnedProvinces().size() < 5) { //DON'T ASK MY WHY
            return getTradeGood().flatMap(TradeGood::getProvinceModifiers).map(m -> m.getModifier(ModifiersUtils.getModifier("naval_forcelimit"))).orElse(0d);
        } else {
            return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("naval_forcelimit"))) * (100 - getLocalAutonomy()) / 100) *
                   (1 + getModifier(ModifiersUtils.getModifier("naval_forcelimit_modifier")));
        }
    }

    public Double getModifier(Modifier modifier) {
        List<Double> list = new ArrayList<>();
        list.add(StaticModifiers.applyToModifiersProvince(this, modifier));

        if (CollectionUtils.isNotEmpty(getBuildings())) {
            list.addAll(getBuildings().stream()
                                      .map(b -> b.getBuilding().getModifiers())
                                      .filter(Optional::isPresent)
                                      .map(Optional::get)
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

        getCulture().flatMap(culture -> culture.getProvinceModifiers().filter(m -> m.hasModifier(modifier))).ifPresent(m -> list.add(m.getModifier(modifier)));
        getTradeGood().flatMap(TradeGood::getProvinceModifiers).map(m -> m.getModifier(modifier)).ifPresent(list::add);

        if (inHre() && !this.save.getHre().dismantled()) {
            list.addAll(this.save.getHre()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getProvinceModifiers)
                                 .filter(Optional::isPresent)
                                 .map(Optional::get)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .filter(Objects::nonNull)
                                 .toList());
        }

        Optional<SaveCountry> owner = getOwner();

        if (owner.filter(SaveCountry::isAlive).isPresent() && !this.save.getCelestialEmpire().dismantled() &&
            owner.filter(c -> this.save.getCelestialEmpire().getEmperor().isPresent())
                 .filter(c -> c.equals(this.save.getCelestialEmpire().getEmperor().get()))
                 .isPresent()) {
            list.addAll(this.save.getCelestialEmpire()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getProvinceModifiers)
                                 .filter(Optional::isPresent)
                                 .map(Optional::get)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .toList());
        }

        if (CollectionUtils.isNotEmpty(getGreatProjects())) {
            list.addAll(getGreatProjects().stream()
                                          .map(SaveGreatProject::getTier)
                                          .filter(Objects::nonNull)
                                          .map(GreatProjectTier::getProvinceModifiers)
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());
        }

        if (getSaveArea() != null && owner.filter(SaveCountry::isAlive).isPresent()) {
            if (getSaveArea().getCountriesStates() != null) {
                CountryState countryState = getSaveArea().getCountryState(owner.get());

                if (countryState != null) {
                    countryState.getActiveEdict()
                                .flatMap(Edict::getWhich)
                                .flatMap(StateEdict::getModifiers)
                                .map(Modifiers::getProvinceModifiers)
                                .map(m -> m.getModifier(modifier))
                                .ifPresent(list::add);
                    countryState.getHolyOrder()
                                .flatMap(HolyOrder::getModifiers)
                                .map(Modifiers::getProvinceModifiers)
                                .map(m -> m.getModifier(modifier))
                                .ifPresent(list::add);
                }
            }

            owner.map(c -> getSaveArea().getInvestment(c)).ifPresent(investment -> {
                if (CollectionUtils.isNotEmpty(investment.getInvestments())) {
                    list.addAll(investment.getInvestments()
                                          .stream()
                                          .map(Investment::getAreaModifier)
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());

                    if (activeTradeCompany().map(BooleanUtils::toBoolean).orElse(false)) {
                        list.addAll(investment.getInvestments()
                                              .stream()
                                              .map(Investment::getCompanyProvinceAreaModifier)
                                              .filter(Optional::isPresent)
                                              .map(Optional::get)
                                              .filter(m -> m.hasModifier(modifier))
                                              .map(m -> m.getModifier(modifier))
                                              .toList());

                        owner.get()
                             .getTradeCompanies()
                             .stream()
                             .filter(company -> company.getProvinces().contains(this))
                             .findFirst()
                             .ifPresent(company -> list.addAll(company.getProvinces()
                                                                      .stream()
                                                                      .map(SaveProvince::getSaveArea)
                                                                      .distinct()
                                                                      .filter(Objects::nonNull)
                                                                      .map(saveArea -> saveArea.getInvestment(owner.get()))
                                                                      .filter(Objects::nonNull)
                                                                      .map(SaveInvestment::getInvestments)
                                                                      .flatMap(Collection::stream)
                                                                      .map(Investment::getCompanyRegionModifier)
                                                                      .filter(Optional::isPresent)
                                                                      .map(Optional::get)
                                                                      .filter(m -> m.hasModifier(modifier))
                                                                      .map(m -> m.getModifier(modifier))
                                                                      .toList()));
                    }
                }
            });

            getSaveArea().getProvinces()
                         .stream()
                         .map(SaveProvince::getCenterOfTrade)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .findFirst()
                         .flatMap(CenterOfTrade::getStateModifiers)
                         .ifPresent(modifiers -> {
                             if (modifiers.hasModifier(modifier)) {
                                 list.add(modifiers.getModifier(modifier));
                             }
                         });
        }

        if (owner.filter(SaveCountry::isAlive).isPresent() && getTrade().isPresent()) {
            TradeNodeCountry tradeNodeCountry = this.save.getTradeNode(ClausewitzUtils.removeQuotes(getTrade().get())).getCountry(owner.get());

            if (tradeNodeCountry != null) {
                tradeNodeCountry.getTradePolicy().ifPresent(tradePolicy -> {
                    tradePolicy.getNodeProvinceModifier().filter(m -> m.getProvinceModifiers().hasModifier(modifier)).ifPresent(m -> {
                        list.add(m.getModifier(modifier));
                    });
                    tradePolicy.getTradePower().filter(m -> m.getProvinceModifiers().hasModifier(modifier)).ifPresent(m -> {
                        list.add(m.getModifier(modifier));
                    });
                });
            }
        }

        getCenterOfTrade().flatMap(CenterOfTrade::getProvinceModifiers).map(m -> m.getModifier(modifier)).ifPresent(list::add);

        return ModifiersUtils.sumModifiers(modifier, list);
    }

    private void refreshAttributes() {
        this.flags = this.item.getChild("flags").map(ListOfDates::new).orElse(null);
        this.occupyingRebelFaction = this.item.getChild("occupying_rebel_faction").map(Id::new).orElse(null);
        this.seatInParliament = this.item.getChild("seat_in_parliament").map(i -> new SeatInParliament(i, this.save)).orElse(null);
        Optional<ClausewitzItem> historyItem = this.item.getChild("history");
        this.history = historyItem.map(i -> new SaveProvinceHistory(i, this)).orElse(null);

        this.buildings = new ArrayList<>(0);
        this.item.getChild("building_builders").ifPresent(buildersItem -> {
            buildersItem.getVariables().forEach(v -> {
                if (historyItem.isEmpty()) {
                    this.buildings.add(new ProvinceBuilding(v.getName(), v.getValue(), null, this.save.getGame().getBuilding(v.getName())));
                } else {
                    historyItem.get()
                               .getChildren()
                               .stream()
                               .filter(child -> child.hasVar(v.getName()))
                               .findFirst()
                               .ifPresentOrElse(child -> this.buildings.add(
                                                        new ProvinceBuilding(v.getName(), v.getValue(), Eu4Utils.stringToDate(child.getName()),
                                                                             this.save.getGame().getBuilding(v.getName()))),
                                                () -> this.buildings.add(
                                                        new ProvinceBuilding(v.getName(), v.getValue(), null, this.save.getGame().getBuilding(v.getName()))));
                }
            });
            this.buildings.sort(ProvinceBuilding::compareTo);
        });

        this.discoveryDates = this.item.getChild("discovery_dates2").map(ListOfDates::new).orElse(null);
        this.discoveryReligionDates = this.item.getChild("discovery_religion_dates2").map(ListOfDates::new).orElse(null);

        this.item.getChild("country_improve_count").ifPresent(improveCountItem -> {
            this.improveCount = new HashMap<>();
            for (int i = 0; i < improveCountItem.getVariables().size() - 1; i += 2) {
                this.improveCount.put(ClausewitzUtils.removeQuotes(improveCountItem.getVariables().get(i).getValue()),
                                      improveCountItem.getVariables().get(i + 1).getAsInt());
            }
        });

        List<ClausewitzItem> modifierItems = this.item.getChildren("modifier");
        this.modifiers = modifierItems.stream()
                                      .map(child -> new SaveModifier(child, this.save.getGame()))
                                      .collect(Collectors.toMap(modifier -> ClausewitzUtils.removeQuotes(modifier.getModifierName()), Function.identity()));

        this.rebelFaction = this.item.getChild("rebel_faction").map(Id::new).orElse(null);
        this.buildingConstruction = this.item.getChild("building_construction").map(i -> new ProvinceConstruction(i, this)).orElse(null);
        this.colonyConstruction = this.item.getChild("colony_construction").map(i -> new ProvinceConstruction(i, this)).orElse(null);
        this.missionaryConstruction = this.item.getChild("missionary_construction").map(i -> new ProvinceConstruction(i, this)).orElse(null);

        List<ClausewitzItem> unitsItems = this.item.getChildren("unit");

        if (!unitsItems.isEmpty()) {
            this.armies = new ArrayList<>(0);
            this.navies = new ArrayList<>(0);
            unitsItems.stream().map(Id::new).forEach(unitId -> {
                this.save.getCountries().values().stream().map(c -> c.getArmy(unitId)).filter(Objects::nonNull).findFirst().ifPresent(this.armies::add);
                this.save.getCountries().values().stream().map(c -> c.getNavy(unitId)).filter(Objects::nonNull).findFirst().ifPresent(this.navies::add);
            });
        }

        this.localAutonomy = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SaveProvince province)) {
            return false;
        }

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
