package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.Eu4Parser;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.Age;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Province;
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
import fr.osallek.eu4parser.model.save.religion.Religions;
import fr.osallek.eu4parser.model.save.revolution.Revolution;
import fr.osallek.eu4parser.model.save.trade.SaveTradeNode;
import fr.osallek.eu4parser.model.save.war.ActiveWar;
import fr.osallek.eu4parser.model.save.war.PreviousWar;
import org.apache.commons.lang3.BooleanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Save {

    //Todo in countries:
    //Todo Teams

    private final String name;

    private final Game game;

    public final ClausewitzItem aiItem;

    public final ClausewitzItem gamestateItem;

    public final ClausewitzItem metaItem;

    private final boolean compressed;

    private GameplayOptions gameplayOptions;

    private IdCounters idCounters;

    private ListOfDates flags;

    private Revolution revolution;

    private Map<String, SaveArea> areas;

    private Institutions institutions;

    private List<SaveHegemon> hegemons;

    private Map<String, SaveTradeNode> tradeNodes;

    private ChangePrices changePrices;

    private Map<Integer, Id> ids;

    private Map<Id, RebelFaction> rebelFactions;

    private Hre hre;

    private CelestialEmpire celestialEmpire;

    private List<TradeLeague> tradeLeagues;

    private Religions religions;

    private FiredEvents firedEvents;

    private PendingEvents pendingEvents;

    private Map<Integer, SaveProvince> provinces;

    private List<SaveProvince> cities;

    private Map<String, SaveCountry> countries;

    private List<SaveCountry> playableCountries;

    private SortedMap<Integer, SaveCountry> greatPowers;

    private Map<Integer, SaveAdvisor> advisors;

    private Diplomacy diplomacy;

    private Combats combats;

    private List<ActiveWar> activeWars;

    private List<PreviousWar> previousWars;

    private TechLevelDates techLevelDates;

    private ListOfDates ideaDates;

    public Save(String name, String gameFolderPath, String modFolder, ClausewitzItem item) throws IOException {
        this(name, gameFolderPath, modFolder, item, item, item, false);
    }

    public Save(String name, String gameFolderPath, String modFolder, ClausewitzItem gamestateItem, ClausewitzItem aiItem,
                ClausewitzItem metaItem) throws IOException {
        this(name, gameFolderPath, modFolder, gamestateItem, aiItem, metaItem, true);
    }

    private Save(String name, String gameFolderPath, String modFolder, ClausewitzItem gamestateItem, ClausewitzItem aiItem,
                 ClausewitzItem metaItem, boolean compressed) throws IOException {
        this.name = name;
        this.gamestateItem = gamestateItem;
        this.aiItem = aiItem;
        this.metaItem = metaItem;
        this.compressed = compressed;
        this.game = Eu4Parser.parseGame(gameFolderPath, this.getModEnabled());
        refreshAttributes();
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
        return this.metaItem.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.metaItem.setVariable("date", date);
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
            return child.getChildren().stream().map(item -> item.getVarAsString("filename")).filter(Objects::nonNull).collect(Collectors.toList());
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
        return this.gameplayOptions;
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
        return idCounters;
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
        return flags;
    }

    public Revolution getRevolution() {
        return revolution;
    }

    public LocalDate getStartDate() {
        return this.gamestateItem.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.gamestateItem.setVariable("start_date", startDate);
    }

    public Map<String, SaveArea> getAreas() {
        return areas;
    }

    public Double getTotalMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("total_military_power");
    }

    public Double getAverageMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("average_military_power");
    }

    public Institutions getInstitutions() {
        return institutions;
    }

    public List<SaveHegemon> getHegemons() {
        return hegemons;
    }

    public Map<String, SaveTradeNode> getTradeNodes() {
        return tradeNodes;
    }

    public SaveTradeNode getTradeNode(String name) {
        return this.tradeNodes.get(name);
    }

    public SaveTradeNode getTradeNode(int index) {
        return this.tradeNodes.values().stream().filter(tradeNode -> tradeNode.getIndex() == index).findFirst().orElse(null);
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
        return changePrices;
    }

    public Map<Integer, Id> getIds() {
        return ids;
    }

    public Map<Id, RebelFaction> getRebelFactions() {
        return rebelFactions;
    }

    public Hre getHre() {
        return hre;
    }

    public CelestialEmpire getCelestialEmpire() {
        return celestialEmpire;
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
            if (this.religions != null) {
                SaveReligion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(false);
                    catholic.setHreReligion(true);
                }

                SaveReligion protestant = this.religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(true);
                    protestant.setHreReligion(false);
                }
            }
        } else if (hreReligionStatus == HreReligionStatus.PROTESTANT) {
            if (this.religions != null) {
                SaveReligion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(true);
                    catholic.setHreReligion(false);
                }

                SaveReligion protestant = this.religions.getReligion("protestant");
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
        TradeLeague.addToItem(this.gamestateItem, members);
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

    public SaveProvince getProvince(Integer id) {
        if (id == null) {
            return null;
        }

        return this.provinces.get(id);
    }

    public Map<Integer, SaveProvince> getProvinces() {
        return provinces;
    }

    public List<SaveProvince> getCities() {
        return cities;
    }

    public SaveCountry getCountry(String tag) {
        if (tag == null) {
            return null;
        }

        return this.countries.get(ClausewitzUtils.removeQuotes(tag));
    }

    public Map<String, SaveCountry> getCountries() {
        return countries;
    }

    public List<SaveCountry> getPlayableCountries() {
        return playableCountries;
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
                                         && BooleanUtils.toBoolean(country.isHuman()))
                      .forEach(country -> country.setPlayerAiPrefsCommand(startWars, keepAlliances, keepTreaties,
                                                                          quickPeace, moveTraders, takeDecisions,
                                                                          embraceInstitutions, developProvinces,
                                                                          disbandUnits, changeFleetMissions,
                                                                          sendMissionaries, convertCultures,
                                                                          promoteCultures, braindead, timeout));
    }

    private SortedMap<Integer, SaveCountry> getInternalGreatPowers() {
        if (this.greatPowers == null) {
            this.greatPowers = new TreeMap<>();
        }

        return this.greatPowers;
    }

    public SortedMap<Integer, SaveCountry> getGreatPowers() {
        return this.greatPowers == null ? new TreeMap<>() : this.greatPowers;
    }

    private Map<Integer, SaveAdvisor> getInternalAdvisors() {
        if (this.advisors == null) {
            this.advisors = new HashMap<>();
        }

        return this.advisors;
    }

    public Map<Integer, SaveAdvisor> getAdvisors() {
        return this.advisors == null ? new HashMap<>() : this.advisors;
    }

    public void putAllAdvisors(Map<Integer, SaveAdvisor> advisors) {
        getInternalAdvisors().putAll(advisors);
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
        return BooleanUtils.toBoolean(this.gamestateItem.getVarAsBool("achievement_ok"));
    }

    public void addTradeCompany(String name, String owner, Integer... provinces) {
        owner = ClausewitzUtils.addQuotes(owner);

        String finalOwner = owner;
        provinces = Arrays.stream(provinces)
                          .filter(province -> getProvince(province).getOwner().equals(finalOwner))
                          .toArray(Integer[]::new);

        if (provinces.length > 0) {
            ClausewitzItem tradeCompanyManagerItem = this.gamestateItem.getChild("trade_company_manager");

            if (tradeCompanyManagerItem == null) {
                tradeCompanyManagerItem = this.gamestateItem.addChild("trade_company_manager");
            }

            SaveTradeCompany.addToItem(tradeCompanyManagerItem, name, owner, provinces);
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
        return this.gamestateItem.getVarAsString("checksum");
    }

    public SaveProvince getProvinceByColor(int red, int green, int blue) {
        Province province = this.game.getProvinceByColor(red, green, blue);

        return province == null ? null : (SaveProvince) province;
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

    private void refreshAttributes() {
        ClausewitzItem gameplaySettings = this.gamestateItem.getChild("gameplaysettings");

        if (gameplaySettings != null) {
            ClausewitzList gameplayOptionsList = gameplaySettings.getList("setgameplayoptions");

            if (gameplayOptionsList != null && !gameplayOptionsList.isEmpty()) {
                this.gameplayOptions = new GameplayOptions(gameplayOptionsList);
            }
        }

        ClausewitzItem flagsItem = this.gamestateItem.getChild("flags");

        if (flagsItem != null) {
            this.flags = new ListOfDates(flagsItem);
        }

        ClausewitzItem revolutionItem = this.gamestateItem.getChild("revolution");

        if (revolutionItem != null) {
            this.revolution = new Revolution(revolutionItem, this);
        }

        ClausewitzList idCountersList = this.gamestateItem.getList("id_counters");

        if (idCountersList != null) {
            this.idCounters = new IdCounters(idCountersList);
        }

        ClausewitzList institutionOrigins = this.gamestateItem.getList("institution_origin");
        ClausewitzList institutionAvailable = this.gamestateItem.getList("institutions");

        if (institutionOrigins != null && institutionAvailable != null) {
            this.institutions = new Institutions(institutionOrigins, institutionAvailable, this);
        }

        ClausewitzItem tradeItem = this.gamestateItem.getChild("trade");

        if (tradeItem != null) {
            AtomicInteger i = new AtomicInteger(1);
            this.tradeNodes = tradeItem.getChildren("node")
                                       .stream()
                                       .map(item -> new SaveTradeNode(item, this, i.getAndIncrement()))
                                       .collect(Collectors.toMap(tradeNode -> ClausewitzUtils.removeQuotes(tradeNode.getName()),
                                                                 Function.identity()));
        }

        ClausewitzItem changePricesItem = this.gamestateItem.getChild("change_price");

        if (changePricesItem != null) {
            this.changePrices = new ChangePrices(changePricesItem, this.game);
        }

        List<ClausewitzItem> idsItems = this.gamestateItem.getChildren("id");
        this.ids = idsItems.stream().map(Id::new).collect(Collectors.toMap(Id::getType, Function.identity()));

        List<ClausewitzItem> rebelFactionItems = this.gamestateItem.getChildren("rebel_faction");
        this.rebelFactions = rebelFactionItems.stream()
                                              .map(child -> new RebelFaction(child, this))
                                              .collect(Collectors.toMap(RebelFaction::getId, Function.identity()));

        ClausewitzItem hreItem = this.gamestateItem.getChild("empire");

        if (hreItem != null) {
            this.hre = new Hre(hreItem, this);
        }

        ClausewitzItem celestialEmpireItem = this.gamestateItem.getChild("celestial_empire");

        if (celestialEmpireItem != null) {
            this.celestialEmpire = new CelestialEmpire(celestialEmpireItem, this);
        }

        ClausewitzItem religionsItem = this.gamestateItem.getChild("religions");
        ClausewitzItem religionInstantDateItem = this.gamestateItem.getChild("religion_instance_data");

        if (religionsItem != null || religionInstantDateItem != null) {
            this.religions = new Religions(religionsItem, religionInstantDateItem, this);
        }

        ClausewitzItem firedEventsItem = this.gamestateItem.getChild("fired_events");

        if (firedEventsItem != null) {
            this.firedEvents = new FiredEvents(firedEventsItem, this.game);
        }

        ClausewitzItem pendingEventsItem = this.gamestateItem.getChild("pending_events");

        if (pendingEventsItem != null) {
            this.pendingEvents = new PendingEvents(pendingEventsItem);
        }

        ClausewitzItem countriesItem = this.gamestateItem.getChild("countries");

        if (countriesItem != null) {
            this.countries = countriesItem.getChildren()
                                          .stream()
                                          .map(countryItem -> new SaveCountry(countryItem, this))
                                          .collect(Collectors.toMap(SaveCountry::getTag, Function.identity(), (x, y) -> y, LinkedHashMap::new));
            this.playableCountries = this.countries.values()
                                                   .stream()
                                                   .filter(SaveCountry::isPlayable)
                                                   .peek(country -> country.setLocalizedName(this.game.getLocalisation(country.getTag())))
                                                   .sorted(Comparator.comparing(SaveCountry::getLocalizedName, Eu4Utils.COLLATOR))
                                                   .collect(Collectors.toList());
        }

        ClausewitzList playersCountriesList = this.gamestateItem.getList("players_countries");

        if (playersCountriesList != null) {
            for (int i = playersCountriesList.getValues().size() - 1; i > 0; i -= 2) {
                if (this.countries.containsKey(ClausewitzUtils.removeQuotes(playersCountriesList.get(i)))) {
                    this.getCountry(ClausewitzUtils.removeQuotes(playersCountriesList.get(i))).addPlayer(playersCountriesList.get(i - 1));
                }
            }
        }

        this.hegemons = this.game.getHegemons().stream().flatMap(hegemon -> {
            ClausewitzItem child = this.gamestateItem.getChild(hegemon.getName());

            if (child != null) {
                SaveHegemon saveHegemon = new SaveHegemon(child, this, hegemon);
                saveHegemon.getCountry().setHegemon(saveHegemon);
                return Stream.of(saveHegemon);
            }

            return Stream.empty();
        }).collect(Collectors.toList());

        List<ClausewitzItem> tradeLeaguesItems = this.gamestateItem.getChildren("trade_league");
        this.tradeLeagues = tradeLeaguesItems.stream()
                                             .map(child -> new TradeLeague(child, this))
                                             .collect(Collectors.toList());
        this.tradeLeagues.forEach(tradeLeague -> tradeLeague.getMembers().forEach(member -> member.setTradeLeague(tradeLeague)));

        ClausewitzItem greatPowersItem = this.gamestateItem.getChild("great_powers");

        if (greatPowersItem != null) {
            greatPowersItem.getChildren("original").forEach(child -> {
                SaveCountry country = this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("country")));
                getInternalGreatPowers().put(child.getVarAsInt("rank"), country);
            });

            this.greatPowers.forEach((key, value) -> value.setGreatPowerRank(key));
        }

        ClausewitzItem provincesItems = this.gamestateItem.getChild("provinces");

        if (provincesItems != null) {
            this.provinces = new HashMap<>();
            provincesItems.getChildren()
                          .forEach(provinceItem -> {
                              Province province = this.game.getProvince(Math.abs(Integer.parseInt(provinceItem.getName())));
                              if (province != null) {
                                  SaveProvince saveProvince = new SaveProvince(provinceItem,
                                                                               province,
                                                                               this);
                                  this.provinces.put(saveProvince.getId(), saveProvince);
                                  this.game.getProvinces().compute(saveProvince.getId(), (integer, p) -> p = saveProvince);
                                  this.game.getProvincesByColor()
                                           .compute(Eu4Utils.rgbToColor(saveProvince.getRed(), saveProvince.getGreen(), saveProvince.getBlue()),
                                                    (color, p) -> p = saveProvince);
                              }
                          });
            this.cities = this.provinces.values()
                                        .stream()
                                        .filter(SaveProvince::isCity)
                                        .sorted((o1, o2) -> Eu4Utils.COLLATOR.compare(o1.getName(), o2.getName()))
                                        .collect(Collectors.toList());
        }

        ClausewitzItem mapAreaDataItem = this.gamestateItem.getChild("map_area_data");

        if (mapAreaDataItem != null) {
            this.areas = mapAreaDataItem.getChildren()
                                        .stream()
                                        .filter(child -> child.getChild("state") != null || child.getChild("investments") != null)
                                        .map(child -> new SaveArea(child, this))
                                        .collect(Collectors.toMap(area -> ClausewitzUtils.removeQuotes(area.getName()), Function.identity()));
            this.areas.values().forEach(saveArea -> saveArea.getProvinces().stream().filter(Objects::nonNull).forEach(province -> province.setSaveArea(saveArea)));
        }

        ClausewitzItem activeAdvisorsItem = this.gamestateItem.getChild("active_advisors");

        if (activeAdvisorsItem != null) {
            activeAdvisorsItem.getChildren().forEach(child -> {
                SaveCountry country = this.getCountry(ClausewitzUtils.removeQuotes(child.getName()));
                child.getChildren("advisor")
                     .stream()
                     .map(Id::new)
                     .forEach(id -> country.getActiveAdvisors().put(id.getId(), getInternalAdvisors().get(id.getId())));
            });
        }

        ClausewitzItem diplomacyItem = this.gamestateItem.getChild("diplomacy");

        if (diplomacyItem != null) {
            this.diplomacy = new Diplomacy(diplomacyItem, this);
        }

        ClausewitzItem combatItem = this.gamestateItem.getChild("combat");

        if (combatItem != null) {
            this.combats = new Combats(combatItem, this);
        }

        List<ClausewitzItem> activeWarsItems = this.gamestateItem.getChildren("active_war");
        this.activeWars = activeWarsItems.stream()
                                         .map(item -> new ActiveWar(item, this))
                                         .collect(Collectors.toList());

        List<ClausewitzItem> previousWarsItems = this.gamestateItem.getChildren("previous_war");
        this.previousWars = previousWarsItems.stream()
                                             .map(item -> new PreviousWar(item, this))
                                             .collect(Collectors.toList());

        ClausewitzItem incomeStatisticsItem = this.gamestateItem.getChild("income_statistics");

        if (incomeStatisticsItem != null) {
            incomeStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .putAllIncomeStatistics(dataItem.getVariables()
                                                        .stream()
                                                        .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem nationSizeStatisticsItem = this.gamestateItem.getChild("nation_size_statistics");

        if (nationSizeStatisticsItem != null) {
            nationSizeStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .putAllNationSizeStatistics(dataItem.getVariables()
                                                            .stream()
                                                            .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem scoreStatisticsItem = this.gamestateItem.getChild("score_statistics");

        if (scoreStatisticsItem != null) {
            scoreStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .putAllScoreStatistics(dataItem.getVariables()
                                                       .stream()
                                                       .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem inflationStatisticsItem = this.gamestateItem.getChild("inflation_statistics");

        if (inflationStatisticsItem != null) {
            inflationStatisticsItem.getChildren("ledger_data").forEach(child -> {
                ClausewitzItem dataItem = child.getChild("data");
                if (dataItem != null) {
                    this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("name")))
                        .putAllInflationStatistics(dataItem.getVariables()
                                                           .stream()
                                                           .collect(Collectors.toMap(var -> Integer.valueOf(var.getName()), ClausewitzVariable::getAsInt)));
                }
            });
        }

        ClausewitzItem tradeCompanyManagerItem = this.gamestateItem.getChild("trade_company_manager");

        if (tradeCompanyManagerItem != null) {
            tradeCompanyManagerItem.getChildren("trade_company").forEach(child -> {
                SaveTradeCompany company = new SaveTradeCompany(child, this);
                company.getOwner().addTradeCompany(company);
            });
        }

        ClausewitzItem techLevelDatesItem = this.gamestateItem.getChild("tech_level_dates");

        if (techLevelDatesItem != null) {
            this.techLevelDates = new TechLevelDates(techLevelDatesItem);
        }

        ClausewitzItem ideaDatesItem = this.gamestateItem.getChild("idea_dates");

        if (ideaDatesItem != null) {
            this.ideaDates = new ListOfDates(ideaDatesItem);
        }
    }
}
