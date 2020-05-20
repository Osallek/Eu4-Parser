package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.changeprices.ChangePrices;
import com.osallek.eu4parser.model.combat.Combats;
import com.osallek.eu4parser.model.counters.IdCounters;
import com.osallek.eu4parser.model.country.Area;
import com.osallek.eu4parser.model.country.Country;
import com.osallek.eu4parser.model.country.TradeCompany;
import com.osallek.eu4parser.model.diplomacy.Diplomacy;
import com.osallek.eu4parser.model.empire.CelestialEmpire;
import com.osallek.eu4parser.model.empire.Hre;
import com.osallek.eu4parser.model.empire.HreReligionStatus;
import com.osallek.eu4parser.model.events.FiredEvents;
import com.osallek.eu4parser.model.events.PendingEvents;
import com.osallek.eu4parser.model.gameplayoptions.GameplayOptions;
import com.osallek.eu4parser.model.institutions.Institutions;
import com.osallek.eu4parser.model.province.Advisor;
import com.osallek.eu4parser.model.province.Province;
import com.osallek.eu4parser.model.religion.Religions;
import com.osallek.eu4parser.model.trade.TradeNode;
import com.osallek.eu4parser.model.war.ActiveWar;
import com.osallek.eu4parser.model.war.PreviousWar;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Save {

    //Todo in countries: map_area_data, trade, custom countries
    //Todo Teams, diplomacy, income_statistics, nation_size_statistics,
    // score_statistics, inflation_statistics, trade_company_manager, tech_level_dates, idea_dates

    private static final Logger LOGGER = Logger.getLogger(Save.class.getName());

    public final ClausewitzItem item;

    private GameplayOptions gameplayOptions;

    private IdCounters idCounters;

    private ListOfDates flags;

    private Map<String, Area> areas;

    private Institutions institutions;

    private Map<String, TradeNode> tradeNodes;

    private ChangePrices changePrices;

    private Hre hre;

    private CelestialEmpire celestialEmpire;

    private List<TradeLeague> tradeLeagues;

    private Religions religions;

    private FiredEvents firedEvents;

    private PendingEvents pendingEvents;

    private Map<Integer, Province> provinces;

    private Map<String, Country> countries;

    private final SortedMap<Integer, Country> greatPowers = new TreeMap<>();

    private final Map<Long, Advisor> advisors = new HashMap<>();

    private Diplomacy diplomacy;

    private Combats combats;

    private List<ActiveWar> activeWars;

    private List<PreviousWar> previousWars;

    private TechLevelDates techLevelDates;

    private ListOfDates ideaDates;

    public Save(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public GameplayOptions getGameplayOptions() {
        return this.gameplayOptions;
    }

    public Integer getSpeed() {
        return this.item.getVarAsInt("speed");
    }

    public void setSpeed(int speed) {
        if (speed < 1) {
            speed = 1;
        } else if (speed > 5) {
            speed = 5;
        }

        this.item.setVariable("speed", speed);
    }

    public Long getMultiplayerRandomSeed() {
        return this.item.getVarAsLong("multiplayer_random_seed");
    }

    public Long getMultiplayerRandomCount() {
        return this.item.getVarAsLong("multiplayer_random_count");
    }

    public IdCounters getIdCounters() {
        return idCounters;
    }

    public Integer getUnitIdCounter() {
        return this.item.getVarAsInt("unit");
    }

    public String getCurrentAge() {
        return this.item.getVarAsString("current_age");
    }

    public Double getNextAgeProgress() {
        return this.item.getVarAsDouble("next_age_progress");
    }

    /**
     * Used for units and armies
     */
    public int getAndIncrementUnitIdCounter() {
        ClausewitzVariable var = this.item.getVar("unit");

        if (var == null) {
            this.item.addVariable("unit", 2);

            return 1;
        } else {
            int value = var.getAsInt();
            var.setValue(value + 1);

            return value;
        }
    }

    public ListOfDates getFlags() {
        return flags;
    }

    public String getRevolutionTarget() {
        return this.item.getVarAsString("revolution_target");
    }

    public void setRevolutionTarget(String revolutionTarget) {
        revolutionTarget = ClausewitzUtils.addQuotes(revolutionTarget);

        if (revolutionTarget.length() == 5) {
            Integer order = null;
            ClausewitzItem orderItem = this.item.getChild("flags");

            if (orderItem != null) {
                order = orderItem.getOrder() + 1;
            }

            if (order == null) {
                orderItem = this.item.getChild("provinces");

                if (orderItem != null) {
                    order = orderItem.getOrder() - 1;
                }
            }

            if (order == null) {
                order = this.item.getNbObjects();
            }

            this.item.setVariable("revolution_target", revolutionTarget, order);
            setHasFirstRevolutionStarted(true, order + 1);
        }
    }

    public String getRevolutionTargetOriginalName() {
        return this.item.getVarAsString("revolution_target_original_name");
    }

    public void setRevolutionTargetOriginalName(String revolutionTargetOriginalName) {
        this.item.setVariable("revolution_target_original_name", revolutionTargetOriginalName);
    }

    public Boolean hasFirstRevolutionStarted() {
        return this.item.getVarAsBool("has_first_revolution_started");
    }

    void setHasFirstRevolutionStarted(boolean hasFirstRevolutionStarted, int order) {
        this.item.setVariable("has_first_revolution_started", hasFirstRevolutionStarted, order);
    }

    public Date getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(Date startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public Map<String, Area> getAreas() {
        return areas;
    }

    public Double getTotalMilitaryPower() {
        return this.item.getVarAsDouble("total_military_power");
    }

    public Double getAverageMilitaryPower() {
        return this.item.getVarAsDouble("average_military_power");
    }

    public Institutions getInstitutions() {
        return institutions;
    }

    public Map<String, TradeNode> getTradeNodes() {
        return tradeNodes;
    }

    public Map<Good, String> getProductionLeaders() {
        Map<Good, String> productionLeaders = new EnumMap<>(Good.class);
        ClausewitzList productionLeaderList = this.item.getList("production_leader_tag");

        if (productionLeaderList != null) {
            for (Good good : Good.values()) {
                productionLeaders.put(good, productionLeaderList.get(good.ordinal()));
            }
        }

        return productionLeaders;
    }

    public Map<Good, String> getGoodsTotalProduced() {
        Map<Good, String> productionLeaders = new EnumMap<>(Good.class);
        ClausewitzList totalProducedList = this.item.getList("tradegoods_total_produced");

        if (totalProducedList != null) {
            for (Good good : Good.values()) {
                productionLeaders.put(good, totalProducedList.get(good.ordinal()));
            }
        }

        return productionLeaders;
    }

    public ChangePrices getChangePrices() {
        return changePrices;
    }

    public Hre getHre() {
        return hre;
    }

    public CelestialEmpire getCelestialEmpire() {
        return celestialEmpire;
    }

    public Boolean getHreLeaguesActive() {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_leagues_status");

        if (hreLeaguesStatus != null) {
            return hreLeaguesStatus.getAsInt() == 1;
        }

        return null;
    }

    public void setHreLeaguesActive(boolean hreLeaguesActive) {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_leagues_status");

        if (hreLeaguesStatus != null) {
            hreLeaguesStatus.setValue(hreLeaguesActive ? 1 : 0);
        } else {
            this.item.addVariable("hre_leagues_status", hreLeaguesActive ? 1 : 0);
        }
    }

    public HreReligionStatus getHreReligionStatus() {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_religion_status");

        if (hreLeaguesStatus != null) {
            return HreReligionStatus.values()[hreLeaguesStatus.getAsInt()];
        }

        return null;
    }

    public void setHreReligionStatus(HreReligionStatus hreReligionStatus) {
        ClausewitzVariable hreReligionStatusVar = this.item.getVar("hre_religion_status");

        if (hreReligionStatusVar != null) {
            hreReligionStatusVar.setValue(hreReligionStatus.ordinal());
        } else {
            this.item.addVariable("hre_religion_status", hreReligionStatus.ordinal());
        }

        if (hreReligionStatus == HreReligionStatus.CATHOLIC) {
            if (this.religions != null) {
                Religion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(false);
                    catholic.setHreReligion(true);
                }

                Religion protestant = this.religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(true);
                    protestant.setHreReligion(false);
                }
            }
        } else if (hreReligionStatus == HreReligionStatus.PROTESTANT) {
            if (this.religions != null) {
                Religion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(true);
                    catholic.setHreReligion(false);
                }

                Religion protestant = this.religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(false);
                    protestant.setHreReligion(true);
                }
            }
        }
    }

    public List<TradeLeague> getTradeLeagues() {
        return tradeLeagues;
    }

    public void addTradeLeague(String... members) {
        TradeLeague.addToItem(this.item, members);
        refreshAttributes();
    }

    public Religions getReligions() {
        return religions;
    }

    public FiredEvents getFiredEvents() {
        return firedEvents;
    }

    public PendingEvents getPendingEvents() {
        return pendingEvents;
    }

    public Province getProvince(int id) {
        return this.provinces.get(id);
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    public Country getCountry(String tag) {
        return this.countries.get(tag);
    }

    public Map<String, Country> getCountries() {
        return countries;
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties,
                                                  boolean quickPeace, boolean moveTraders, boolean takeDecisions,
                                                  boolean embraceInstitutions, boolean developProvinces,
                                                  boolean disbandUnits, boolean changeFleetMissions,
                                                  boolean sendMissionaries, boolean convertCultures,
                                                  boolean promoteCultures, boolean braindead) {
        setAiPrefsForNotConfiguredPlayers(startWars, keepAlliances, keepTreaties, quickPeace, moveTraders,
                                          takeDecisions, embraceInstitutions, developProvinces, disbandUnits,
                                          changeFleetMissions, sendMissionaries, convertCultures, promoteCultures,
                                          braindead, -1);
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties,
                                                  boolean quickPeace, boolean moveTraders, boolean takeDecisions,
                                                  boolean embraceInstitutions, boolean developProvinces,
                                                  boolean disbandUnits, boolean changeFleetMissions,
                                                  boolean sendMissionaries, boolean convertCultures,
                                                  boolean promoteCultures, boolean braindead, int timeout) {
        this.countries.values()
                      .stream()
                      .filter(country -> country.getPlayerAiPrefsCommand() == null
                                         && Boolean.TRUE.equals(country.isHuman()))
                      .forEach(country -> country.setPlayerAiPrefsCommand(startWars, keepAlliances, keepTreaties,
                                                                          quickPeace, moveTraders, takeDecisions,
                                                                          embraceInstitutions, developProvinces,
                                                                          disbandUnits, changeFleetMissions,
                                                                          sendMissionaries, convertCultures,
                                                                          promoteCultures, braindead, timeout));
    }

    public SortedMap<Integer, Country> getGreatPowers() {
        return greatPowers;
    }

    public Map<Long, Advisor> getAdvisors() {
        return advisors;
    }

    public Diplomacy getDiplomacy() {
        return diplomacy;
    }

    public Combats getCombats() {
        return combats;
    }

    public List<ActiveWar> getActiveWars() {
        return activeWars;
    }

    public List<PreviousWar> getPreviousWars() {
        return previousWars;
    }

    public boolean getAchievementOk() {
        return this.item.getVarAsBool("achievement_ok");
    }

    public void addTradeCompany(String name, String owner, Integer... provinces) {
        owner = ClausewitzUtils.addQuotes(owner);

        String finalOwner = owner;
        provinces = Arrays.stream(provinces)
                          .filter(province -> getProvince(province).getOwner().equals(finalOwner))
                          .toArray(Integer[]::new);

        if (provinces.length > 0) {
            ClausewitzItem tradeCompanyManagerItem = this.item.getChild("trade_company_manager");

            if (tradeCompanyManagerItem == null) {
                tradeCompanyManagerItem = this.item.addChild("trade_company_manager");
            }

            TradeCompany.addToItem(tradeCompanyManagerItem, name, owner, provinces);
            refreshAttributes();
        }
    }

    public TechLevelDates getTechLevelDates() {
        return techLevelDates;
    }

    public ListOfDates getIdeaDates() {
        return ideaDates;
    }

    public String getChecksum() {
        return this.item.getVarAsString("checksum");
    }

    private void refreshAttributes() {
        ClausewitzItem gameplaySettings = this.item.getChild("gameplaysettings");

        if (gameplaySettings != null) {
            ClausewitzList gameplayOptionsList = gameplaySettings.getList("setgameplayoptions");

            if (gameplayOptionsList != null && !gameplayOptionsList.isEmpty()) {
                this.gameplayOptions = new GameplayOptions(gameplayOptionsList);
            }
        }

        ClausewitzItem flagsItem = this.item.getChild("flags");

        if (flagsItem != null) {
            this.flags = new ListOfDates(flagsItem);
        }

        ClausewitzList idCountersList = this.item.getList("id_counters");

        if (idCountersList != null) {
            this.idCounters = new IdCounters(idCountersList);
        }

        ClausewitzList institutionOrigins = this.item.getList("institution_origin");
        ClausewitzList institutionAvailable = this.item.getList("institutions");

        if (institutionOrigins != null && institutionAvailable != null) {
            this.institutions = new Institutions(institutionOrigins, institutionAvailable);
        }

        ClausewitzItem tradeItem = this.item.getChild("trade");

        if (tradeItem != null) {
            this.tradeNodes = tradeItem.getChildren("node")
                                       .stream()
                                       .map(TradeNode::new)
                                       .collect(Collectors.toMap(tradeNode -> ClausewitzUtils.removeQuotes(tradeNode.getName()),
                                                                 Function.identity()));
        }

        ClausewitzItem changePricesItem = this.item.getChild("change_price");

        if (changePricesItem != null) {
            this.changePrices = new ChangePrices(changePricesItem);
        }

        ClausewitzItem hreItem = this.item.getChild("empire");

        if (hreItem != null) {
            this.hre = new Hre(hreItem);
        }

        ClausewitzItem celestialEmpireItem = this.item.getChild("celestial_empire");

        if (celestialEmpireItem != null) {
            this.celestialEmpire = new CelestialEmpire(celestialEmpireItem);
        }

        List<ClausewitzItem> tradeLeaguesItems = this.item.getChildren("trade_league");
        this.tradeLeagues = tradeLeaguesItems.stream()
                                             .map(TradeLeague::new)
                                             .collect(Collectors.toList());

        ClausewitzItem religionsItem = this.item.getChild("religions");
        ClausewitzItem religionInstantDateItem = this.item.getChild("religion_instance_data");

        if (religionsItem != null || religionInstantDateItem != null) {
            this.religions = new Religions(religionsItem, religionInstantDateItem);
        }

        ClausewitzItem firedEventsItem = this.item.getChild("fired_events");

        if (firedEventsItem != null) {
            this.firedEvents = new FiredEvents(firedEventsItem);
        }

        ClausewitzItem pendingEventsItem = this.item.getChild("pending_events");

        if (pendingEventsItem != null) {
            this.pendingEvents = new PendingEvents(pendingEventsItem);
        }

        ClausewitzItem countriesItem = this.item.getChild("countries");

        if (countriesItem != null) {
            this.countries = countriesItem.getChildren()
                                          .stream()
                                          .map(countryItem -> new Country(countryItem, this))
                                          .collect(Collectors.toMap(Country::getTag, Function.identity(), (x, y) -> y, LinkedHashMap::new));
        }

        ClausewitzList playersCountriesList = this.item.getList("players_countries");

        if (playersCountriesList != null) {
            for (int i = playersCountriesList.getValues().size() - 1; i > 0; i -= 2) {
                if (this.countries.containsKey(ClausewitzUtils.removeQuotes(playersCountriesList.get(i)))) {
                    this.getCountry(ClausewitzUtils.removeQuotes(playersCountriesList.get(i)))
                        .setPlayer(playersCountriesList.get(i - 1));
                }
            }
        }

        ClausewitzItem greatPowersItem = this.item.getChild("great_powers");

        if (greatPowersItem != null) {
            greatPowersItem.getChildren("original").forEach(child -> {
                Country country = this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("country")));
                this.greatPowers.put(child.getVarAsInt("rank"), country);
            });
        }

        ClausewitzItem provincesItems = this.item.getChild("provinces");

        if (provincesItems != null) {
            this.provinces = provincesItems.getChildren()
                                           .stream()
                                           .map(provinceItem -> new Province(provinceItem, this))
                                           .collect(Collectors.toMap(Province::getId, Function.identity(), (x, y) -> y, LinkedHashMap::new));
        }

        ClausewitzItem mapAreaDataItem = this.item.getChild("map_area_data");

        if (mapAreaDataItem != null) {
            this.areas = mapAreaDataItem.getChildren()
                                        .stream()
                                        .filter(child -> child.getChild("state") != null
                                                         || child.getChild("investments") != null)
                                        .map(child -> new Area(child, this))
                                        .collect(Collectors.toMap(area -> ClausewitzUtils.removeQuotes(area.getName()),
                                                                  Function.identity()));
        }

        ClausewitzItem activeAdvisorsItem = this.item.getChild("active_advisors");

        if (activeAdvisorsItem != null) {
            activeAdvisorsItem.getChildren().forEach(child -> {
                Country country = this.getCountry(ClausewitzUtils.removeQuotes(child.getName()));
                child.getChildren("advisor")
                     .stream()
                     .map(Id::new)
                     .forEach(id -> country.getActiveAdvisors().put(id.getId(), this.advisors.get(id.getId())));
            });
        }

        ClausewitzItem diplomacyItem = this.item.getChild("diplomacy");

        if (diplomacyItem != null) {
            this.diplomacy = new Diplomacy(diplomacyItem, this);
        }

        ClausewitzItem combatItem = this.item.getChild("combat");

        if (combatItem != null) {
            this.combats = new Combats(combatItem);
        }

        List<ClausewitzItem> activeWarsItems = this.item.getChildren("active_war");
        this.activeWars = activeWarsItems.stream()
                                         .map(ActiveWar::new)
                                         .collect(Collectors.toList());

        List<ClausewitzItem> previousWarsItems = this.item.getChildren("previous_war");
        this.previousWars = previousWarsItems.stream()
                                             .map(PreviousWar::new)
                                             .collect(Collectors.toList());

        ClausewitzItem incomeStatisticsItem = this.item.getChild("income_statistics");

        if (incomeStatisticsItem != null) {
            incomeStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .getIncomeStatistics()
                        .putAll(dataItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem nationSizeStatisticsItem = this.item.getChild("nation_size_statistics");

        if (nationSizeStatisticsItem != null) {
            nationSizeStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .getNationSizeStatistics()
                        .putAll(dataItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem scoreStatisticsItem = this.item.getChild("score_statistics");

        if (scoreStatisticsItem != null) {
            scoreStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .getScoreStatistics()
                        .putAll(dataItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem inflationStatisticsItem = this.item.getChild("inflation_statistics");

        if (inflationStatisticsItem != null) {
            inflationStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .getInflationStatistics()
                        .putAll(dataItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem tradeCompanyManagerItem = this.item.getChild("trade_company_manager");

        if (tradeCompanyManagerItem != null) {
            tradeCompanyManagerItem.getChildren("trade_company").forEach(child -> {
                TradeCompany company = new TradeCompany(child);
                this.getCountry(ClausewitzUtils.removeQuotes(company.getOwner())).getTradeCompanies().add(company);
            });
        }

        ClausewitzItem techLevelDatesItem = this.item.getChild("tech_level_dates");

        if (techLevelDatesItem != null) {
            this.techLevelDates = new TechLevelDates(techLevelDatesItem);
        }

        ClausewitzItem ideaDatesItem = this.item.getChild("idea_dates");

        if (ideaDatesItem != null) {
            this.ideaDates = new ListOfDates(ideaDatesItem);
        }
    }
}
