package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.LauncherSettings;
import fr.osallek.eu4parser.model.game.Age;
import fr.osallek.eu4parser.model.game.Area;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Hegemon;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.game.ProvinceHistoryItem;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.save.changeprices.ChangePrices;
import fr.osallek.eu4parser.model.save.combat.Combats;
import fr.osallek.eu4parser.model.save.counters.IdCounters;
import fr.osallek.eu4parser.model.save.country.SaveArea;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveHegemon;
import fr.osallek.eu4parser.model.save.country.SaveTradeCompany;
import fr.osallek.eu4parser.model.save.diplomacy.Diplomacy;
import fr.osallek.eu4parser.model.save.empire.CelestialEmpire;
import fr.osallek.eu4parser.model.save.empire.Hre;
import fr.osallek.eu4parser.model.save.empire.HreReligionStatus;
import fr.osallek.eu4parser.model.save.events.FiredEvents;
import fr.osallek.eu4parser.model.save.events.PendingEvents;
import fr.osallek.eu4parser.model.save.gameplayoptions.GameplayOptions;
import fr.osallek.eu4parser.model.save.institutions.Institutions;
import fr.osallek.eu4parser.model.save.province.SaveAdvisor;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import fr.osallek.eu4parser.model.save.province.SaveProvinceHistoryEvent;
import fr.osallek.eu4parser.model.save.religion.Religions;
import fr.osallek.eu4parser.model.save.revolution.Revolution;
import fr.osallek.eu4parser.model.save.trade.SaveTradeNode;
import fr.osallek.eu4parser.model.save.war.ActiveWar;
import fr.osallek.eu4parser.model.save.war.PreviousWar;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Save {

    private final String name;

    private final Game game;

    public final ClausewitzItem aiItem;

    public final ClausewitzItem gamestateItem;

    public final ClausewitzItem metaItem;

    private final boolean compressed;

    private LocalDate date;

    private Map<Integer, SaveProvince> provinces; //Kept here because heavy to compute and used a lot

    private List<SaveProvince> cities;

    private Map<String, SaveCountry> countries;

    private List<SaveCountry> playableCountries;

    public Save(String name, Path gameFolderPath, ClausewitzItem item, LauncherSettings launcherSettings) throws IOException {
        this(name, gameFolderPath, item, item, item, false, launcherSettings);
    }

    public Save(String name, Path gameFolderPath, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem,
                LauncherSettings launcherSettings) throws IOException {
        this(name, gameFolderPath, gamestateItem, aiItem, metaItem, true, launcherSettings);
    }

    private Save(String name, Path gameFolderPath, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem,
                 boolean compressed, LauncherSettings launcherSettings) throws IOException {
        this.name = name;
        this.gamestateItem = gamestateItem;
        this.aiItem = aiItem;
        this.metaItem = metaItem;
        this.compressed = compressed;
        this.game = new Game(gameFolderPath, launcherSettings, getModEnabled());
        this.date = this.metaItem.getVarAsDate("date");
        computeAdvisors();
    }

    public Save(String name, ClausewitzItem item, Game game) {
        this(name, item, item, item, false, game);
    }

    public Save(String name, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem, boolean compressed, Game game) {
        this.name = name;
        this.gamestateItem = gamestateItem;
        this.aiItem = aiItem;
        this.metaItem = metaItem;
        this.compressed = compressed;
        this.game = game;
        this.date = this.metaItem.getVarAsDate("date");
        computeAdvisors();
    }

    public String getName() {
        return name;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public Game getGame() {
        return game;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.metaItem.setVariable("date", date);
        this.date = date;
    }

    public String getPlayer() {
        return this.metaItem.getVarAsString("player");
    }

    public void setPlayer(String country) {
        country = ClausewitzUtils.addQuotes(country);

        if (country.length() == 5) {
            this.metaItem.setVariable("player", country);
        }
    }

    public SaveCountry getPlayedCountry() {
        return getCountry(ClausewitzUtils.removeQuotes(getPlayer()));
    }

    public String getDisplayedCountryName() {
        return this.metaItem.getVarAsString("displayed_country_name");
    }

    public List<String> getSavegameVersions() {
        ClausewitzList list = this.metaItem.getList("savegame_versions");

        if (list != null) {
            return list.getValues();
        }

        return new ArrayList<>();
    }

    public List<String> getDlcEnabled() {
        ClausewitzList list = this.metaItem.getList("dlc_enabled");

        if (list != null) {
            return list.getValues();
        }

        return new ArrayList<>();
    }

    public List<String> getModEnabled() {
        ClausewitzItem child = this.metaItem.getChild("mods_enabled_names");

        if (child != null) {
            return child.getChildren().stream().map(item -> item.getVarAsString("filename")).filter(Objects::nonNull).toList();
        }

        return new ArrayList<>();
    }

    public boolean isMultiPlayer() {
        return BooleanUtils.toBoolean(this.metaItem.getVarAsBool("multi_player"));
    }

    public boolean isRandomNewWorld() {
        return BooleanUtils.toBoolean(this.metaItem.getVarAsBool("is_random_new_world"));
    }

    public GameplayOptions getGameplayOptions() {
        ClausewitzItem gameplaySettings = this.gamestateItem.getChild("gameplaysettings");

        if (gameplaySettings != null) {
            ClausewitzList gameplayOptionsList = gameplaySettings.getList("setgameplayoptions");

            if (gameplayOptionsList != null && !gameplayOptionsList.isEmpty()) {
                return new GameplayOptions(gameplayOptionsList);
            }
        }

        return null;
    }

    public Integer getSpeed() {
        return this.gamestateItem.getVarAsInt("speed");
    }

    public void setSpeed(int speed) {
        if (speed < 1) {
            speed = 1;
        } else if (speed > 5) {
            speed = 5;
        }

        this.gamestateItem.setVariable("speed", speed);
    }

    public IdCounters getIdCounters() {
        ClausewitzList idCountersList = this.gamestateItem.getList("id_counters");
        return idCountersList != null ? new IdCounters(idCountersList) : null;
    }

    public Integer getUnitIdCounter() {
        return this.gamestateItem.getVarAsInt("unit");
    }

    public Age getCurrentAge() {
        return this.game.getAge(ClausewitzUtils.removeQuotes(this.gamestateItem.getVarAsString("current_age")));
    }

    public Double getNextAgeProgress() {
        return this.gamestateItem.getVarAsDouble("next_age_progress");
    }

    /**
     * Used for units and armies
     */
    public int getAndIncrementUnitIdCounter() {
        Integer var = this.gamestateItem.getVarAsInt("unit");

        if (var == null) {
            this.gamestateItem.addVariable("unit", 2);

            return 1;
        } else {
            this.gamestateItem.setVariable("unit", var + 1);

            return var;
        }
    }

    public ListOfDates getFlags() {
        ClausewitzItem flagsItem = this.gamestateItem.getChild("flags");
        return flagsItem != null ? new ListOfDates(flagsItem) : null;
    }

    public Revolution getRevolution() {
        ClausewitzItem revolutionItem = this.gamestateItem.getChild("revolution");
        return revolutionItem != null ? new Revolution(revolutionItem, this) : null;
    }

    public LocalDate getStartDate() {
        return this.gamestateItem.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.gamestateItem.setVariable("start_date", startDate);
    }

    public Stream<SaveArea> getAreasStream() {
        ClausewitzItem mapAreaDataItem = this.gamestateItem.getChild("map_area_data");

        if (mapAreaDataItem != null) {
            Collection<String> names = this.game.getAreasNames();
            return mapAreaDataItem.getChildren()
                                  .stream()
                                  .filter(child -> names.contains(child.getName()))
                                  .map(child -> new SaveArea(child, this));
        }

        return Stream.empty();
    }

    public Map<String, SaveArea> getAreas() {
        return getAreasStream().collect(Collectors.toMap(area -> ClausewitzUtils.removeQuotes(area.getName()), Function.identity()));
    }

    public Double getTotalMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("total_military_power");
    }

    public Double getAverageMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("average_military_power");
    }

    public Institutions getInstitutions() {
        ClausewitzList institutionOrigins = this.gamestateItem.getList("institution_origin");
        ClausewitzList institutionAvailable = this.gamestateItem.getList("institutions");

        return institutionOrigins != null && institutionAvailable != null ? new Institutions(institutionOrigins, institutionAvailable, this) : null;
    }

    public List<SaveHegemon> getHegemons() {
        return this.game.getHegemons().stream().flatMap(hegemon -> {
            ClausewitzItem child = this.gamestateItem.getChild(hegemon.getName());

            if (child != null) {
                return Stream.of(new SaveHegemon(child, this, hegemon));
            }

            return Stream.empty();
        }).toList();
    }

    public void setHegemon(Hegemon hegemon, SaveCountry country, double progress) {
        Optional<SaveHegemon> saveHegemon = getHegemons().stream().filter(h -> hegemon.equals(h.hegemon())).findFirst();

        if (saveHegemon.isPresent()) {
            saveHegemon.get().setCountry(country);
            saveHegemon.get().setProgress(progress);
        } else {
            SaveHegemon.addToItem(this.gamestateItem, hegemon.getName(), country, progress, this.gamestateItem.getList("institutions").getOrder() + 1);
        }
    }

    public Stream<SaveTradeNode> getTradeNodesStream() {
        AtomicInteger i = new AtomicInteger(1);
        ClausewitzItem tradeItem = this.gamestateItem.getChild("trade");

        if (tradeItem != null) {
            return tradeItem.getChildren("node")
                            .stream()
                            .map(item -> new SaveTradeNode(item, this, i.getAndIncrement()));
        }

        return Stream.empty();
    }

    public Map<String, SaveTradeNode> getTradeNodes() {
        return getTradeNodesStream().collect(Collectors.toMap(tradeNode -> ClausewitzUtils.removeQuotes(tradeNode.getName()), Function.identity()));
    }

    public SaveTradeNode getTradeNode(String name) {
        return getTradeNodesStream().filter(node -> node.getName().equals(name)).findFirst().orElse(null);
    }

    public SaveTradeNode getTradeNode(int index) {
        return getTradeNodesStream().filter(node -> node.getIndex() == index).findFirst().orElse(null);
    }

    public Map<TradeGood, SaveCountry> getProductionLeaders() {
        Map<TradeGood, SaveCountry> productionLeaders = new LinkedHashMap<>();
        ClausewitzList productionLeaderList = this.gamestateItem.getList("production_leader_tag");

        if (productionLeaderList != null) {
            //Start from 1 because first leader is for empty trade good
            for (int i = 1; i < productionLeaderList.size(); i++) {
                productionLeaders.put(this.game.getTradeGood(i - 1), getCountry(productionLeaderList.get(i)));
            }
        }

        return productionLeaders;
    }

    public Map<TradeGood, String> getGoodsTotalProduced() {
        Map<TradeGood, String> productionLeaders = new LinkedHashMap<>();
        ClausewitzList totalProducedList = this.gamestateItem.getList("tradegoods_total_produced");

        if (totalProducedList != null) {
            for (int i = 0; i < totalProducedList.size(); i++) {
                productionLeaders.put(this.game.getTradeGood(i - 1), totalProducedList.get(i));
            }
        }

        return productionLeaders;
    }

    public ChangePrices getChangePrices() {
        ClausewitzItem changePricesItem = this.gamestateItem.getChild("change_price");
        return changePricesItem != null ? new ChangePrices(changePricesItem, this.game) : null;
    }

    public Map<Integer, Id> getIds() {
        return this.gamestateItem.getChildren("id").stream().map(Id::new).collect(Collectors.toMap(Id::getType, Function.identity()));
    }

    public Map<Id, RebelFaction> getRebelFactions() {
        return this.gamestateItem.getChildren("rebel_faction").stream()
                                 .map(child -> new RebelFaction(child, this))
                                 .collect(Collectors.toMap(RebelFaction::getId, Function.identity(), (a, b) -> b));
    }

    public Hre getHre() {
        ClausewitzItem hreItem = this.gamestateItem.getChild("empire");
        return hreItem != null ? new Hre(hreItem, this) : null;
    }

    public CelestialEmpire getCelestialEmpire() {
        ClausewitzItem celestialEmpireItem = this.gamestateItem.getChild("celestial_empire");
        return celestialEmpireItem != null ? new CelestialEmpire(celestialEmpireItem, this) : null;
    }

    public Boolean getHreLeaguesActive() {
        Integer hreLeaguesStatus = this.gamestateItem.getVarAsInt("hre_leagues_status");

        return hreLeaguesStatus == null ? null : hreLeaguesStatus == 1;
    }

    public void setHreLeaguesActive(boolean hreLeaguesActive) {
        this.gamestateItem.setVariable("hre_leagues_status", hreLeaguesActive ? 1 : 0);
    }

    public HreReligionStatus getHreReligionStatus() {
        Integer hreLeaguesStatus = this.gamestateItem.getVarAsInt("hre_religion_status");

        return hreLeaguesStatus == null ? null : HreReligionStatus.values()[hreLeaguesStatus];
    }

    public void setHreReligionStatus(HreReligionStatus hreReligionStatus) {
        this.gamestateItem.setVariable("hre_religion_status", hreReligionStatus.ordinal());

        if (hreReligionStatus == HreReligionStatus.CATHOLIC) {
            Religions religions = getReligions();
            if (religions != null) {
                SaveReligion catholic = religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(false);
                    catholic.setHreReligion(true);
                }

                SaveReligion protestant = religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(true);
                    protestant.setHreReligion(false);
                }
            }
        } else if (hreReligionStatus == HreReligionStatus.PROTESTANT) {
            Religions religions = getReligions();
            if (religions != null) {
                SaveReligion catholic = religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(true);
                    catholic.setHreReligion(false);
                }

                SaveReligion protestant = religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(false);
                    protestant.setHreReligion(true);
                }
            }
        }
    }

    public Stream<TradeLeague> getTradeLeaguesStream() {
        return this.gamestateItem.getChildren("trade_league").stream().map(child -> new TradeLeague(child, this));
    }

    public TradeLeague getTradeLeague(SaveCountry country) {
        return getTradeLeaguesStream().filter(tl -> tl.hasMember(country)).findFirst().orElse(null);
    }

    public List<TradeLeague> getTradeLeagues() {
        return getTradeLeaguesStream().toList();
    }

    public void addTradeLeague(String... members) {
        TradeLeague.addToItem(this.gamestateItem, members);
    }

    public Religions getReligions() {
        ClausewitzItem religionsItem = this.gamestateItem.getChild("religions");
        ClausewitzItem religionInstantDateItem = this.gamestateItem.getChild("religion_instance_data");
        return religionsItem != null || religionInstantDateItem != null ? new Religions(religionsItem, religionInstantDateItem, this) : null;
    }

    public FiredEvents getFiredEvents() {
        ClausewitzItem firedEventsItem = this.gamestateItem.getChild("fired_events");
        return firedEventsItem != null ? new FiredEvents(firedEventsItem, this.game) : null;
    }

    public PendingEvents getPendingEvents() {
        ClausewitzItem pendingEventsItem = this.gamestateItem.getChild("pending_events");
        return pendingEventsItem != null ? new PendingEvents(pendingEventsItem) : null;
    }

    public SaveProvince getProvince(Integer id) {
        if (id == null) {
            return null;
        }

        return getProvinces().get(id);
    }

    public Map<Integer, SaveProvince> getProvinces() {
        computeProvinces();
        return this.provinces;
    }

    public List<SaveProvince> getCities() {
        computeProvinces();
        return this.cities;
    }

    private synchronized void computeProvinces() {
        if (this.provinces == null || this.cities == null) {
            this.provinces = new HashMap<>();
            this.cities = new ArrayList<>();
            ClausewitzItem provincesItems = this.gamestateItem.getChild("provinces");

            if (provincesItems != null) {
                provincesItems.getChildren().forEach(provinceItem -> {
                    Province province = this.game.getProvince(Math.abs(Integer.parseInt(provinceItem.getName())));
                    if (province != null) {
                        SaveProvince saveProvince = new SaveProvince(provinceItem, province, this);
                        this.provinces.put(saveProvince.getId(), saveProvince);
                        this.game.getProvincesByColor()
                                 .compute(Eu4Utils.rgbToColor(saveProvince.getRed(), saveProvince.getGreen(), saveProvince.getBlue()),
                                          (color, p) -> p = saveProvince);
                    }
                });
                this.cities = this.provinces.values()
                                            .stream()
                                            .filter(SaveProvince::isCity)
                                            .sorted((o1, o2) -> Eu4Utils.COLLATOR.compare(o1.getName(), o2.getName()))
                                            .toList();
            }
        }
    }

    public SaveCountry getCountry(String tag) {
        if (tag == null) {
            return null;
        }

        return getCountries().get(ClausewitzUtils.removeQuotes(tag));
    }

    public Map<String, SaveCountry> getCountries() {
        computeCountries();
        return this.countries;
    }

    public List<SaveCountry> getPlayableCountries() {
        computeCountries();
        return this.playableCountries;
    }

    private synchronized void computeCountries() {
        if (this.countries == null || this.playableCountries == null) {
            this.countries = new HashMap<>();
            this.playableCountries = new ArrayList<>();
            ClausewitzItem countriesItem = this.gamestateItem.getChild("countries");

            if (countriesItem != null) {
                this.countries = countriesItem.getChildren()
                                              .stream()
                                              .map(countryItem -> new SaveCountry(countryItem, this))
                                              .collect(Collectors.toMap(SaveCountry::getTag, Function.identity(), (x, y) -> y, LinkedHashMap::new));
                this.playableCountries = this.countries.values().stream().filter(SaveCountry::isPlayable).toList();
            }
        }
    }

    public List<SaveTeam> getTeams() {
        ClausewitzItem teamsItem = this.gamestateItem.getChild("teams");

        if (teamsItem != null) {
            return teamsItem.getChildren("team").stream().map(item -> new SaveTeam(item, this)).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public void addTeam(String name, List<String> members) {
        ClausewitzItem teamsItem = this.gamestateItem.getChild("teams");

        if (teamsItem == null) {
            teamsItem = new ClausewitzItem(this.gamestateItem, "teams", this.gamestateItem.getChild("players_countries").getOrder());
            this.gamestateItem.addChild(teamsItem, true);
        }

        ClausewitzItem teamItem = teamsItem.addChild("team");
        SaveTeam team = new SaveTeam(teamItem, this);
        team.setName(name);
        members.forEach(team::addCountry);
    }

    public void removeTeam(String name) {
        ClausewitzItem teamsItem = this.gamestateItem.getChild("teams");

        if (teamsItem == null) {
            return;
        }

        getTeams().stream().filter(saveTeam -> name.equals(saveTeam.name())).findFirst().ifPresent(saveTeam -> teamsItem.removeChild(saveTeam.item()));
    }

    public Map<String, SaveCountry> getPlayers() {
        ClausewitzList playersCountriesList = this.gamestateItem.getList("players_countries");
        Map<String, SaveCountry> players = new LinkedHashMap<>();

        if (playersCountriesList != null) {
            for (int i = playersCountriesList.getValues().size() - 1; i > 0; i -= 2) {
                if (getCountry(ClausewitzUtils.removeQuotes(playersCountriesList.get(i))) != null) {
                    players.put(playersCountriesList.get(i - 1), getCountry(ClausewitzUtils.removeQuotes(playersCountriesList.get(i))));
                }
            }
        }

        return players;
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties, boolean quickPeace, boolean moveTraders,
                                                  boolean takeDecisions, boolean embraceInstitutions, boolean developProvinces, boolean disbandUnits,
                                                  boolean changeFleetMissions, boolean sendMissionaries, boolean convertCultures, boolean promoteCultures,
                                                  boolean braindead) {
        setAiPrefsForNotConfiguredPlayers(startWars, keepAlliances, keepTreaties, quickPeace, moveTraders, takeDecisions, embraceInstitutions, developProvinces,
                                          disbandUnits, changeFleetMissions, sendMissionaries, convertCultures, promoteCultures, braindead, -1);
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties, boolean quickPeace, boolean moveTraders,
                                                  boolean takeDecisions, boolean embraceInstitutions, boolean developProvinces, boolean disbandUnits,
                                                  boolean changeFleetMissions, boolean sendMissionaries, boolean convertCultures, boolean promoteCultures,
                                                  boolean braindead, int timeout) {
        this.countries.values()
                      .stream()
                      .filter(country -> country.getPlayerAiPrefsCommand() == null && BooleanUtils.toBoolean(country.isHuman()))
                      .forEach(country -> country.setPlayerAiPrefsCommand(startWars, keepAlliances, keepTreaties, quickPeace, moveTraders, takeDecisions,
                                                                          embraceInstitutions, developProvinces, disbandUnits, changeFleetMissions,
                                                                          sendMissionaries, convertCultures, promoteCultures, braindead, timeout));
    }

    public Stream<Map.Entry<Integer, SaveCountry>> getGreatPowersStream() {
        ClausewitzItem greatPowersItem = this.gamestateItem.getChild("great_powers");
        if (greatPowersItem != null) {
            return greatPowersItem.getChildren("original")
                                  .stream()
                                  .map(child -> Pair.of(child.getVarAsInt("rank"), getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("country")))));
        }

        return Stream.empty();
    }

    public Integer getGreatPowersRank(SaveCountry country) {
        return getGreatPowersStream().filter(c -> c.getValue().equals(country)).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    public Stream<SaveAdvisor> getAdvisorsStream() {
        return getCountries().values().stream().map(SaveCountry::getAdvisors).map(Map::values).flatMap(Collection::stream);
    }

    public SaveAdvisor getAdvisor(int id) {
        return getAdvisorsStream().filter(saveAdvisor -> saveAdvisor.getId().getId().equals(id)).findFirst().orElse(null);
    }

    public Stream<Map.Entry<SaveCountry, Stream<Id>>> getActiveAdvisorsStream() {
        ClausewitzItem activeAdvisorsItem = this.gamestateItem.getChild("active_advisors");

        if (activeAdvisorsItem != null) {
            return activeAdvisorsItem.getChildren()
                                     .stream()
                                     .map(child -> Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getName())),
                                                           child.getChildren("advisor").stream().map(Id::new)));
        }
        return Stream.empty();
    }

    public Map<SaveCountry, List<Id>> getActiveAdvisors() {
        return getActiveAdvisorsStream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toList()));
    }

    public List<Id> getActiveAdvisors(SaveCountry country) {
        return getActiveAdvisorsStream().filter(e -> e.getKey().equals(country))
                                        .findFirst()
                                        .map(Map.Entry::getValue)
                                        .map(Stream::toList)
                                        .orElse(new ArrayList<>());
    }

    public Stream<Map.Entry<SaveCountry, SortedMap<Integer, Integer>>> getIncomeStatisticsStream() {
        ClausewitzItem incomeStatisticsItem = this.gamestateItem.getChild("income_statistics");

        if (incomeStatisticsItem != null) {
            return incomeStatisticsItem.getChildren("ledger_data").stream().map(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))),
                                   dataItem.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()), ClausewitzVariable::getAsInt, (a, b) -> a,
                                                                     TreeMap::new)));
                }
                return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))), new TreeMap<>());
            });
        }

        return Stream.empty();
    }

    public SortedMap<Integer, Integer> getIncomeStatistics(SaveCountry country) {
        return getIncomeStatisticsStream().filter(e -> e.getKey().equals(country)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public Stream<Map.Entry<SaveCountry, SortedMap<Integer, Integer>>> getScoreStatisticsStream() {
        ClausewitzItem scoreStatisticsItem = this.gamestateItem.getChild("score_statistics");

        if (scoreStatisticsItem != null) {
            return scoreStatisticsItem.getChildren("ledger_data").stream().map(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))),
                                   dataItem.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()), ClausewitzVariable::getAsInt, (a, b) -> a,
                                                                     TreeMap::new)));
                }
                return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))), new TreeMap<>());
            });
        }

        return Stream.empty();
    }

    public SortedMap<Integer, Integer> getScoreStatistics(SaveCountry country) {
        return getScoreStatisticsStream().filter(e -> e.getKey().equals(country)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public Stream<Map.Entry<SaveCountry, SortedMap<Integer, Integer>>> getInflationStatisticsStream() {
        ClausewitzItem inflationStatisticsItem = this.gamestateItem.getChild("inflation_statistics");

        if (inflationStatisticsItem != null) {
            return inflationStatisticsItem.getChildren("ledger_data").stream().map(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))),
                                   dataItem.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()), ClausewitzVariable::getAsInt, (a, b) -> a,
                                                                     TreeMap::new)));
                }
                return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))), new TreeMap<>());
            });
        }

        return Stream.empty();
    }

    public SortedMap<Integer, Integer> getInflationStatistics(SaveCountry country) {
        return getInflationStatisticsStream().filter(e -> e.getKey().equals(country)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public Stream<Map.Entry<SaveCountry, SortedMap<Integer, Integer>>> getNationSizeStatistics() {
        ClausewitzItem incomeStatisticsItem = this.gamestateItem.getChild("nation_size_statistics");

        if (incomeStatisticsItem != null) {
            return incomeStatisticsItem.getChildren("ledger_data").stream().map(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))),
                                   dataItem.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()), ClausewitzVariable::getAsInt, (a, b) -> a,
                                                                     TreeMap::new)));
                }
                return Pair.of(getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name"))), new TreeMap<>());
            });
        }

        return Stream.empty();
    }

    public SortedMap<Integer, Integer> getNationSizeStatistics(SaveCountry country) {
        return getNationSizeStatistics().filter(e -> e.getKey().equals(country)).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public Stream<SaveTradeCompany> getTradeCompagniesStream() {
        ClausewitzItem tradeCompanyManagerItem = this.gamestateItem.getChild("trade_company_manager");

        if (tradeCompanyManagerItem != null) {
            return tradeCompanyManagerItem.getChildren("trade_company").stream().map(child -> new SaveTradeCompany(child, this));
        }

        return Stream.empty();
    }

    public List<SaveTradeCompany> getTradeCompagnies(SaveCountry country) {
        return getTradeCompagniesStream().filter(c -> c.getOwner().equals(country)).toList();
    }

    public Diplomacy getDiplomacy() {
        ClausewitzItem diplomacyItem = this.gamestateItem.getChild("diplomacy");
        return diplomacyItem != null ? new Diplomacy(diplomacyItem, this) : null;
    }

    public Combats getCombats() {
        ClausewitzItem combatItem = this.gamestateItem.getChild("combat");
        return combatItem != null ? new Combats(combatItem, this) : null;
    }

    public List<ActiveWar> getActiveWars() {
        return this.gamestateItem.getChildren("active_war").stream().map(ActiveWar::new).toList();
    }

    public List<PreviousWar> getPreviousWars() {
        return this.gamestateItem.getChildren("previous_war").stream().map(PreviousWar::new).toList();
    }

    public boolean getAchievementOk() {
        return BooleanUtils.toBoolean(this.gamestateItem.getVarAsBool("achievement_ok"));
    }

    public void addTradeCompany(String name, SaveProvince province) {
        ClausewitzItem tradeCompanyManagerItem = this.gamestateItem.getChild("trade_company_manager");

        if (tradeCompanyManagerItem == null) {
            tradeCompanyManagerItem = this.gamestateItem.addChild("trade_company_manager");
        }

        SaveTradeCompany.addToItem(this, tradeCompanyManagerItem, name, province);
    }

    public TechLevelDates getTechLevelDates() {
        ClausewitzItem techLevelDatesItem = this.gamestateItem.getLastChild("tech_level_dates");
        return techLevelDatesItem != null ? new TechLevelDates(techLevelDatesItem) : null;
    }

    public ListOfDates getIdeaDates() {
        ClausewitzItem ideaDatesItem = this.gamestateItem.getChild("idea_dates");
        return ideaDatesItem != null ? new ListOfDates(ideaDatesItem) : null;
    }

    public String getChecksum() {
        return this.gamestateItem.getLastVarAsString("checksum"); //Use last because know it is at the end of the file
    }

    public SaveProvince getProvinceByColor(int red, int green, int blue) {
        Province province = this.game.getProvincesByColor().get(new Color(red, green, blue).getRGB());

        return province == null ? null : this.provinces.get(province.getId());
    }

    public List<SaveGreatProject> getGreatProjects() {
        ClausewitzItem greatProjectsItem = this.gamestateItem.getChild("great_projects");

        if (greatProjectsItem == null) {
            return new ArrayList<>();
        }

        return greatProjectsItem.getChildren().stream().map(item -> new SaveGreatProject(this, item)).toList();
    }

    public SaveGreatProject getGreatProject(String name) {
        return getGreatProjects().stream().filter(project -> ClausewitzUtils.removeQuotes(name).equals(project.getName())).findFirst().orElse(null);
    }

    public void writeAi(BufferedWriter bufferedWriter, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (this.compressed) {
            bufferedWriter.write(Eu4Utils.MAGIC_WORD);
            bufferedWriter.newLine();
            this.aiItem.write(bufferedWriter, 0, listeners);
        }
    }

    public void writeGamestate(BufferedWriter bufferedWriter, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (this.compressed) {
            bufferedWriter.write(Eu4Utils.MAGIC_WORD);
            bufferedWriter.newLine();
            this.gamestateItem.write(bufferedWriter, 0, listeners);
        }
    }

    public void writeMeta(BufferedWriter bufferedWriter, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (this.compressed) {
            bufferedWriter.write(Eu4Utils.MAGIC_WORD);
            bufferedWriter.newLine();
            this.metaItem.write(bufferedWriter, 0, listeners);
        }
    }

    public void writeAll(BufferedWriter bufferedWriter, Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        if (!this.compressed) {
            bufferedWriter.write(Eu4Utils.MAGIC_WORD);
            bufferedWriter.newLine();
            this.gamestateItem.write(bufferedWriter, 0, listeners);
        }
    }

    private void computeAdvisors() {
        for (SaveProvince province : getProvinces().values()) {
            if (province.getHistory() != null) {
                for (SaveProvinceHistoryEvent historyEvent : province.getHistory().getEvents()) {
                    SaveAdvisor advisor = historyEvent.getAdvisor();

                    if (advisor != null) {
                        Country owner = province.getHistoryItemAt(historyEvent.getDate()).getOwner();

                        if (owner != null) {
                            getCountry(owner.getTag()).addAdvisor(advisor);
                        }
                    }
                }
            }
        }
    }
}
