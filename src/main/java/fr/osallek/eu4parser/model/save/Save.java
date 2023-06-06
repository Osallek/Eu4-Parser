package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.LauncherSettings;
import fr.osallek.eu4parser.model.game.Age;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Hegemon;
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

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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

    public Save(String name, Path gameFolderPath, ClausewitzItem item, LauncherSettings launcherSettings) throws IOException {
        this(name, gameFolderPath, item, item, item, false, launcherSettings);
    }

    public Save(String name, Path gameFolderPath, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem,
                LauncherSettings launcherSettings) throws IOException {
        this(name, gameFolderPath, gamestateItem, aiItem, metaItem, true, launcherSettings);
    }

    private Save(String name, Path gameFolderPath, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem, boolean compressed,
                 LauncherSettings launcherSettings) throws IOException {
        this.name = name;
        this.gamestateItem = gamestateItem;
        this.aiItem = aiItem;
        this.metaItem = metaItem;
        this.compressed = compressed;
        this.game = new Game(gameFolderPath, launcherSettings, getModEnabled());
        refreshAttributes();
    }

    public Save(String name, ClausewitzItem item, Game game) throws IOException {
        this(name, item, item, item, false, game);
    }

    public Save(String name, ClausewitzItem gamestateItem, ClausewitzItem aiItem, ClausewitzItem metaItem, boolean compressed, Game game) throws IOException {
        this.name = name;
        this.gamestateItem = gamestateItem;
        this.aiItem = aiItem;
        this.metaItem = metaItem;
        this.compressed = compressed;
        this.game = game;
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
        return this.metaItem.getVarAsDate("date").get();
    }

    public void setDate(LocalDate date) {
        this.metaItem.setVariable("date", date);
    }

    public Optional<String> getPlayer() {
        return this.metaItem.getVarAsString("player");
    }

    public void setPlayer(String country) {
        country = ClausewitzUtils.addQuotes(country);

        if (country.length() == 5) {
            this.metaItem.setVariable("player", country);
        }
    }

    public Optional<SaveCountry> getPlayedCountry() {
        return getPlayer().map(ClausewitzUtils::removeQuotes).map(this::getCountry);
    }

    public Optional<String> getDisplayedCountryName() {
        return this.metaItem.getVarAsString("displayed_country_name");
    }

    public List<String> getSavegameVersions() {
        return this.metaItem.getList("savegame_versions").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getDlcEnabled() {
        return this.metaItem.getList("dlc_enabled").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getModEnabled() {
        return this.metaItem.getChild("mods_enabled_names")
                            .map(ClausewitzItem::getChildren)
                            .stream()
                            .flatMap(Collection::stream)
                            .map(item -> item.getVarAsString("filename"))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
    }

    public boolean isMultiPlayer() {
        return this.metaItem.getVarAsBool("multi_player").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean isRandomNewWorld() {
        return this.metaItem.getVarAsBool("is_random_new_world").map(BooleanUtils::toBoolean).orElse(false);
    }

    public GameplayOptions getGameplayOptions() {
        return this.gameplayOptions;
    }

    public Optional<Integer> getSpeed() {
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

    public Optional<Integer> getUnitIdCounter() {
        return this.gamestateItem.getVarAsInt("unit");
    }

    public Optional<Age> getCurrentAge() {
        return this.gamestateItem.getVarAsString("current_age").map(ClausewitzUtils::removeQuotes).map(this.game::getAge);
    }

    public Optional<Double> getNextAgeProgress() {
        return this.gamestateItem.getVarAsDouble("next_age_progress");
    }

    /**
     * Used for units and armies
     */
    public int getAndIncrementUnitIdCounter() {
        Optional<Integer> unit = this.gamestateItem.getVarAsInt("unit");

        if (unit.isEmpty()) {
            this.gamestateItem.addVariable("unit", 2);

            return 1;
        } else {
            this.gamestateItem.setVariable("unit", unit.get() + 1);

            return unit.get();
        }
    }

    public ListOfDates getFlags() {
        return flags;
    }

    public Revolution getRevolution() {
        return revolution;
    }

    public Optional<LocalDate> getStartDate() {
        return this.gamestateItem.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.gamestateItem.setVariable("start_date", startDate);
    }

    public Map<String, SaveArea> getAreas() {
        return areas;
    }

    public Optional<Double> getTotalMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("total_military_power");
    }

    public Optional<Double> getAverageMilitaryPower() {
        return this.gamestateItem.getVarAsDouble("average_military_power");
    }

    public Institutions getInstitutions() {
        return institutions;
    }

    public List<SaveHegemon> getHegemons() {
        return hegemons;
    }

    public void setHegemon(Hegemon hegemon, SaveCountry country, double progress) {
        Optional<SaveHegemon> saveHegemon = getHegemons().stream().filter(h -> hegemon.equals(h.hegemon())).findFirst();

        if (saveHegemon.isPresent()) {
            saveHegemon.get().setCountry(country);
            saveHegemon.get().setProgress(progress);
        } else {
            SaveHegemon.addToItem(this.gamestateItem, hegemon.getName(), country, progress,
                                  this.gamestateItem.getList("institutions").map(ClausewitzList::getOrder).orElse(0) + 1);
            refreshAttributes();
        }
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
        this.gamestateItem.getList("production_leader_tag").ifPresent(list -> {
            //Start from 1 because first leader is for empty trade good
            for (int i = 1; i < list.size(); i++) {
                productionLeaders.put(this.game.getTradeGood(i - 1), getCountry(list.get(i).get()));
            }
        });

        return productionLeaders;
    }

    public Map<TradeGood, String> getGoodsTotalProduced() {
        Map<TradeGood, String> productionLeaders = new LinkedHashMap<>();
        this.gamestateItem.getList("tradegoods_total_produced").ifPresent(list -> {
            for (int i = 0; i < list.size(); i++) {
                productionLeaders.put(this.game.getTradeGood(i - 1), list.get(i).get());
            }
        });

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

    public Optional<Boolean> getHreLeaguesActive() {
        return this.gamestateItem.getVarAsInt("hre_leagues_status").map(integer -> integer == 1);
    }

    public void setHreLeaguesActive(boolean hreLeaguesActive) {
        this.gamestateItem.setVariable("hre_leagues_status", hreLeaguesActive ? 1 : 0);
    }

    public Optional<HreReligionStatus> getHreReligionStatus() {
        return this.gamestateItem.getVarAsInt("hre_religion_status").map(integer -> HreReligionStatus.values()[integer]);
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
                if (protestant != null && protestant.getEnable().isPresent()) {
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
                if (protestant != null && protestant.getEnable().isPresent()) {
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

    public List<SaveTeam> getTeams() {
        return this.gamestateItem.getChild("teams")
                                 .map(ClausewitzItem::getChildren)
                                 .stream()
                                 .flatMap(Collection::stream)
                                 .map(item -> new SaveTeam(item, this))
                                 .toList();
    }

    public void addTeam(String name, List<String> members) {
        ClausewitzItem teamsItem = this.gamestateItem.getChild("teams").orElseGet(() -> {
            ClausewitzItem i = new ClausewitzItem(this.gamestateItem, "teams",
                                                  this.gamestateItem.getChild("players_countries").map(ClausewitzItem::getOrder).orElse(1));
            this.gamestateItem.addChild(i, true);

            return i;
        });

        ClausewitzItem teamItem = teamsItem.addChild("team");
        SaveTeam team = new SaveTeam(teamItem, this);
        team.setName(name);
        members.forEach(team::addCountry);
    }

    public void removeTeam(String name) {
        this.gamestateItem.getChild("teams")
                          .ifPresent(item -> getTeams().stream()
                                                       .filter(saveTeam -> name.equals(saveTeam.name()))
                                                       .findFirst()
                                                       .ifPresent(saveTeam -> item.removeChild(saveTeam.item())));
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
        return this.gamestateItem.getVarAsBool("achievement_ok").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void addTradeCompany(String name, SaveProvince province) {
        ClausewitzItem tradeCompanyManagerItem = this.gamestateItem.getChild("trade_company_manager")
                                                                   .orElse(this.gamestateItem.addChild("trade_company_manager"));
        SaveTradeCompany.addToItem(this, tradeCompanyManagerItem, name, province);
        refreshAttributes();
    }

    public TechLevelDates getTechLevelDates() {
        return techLevelDates;
    }

    public ListOfDates getIdeaDates() {
        return ideaDates;
    }

    public Optional<String> getChecksum() {
        return this.gamestateItem.getVarAsString("checksum");
    }

    public SaveProvince getProvinceByColor(int red, int green, int blue) {
        Province province = this.game.getProvincesByColor().get(new Color(red, green, blue).getRGB());

        return province == null ? null : this.provinces.get(province.getId());
    }

    public List<SaveGreatProject> getGreatProjects() {
        return this.gamestateItem.getChild("great_projects")
                                 .map(ClausewitzItem::getChildren)
                                 .stream()
                                 .flatMap(Collection::stream)
                                 .map(item -> new SaveGreatProject(this, item))
                                 .toList();
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

    private void refreshAttributes() {
        this.gameplayOptions = this.gamestateItem.getChild("gameplaysettings")
                                                 .flatMap(item -> item.getList("setgameplayoptions"))
                                                 .filter(Predicate.not(ClausewitzList::isEmpty))
                                                 .map(GameplayOptions::new)
                                                 .orElse(null);
        this.flags = this.gamestateItem.getChild("flags").map(ListOfDates::new).orElse(null);
        this.revolution = this.gamestateItem.getChild("revolution").map(item -> new Revolution(item, this)).orElse(null);
        this.idCounters = this.gamestateItem.getList("id_counters").map(IdCounters::new).orElse(null);

        Optional<ClausewitzList> institutionOrigins = this.gamestateItem.getList("institution_origin");
        Optional<ClausewitzList> institutionAvailable = this.gamestateItem.getList("institutions");
        if (institutionOrigins.isPresent() && institutionAvailable.isPresent()) {
            this.institutions = new Institutions(institutionOrigins.get(), institutionAvailable.get(), this);
        }

        this.gamestateItem.getChild("trade").ifPresentOrElse(item -> {
            AtomicInteger i = new AtomicInteger(1);
            this.tradeNodes = item.getChildren("node")
                                  .stream()
                                  .map(clausewitzItem -> new SaveTradeNode(clausewitzItem, this, i.getAndIncrement()))
                                  .collect(Collectors.toMap(tradeNode -> ClausewitzUtils.removeQuotes(tradeNode.getName()), Function.identity()));
        }, () -> this.tradeNodes = null);

        this.changePrices = this.gamestateItem.getChild("change_price").map(item -> new ChangePrices(item, this.game)).orElse(null);
        this.ids = this.gamestateItem.getChildren("id").stream().map(Id::new).collect(Collectors.toMap(Id::getType, Function.identity()));

        List<ClausewitzItem> rebelFactionItems = this.gamestateItem.getChildren("rebel_faction");
        this.rebelFactions = rebelFactionItems.stream()
                                              .map(child -> new RebelFaction(child, this))
                                              .collect(Collectors.toMap(RebelFaction::getId, Function.identity(), (a, b) -> b));
        this.hre = this.gamestateItem.getChild("empire").map(item -> new Hre(item, this)).orElse(null);
        this.celestialEmpire = this.gamestateItem.getChild("celestial_empire").map(item -> new CelestialEmpire(item, this)).orElse(null);

        Optional<ClausewitzItem> religionsItem = this.gamestateItem.getChild("religions");
        Optional<ClausewitzItem> religionInstantDateItem = this.gamestateItem.getChild("religion_instance_data");

        if (religionsItem.isPresent() || religionInstantDateItem.isPresent()) {
            this.religions = new Religions(religionsItem.get(), religionInstantDateItem.get(), this);
        }

        this.firedEvents = this.gamestateItem.getChild("fired_events").map(item -> new FiredEvents(item, this.game)).orElse(null);
        this.pendingEvents = this.gamestateItem.getChild("pending_events").map(PendingEvents::new).orElse(null);

        this.gamestateItem.getChild("countries").ifPresent(item -> {
            this.countries = item.getChildren()
                                 .stream()
                                 .map(countryItem -> new SaveCountry(countryItem, this))
                                 .collect(Collectors.toMap(SaveCountry::getTag, Function.identity(), (x, y) -> y, LinkedHashMap::new));
            this.playableCountries = this.countries.values().stream().filter(SaveCountry::isPlayable).toList();
        });

        this.gamestateItem.getList("players_countries").ifPresent(list -> {
            for (int i = list.getValues().size() - 1; i > 0; i -= 2) {
                if (this.countries.containsKey(ClausewitzUtils.removeQuotes(list.get(i).get()))) {
                    this.getCountry(ClausewitzUtils.removeQuotes(list.get(i).get())).addPlayer(list.get(i - 1).get());
                }
            }
        });

        this.hegemons = this.game.getHegemons().stream().flatMap(hegemon -> this.gamestateItem.getChild(hegemon.getName()).map(item -> {
            SaveHegemon saveHegemon = new SaveHegemon(item, this, hegemon);
            saveHegemon.getCountry().setHegemon(saveHegemon);
            return Stream.of(saveHegemon);
        }).orElseGet(Stream::empty)).toList();

        List<ClausewitzItem> tradeLeaguesItems = this.gamestateItem.getChildren("trade_league");
        this.tradeLeagues = tradeLeaguesItems.stream().map(child -> new TradeLeague(child, this)).toList();
        this.tradeLeagues.forEach(tradeLeague -> tradeLeague.getMembers().forEach(member -> member.setTradeLeague(tradeLeague)));

        this.gamestateItem.getChild("great_powers").ifPresent(item -> {
            item.getChildren("original").forEach(child -> {
                SaveCountry country = this.getCountry(ClausewitzUtils.removeQuotes(child.getVarAsString("country").get()));
                getInternalGreatPowers().put(child.getVarAsInt("rank").get(), country);
            });

            this.greatPowers.forEach((key, value) -> value.setGreatPowerRank(key));
        });

        this.gamestateItem.getChild("provinces").ifPresent(item -> {
            this.provinces = new HashMap<>();
            item.getChildren().forEach(provinceItem -> {
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
        });

        this.gamestateItem.getChild("map_area_data").ifPresent(item -> {
            this.areas = item.getChildren()
                             .stream()
                             .filter(child -> child.getChild("state").isPresent() || child.getChild("investments").isPresent())
                             .map(child -> new SaveArea(child, this))
                             .collect(Collectors.toMap(area -> ClausewitzUtils.removeQuotes(area.getName()), Function.identity()));
            this.areas.values()
                      .forEach(saveArea -> saveArea.getProvinces().stream().filter(Objects::nonNull).forEach(province -> province.setSaveArea(saveArea)));
        });

        this.gamestateItem.getChild("active_advisors").ifPresent(item -> item.getChildren().forEach(child -> {
            SaveCountry country = getCountry(ClausewitzUtils.removeQuotes(child.getName()));
            child.getChildren("advisor")
                 .stream()
                 .map(Id::new)
                 .forEach(id -> country.getActiveAdvisors().put(id.getId(), getInternalAdvisors().get(id.getId())));
        }));

        this.diplomacy = this.gamestateItem.getChild("diplomacy").map(item -> new Diplomacy(item, this)).orElse(null);
        this.combats = this.gamestateItem.getChild("combat").map(item -> new Combats(item, this)).orElse(null);
        this.activeWars = this.gamestateItem.getChildren("active_war").stream().map(item -> new ActiveWar(item, this)).toList();
        this.previousWars = this.gamestateItem.getChildren("previous_war").stream().map(item -> new PreviousWar(item, this)).toList();

        this.gamestateItem.getChild("income_statistics")
                          .ifPresent(item -> item.getChildren("ledger_data")
                                                 .forEach(child -> child.getChild("data")
                                                                        .ifPresent(dataItem -> getCountry(ClausewitzUtils.removeQuotes(
                                                                                child.getVarAsString("name").get())).putAllIncomeStatistics(
                                                                                dataItem.getVariables()
                                                                                        .stream()
                                                                                        .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()),
                                                                                                                  ClausewitzVariable::getAsInt))))));
        this.gamestateItem.getChild("nation_size_statistics")
                          .ifPresent(item -> item.getChildren("ledger_data")
                                                 .forEach(child -> child.getChild("data")
                                                                        .ifPresent(dataItem -> getCountry(ClausewitzUtils.removeQuotes(
                                                                                child.getVarAsString("name").get())).putAllNationSizeStatistics(
                                                                                dataItem.getVariables()
                                                                                        .stream()
                                                                                        .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()),
                                                                                                                  ClausewitzVariable::getAsInt))))));
        this.gamestateItem.getChild("score_statistics")
                          .ifPresent(item -> item.getChildren("ledger_data")
                                                 .forEach(child -> child.getChild("data")
                                                                        .ifPresent(dataItem -> getCountry(ClausewitzUtils.removeQuotes(
                                                                                child.getVarAsString("name").get())).putAllScoreStatistics(
                                                                                dataItem.getVariables()
                                                                                        .stream()
                                                                                        .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()),
                                                                                                                  ClausewitzVariable::getAsInt))))));
        this.gamestateItem.getChild("inflation_statistics")
                          .ifPresent(item -> item.getChildren("ledger_data")
                                                 .forEach(child -> child.getChild("data")
                                                                        .ifPresent(dataItem -> getCountry(ClausewitzUtils.removeQuotes(
                                                                                child.getVarAsString("name").get())).putAllInflationStatistics(
                                                                                dataItem.getVariables()
                                                                                        .stream()
                                                                                        .collect(Collectors.toMap(v -> Integer.valueOf(v.getName()),
                                                                                                                  ClausewitzVariable::getAsInt))))));

        this.gamestateItem.getChild("trade_company_manager").ifPresent(item -> item.getChildren("trade_company").forEach(child -> {
            SaveTradeCompany company = new SaveTradeCompany(child, this);
            company.getOwner().addTradeCompany(company);
        }));

        this.techLevelDates = this.gamestateItem.getChild("tech_level_dates").map(TechLevelDates::new).orElse(null);
        this.ideaDates = this.gamestateItem.getChild("idea_dates").map(ListOfDates::new).orElse(null);
    }
}
