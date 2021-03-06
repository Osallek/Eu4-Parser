package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.parser.ClausewitzParser;
import fr.osallek.clausewitzparser.parser.LuaParser;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ModNotFoundException;
import fr.osallek.eu4parser.common.ModifierScope;
import fr.osallek.eu4parser.common.ModifierType;
import fr.osallek.eu4parser.common.ModifiersUtils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.common.TreeNode;
import fr.osallek.eu4parser.model.Mod;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.localisation.Eu4Language;
import fr.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private static final PathMatcher TXT_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.txt");

    private final String gameFolderPath;

    private final String modFolderPath;

    private final String mapFolderPath;

    private final String commonFolderPath;

    private final String gfxFolderPath;

    private final String localisationFolderPath;

    private final String interfaceFolderPath;

    private final String missionsFolderPath;

    private final File provincesImage;

    private TreeNode<FileNode> filesNode;

    private Map<Integer, Province> provinces;

    private Map<Integer, Province> provincesByColor;

    private Map<String, Continent> continents;

    private Map<String, CultureGroup> cultureGroups;

    private Map<String, ReligionGroup> religionGroups;

    private Map<String, Institution> institutions;

    private Map<String, TradeGood> tradeGoods;

    private Map<String, TradeNode> tradeNodes;

    private Map<String, Building> buildings;

    private Map<String, String> localisations;

    private Map<String, SpriteType> spriteTypes;

    private Map<String, ImperialReform> imperialReforms;

    private Map<String, Decree> decrees;

    private Map<String, GoldenBull> goldenBulls;

    private Map<String, Event> events;

    private Map<String, Government> governments;

    private SortedMap<Integer, GovernmentRank> governmentRanks;

    private Map<String, GovernmentName> governmentNames;

    private Map<String, GovernmentReform> governmentReforms;

    private Map<String, Unit> units;

    private Map<String, Area> areas;

    private Map<String, Advisor> advisors;

    private Map<String, IdeaGroup> ideaGroups;

    private Map<String, CasusBelli> casusBelli;

    private Map<String, ColonialRegion> colonialRegions;

    private Map<String, TradeCompany> tradeCompanies;

    private Map<String, Region> regions;

    private Map<String, SuperRegion> superRegions;

    private Map<String, TechGroup> techGroups;

    private Map<String, SubjectType> subjectTypes;

    private Map<String, FetishistCult> fetishistCults;

    private Map<String, ChurchAspect> churchAspects;

    private Map<String, MissionTree> missionTrees;

    private Map<String, EstatePrivilege> estatePrivileges;

    private Map<String, Estate> estates;

    private Map<Power, TreeSet<Technology>> technologies;

    private SortedSet<ProfessionalismModifier> professionalismModifiers;

    private Map<String, Object> defines;

    private Map<String, RulerPersonality> rulerPersonalities;

    private Map<String, LeaderPersonality> leaderPersonalities;

    private Map<String, Investment> investments;

    private Map<String, Policy> policies;

    private Map<String, Hegemon> hegemons;

    private Map<String, Faction> factions;

    private Map<String, Age> ages;

    private SortedMap<String, DefenderOfFaith> defenderOfFaith;

    private SortedMap<String, CenterOfTrade> centersOfTrade;

    private Map<String, Fervor> fervors;

    private Map<String, GreatProject> greatProjects;

    private Map<String, HolyOrder> holyOrders;

    private SortedMap<String, Isolationism> isolationisms;

    private Map<String, NativeAdvancements> nativeAdvancements;

    private Map<String, NavalDoctrine> navalDoctrines;

    private Map<String, ParliamentIssue> parliamentIssues;

    private Map<String, PersonalDeity> personalDeities;

    private Map<String, ReligiousReforms> religiousReforms;

    private Map<String, CrownLandBonus> crownLandBonuses;

    private Map<String, StateEdict> stateEdicts;

    private Map<String, TradePolicy> tradePolicies;

    private Map<String, StaticModifier> staticModifiers;

    private Map<String, EventModifier> eventModifiers;

    private Map<String, TriggeredModifier> provinceTriggeredModifiers;

    private Map<String, TriggeredModifier> triggeredModifiers;

    private Map<String, String> countryTags;

    public Game(String gameFolderPath, String modFolderPath, List<String> modEnabled) throws IOException {
        this.gameFolderPath = gameFolderPath;
        this.modFolderPath = modFolderPath;

        Instant start = Instant.now();

        readMods(modEnabled);

        this.mapFolderPath = "map";
        this.commonFolderPath = "common";
        this.gfxFolderPath = "gfx";
        this.localisationFolderPath = "localisation";
        this.interfaceFolderPath = "interface";
        this.missionsFolderPath = "missions";
        this.provincesImage = getAbsoluteFile(this.mapFolderPath + File.separator + "provinces.bmp");

        loadDefines();
        loadLocalisations();
        readSpriteTypes();
        readEstates();
        readFactions();
        readProvinces();
        readAreas();
        readRegions();
        readSuperRegions();
        readCultures();
        readReligion();
        readInstitutions();
        readTradeGoods();
        readTradeNodes();
        readBuildings();
        readImperialReforms();
        readDecrees();
        readGoldenBulls();
        readEvents();
        readGovernmentRanks();
        readGovernmentNames();
        readGovernmentReforms();
        readGovernments();
        readUnits();
        readTechGroups();
        readAdvisors();
        readIdeaGroups();
        readCasusBelli();
        readTradeCompanies();
        readSubjectTypes();
        readFetishistCults();
        readChurchAspects();
        readMissionTrees();
        readTechnologies();
        readRulerPersonalities();
        readLeaderPersonalities();
        readProfessionalismModifiers();
        readStaticModifiers();
        readInvestments();
        readPolicies();
        readHegemons();
        readAges();
        readDefenderOfFaith();
        readCentersOfTrade();
        readFervors();
        readGreatProjects();
        readHolyOrders();
        readIsolationism();
        readNativeAdvancements();
        readNavalDoctrine();
        readParliamentIssue();
        readPersonalDeities();
        readReligiousReforms();
        readCrownLandBonuses();
        readStateEdicts();
        readTradePolicies();
        readEventModifiers();
        readProvinceTriggeredModifiers();
        readTriggeredModifiers();
        readCountryTags();
        readColonialRegions();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time to read game data: {}ms !", Duration.between(start, Instant.now()).toMillis());
        }
    }

    private TreeNode<FileNode> getTreeNode(String relativePath) {
        return this.filesNode.getRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath().toString()));
    }

    private FileNode getFileNode(String relativePath) {
        return this.filesNode.getDataRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath().toString()));
    }

    @SafeVarargs
    private Stream<FileNode> getFileNodes(String relativePath, Predicate<FileNode>... predicates) {
        TreeNode<FileNode> treeNode = getTreeNode(relativePath);
        return treeNode == null ? Stream.empty() : treeNode.getLeaves(predicates).stream().map(TreeNode::getData);
    }

    @SafeVarargs
    private Stream<Path> getPaths(String relativePath, Predicate<FileNode>... predicates) {
        Stream<FileNode> treeNode = getFileNodes(relativePath, predicates);
        return treeNode == null ? Stream.empty() : treeNode.map(FileNode::getPath);
    }

    private Path getAbsolutePath(String relativePath) {
        FileNode node = getFileNode(relativePath);
        return node == null ? null : node.getPath();
    }

    private File getAbsoluteFile(String relativePath) {
        Path path = getAbsolutePath(relativePath);
        return path == null ? null : path.toFile();
    }

    public String getGameFolderPath() {
        return gameFolderPath;
    }

    public File getProvincesImage() {
        return this.provincesImage;
    }

    public File getNormalCursorImage() {
        return getAbsoluteFile(this.gfxFolderPath + File.separator + "cursors" + File.separator + "normal.png");
    }

    public File getSelectedCursorImage() {
        return getAbsoluteFile(this.gfxFolderPath + File.separator + "cursors" + File.separator + "selected.png");
    }

    public File getGoldImage() {
        return getSpriteTypeImageFile("GFX_icon_gold");
    }

    public File getCountryFlagImage(Country country) {
        return country == null ? null : getCountryFlagImage(country.getTag());
    }

    public File getCountryFlagImage(String country) {
        return country == null ? null : getAbsoluteFile(this.gfxFolderPath + File.separator + "flags" + File.separator + country + ".tga");
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    public Province getProvince(int i) {
        return this.provinces.get(i);
    }

    public Map<Integer, Province> getProvincesByColor() {
        return provincesByColor;
    }

    public Province getProvinceByColor(int red, int green, int blue) {
        return this.provincesByColor.get(Eu4Utils.rgbToColor(red, green, blue));
    }

    public List<Continent> getContinents() {
        return this.continents.values()
                              .stream()
                              .sorted(Comparator.comparing(Continent::getLocalizedName, Eu4Utils.COLLATOR))
                              .collect(Collectors.toList());
    }

    public Continent getContinent(String name) {
        return this.continents.get(name);
    }

    public Continent getContinent(int i) {
        return new ArrayList<>(this.continents.values()).get(i);
    }

    public String getLocalisation(String key) {
        return this.localisations.getOrDefault(key, key);
    }

    public String getLocalisationClean(String key) {
        if (key == null) {
            return null;
        }

        String localisationString = getLocalisation(key);

        if (localisationString == null) {
            return key;
        }

        StringBuilder localisation = new StringBuilder(localisationString);

        if (localisation.length() == 0) {
            return key;
        }

        int indexOf;
        while (localisation.toString().indexOf('§') >= 0) {
            for (int i = 0; i < localisation.length(); i++) {
                if (localisation.charAt(i) == '§') {
                    localisation.deleteCharAt(i);//Remove char
                    localisation.deleteCharAt(i);//Remove color code
                    indexOf = localisation.indexOf("§", i);
                    localisation.deleteCharAt(indexOf);//Remove closing char
                    localisation.deleteCharAt(indexOf);//Remove closing code
                    break;
                }
            }
        }

        if ((indexOf = localisation.toString().indexOf('$')) >= 0) {
            if (ClausewitzUtils.hasAtLeast(localisation.toString(), '$', 2)) {
                String[] splits = localisation.toString().split("\\$");
                localisation = new StringBuilder();
                for (int i = 0; i < splits.length; i += 2) {
                    localisation.append(splits[i]).append(" ");
                }
            } else {
                localisation = new StringBuilder(localisation.substring(0, indexOf));
            }
        }

        return localisation.toString().replace("\\r\\n", "")
                           .replace("\\n", " ")
                           .replaceAll("[^'.\\p{L}\\p{M}\\p{Alnum}\\p{Space}]", "")
                           .trim();
    }

    public String getLocalisationCleanNoPunctuation(String key) {
        return getLocalisationClean(key).replaceAll("[\\p{P}]", "").trim();
    }

    public SpriteType getSpriteType(String key) {
        if (key == null) {
            return null;
        }

        return this.spriteTypes.get(key);
    }

    public File getSpriteTypeImageFile(String key) {
        SpriteType spriteType = getSpriteType(key);

        if (spriteType == null) {
            return null;
        }

        String relativePath = Path.of(ClausewitzUtils.removeQuotes(spriteType.getTextureFile())).toString();
        File file = getAbsoluteFile(relativePath);

        if (file != null && file.exists()) {
            return file;
        }

        //Fix some time files are not rightly registered (I don't know how the game loads them...)
        if (relativePath.endsWith(".tga")) {
            return getAbsoluteFile(relativePath.replace(".tga", ".dds"));
        } else if (relativePath.endsWith(".dds")) {
            return getAbsoluteFile(relativePath.replace(".dds", ".tga"));
        }

        return null;
    }

    public Collection<CultureGroup> getCultureGroups() {
        return this.cultureGroups.values();
    }

    public List<Culture> getCultures() {
        return this.cultureGroups.values()
                                 .stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .sorted(Comparator.comparing(Culture::getLocalizedName, Eu4Utils.COLLATOR))
                                 .collect(Collectors.toList());
    }

    public Culture getCulture(String name) {
        if (name == null) {
            return null;
        }

        return this.cultureGroups.values()
                                 .stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .filter(culture -> culture.getName().equalsIgnoreCase(name))
                                 .findFirst()
                                 .orElse(null);
    }

    public Collection<ReligionGroup> getReligionGroups() {
        return this.religionGroups.values();
    }

    public List<Religion> getReligions() {
        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .sorted(Comparator.comparing(Religion::getLocalizedName, Eu4Utils.COLLATOR))
                                  .collect(Collectors.toList());
    }

    public List<Religion> getReligionsNotOrdered() {
        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .collect(Collectors.toList());
    }

    public Religion getReligion(String name) {
        if (name == null) {
            return null;
        }

        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .filter(religion -> religion.getName().equalsIgnoreCase(name))
                                  .findFirst()
                                  .orElse(null);
    }

    public List<Institution> getInstitutions() {
        return this.institutions.values()
                                .stream()
                                .sorted(Comparator.comparingInt(Institution::getIndex))
                                .collect(Collectors.toList());
    }

    public Institution getInstitution(int i) {
        return getInstitutions().get(i);
    }

    public Institution getInstitution(String name) {
        return this.institutions.get(name);
    }

    public List<TradeGood> getTradeGoods() {
        return this.tradeGoods.values()
                              .stream()
                              .sorted(Comparator.comparing(TradeGood::getLocalizedName, Eu4Utils.COLLATOR))
                              .collect(Collectors.toList());
    }

    public TradeGood getTradeGood(String name) {
        return this.tradeGoods.get(name);
    }

    public TradeGood getTradeGood(int i) {
        return new ArrayList<>(this.tradeGoods.values()).get(i);
    }

    public List<TradeNode> getTradeNodes() {
        return this.tradeNodes.values()
                              .stream()
                              .sorted(Comparator.comparing(TradeNode::getLocalizedName, Eu4Utils.COLLATOR))
                              .collect(Collectors.toList());
    }

    public TradeNode getTradeNode(String name) {
        return this.tradeNodes.get(name);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(this.buildings.values());
    }

    public Building getBuilding(String name) {
        return this.buildings.get(name);
    }

    public List<ImperialReform> getImperialReforms() {
        return new ArrayList<>(this.imperialReforms.values());
    }

    public ImperialReform getImperialReform(String name) {
        return this.imperialReforms.get(name);
    }

    public List<Decree> getDecrees() {
        return new ArrayList<>(this.decrees.values());
    }

    public Decree getDecree(String name) {
        return this.decrees.get(name);
    }

    public List<GoldenBull> getGoldenBulls() {
        return new ArrayList<>(this.goldenBulls.values());
    }

    public GoldenBull getGoldenBull(String name) {
        return this.goldenBulls.get(name);
    }

    public List<Event> getEvents() {
        return this.events.values()
                          .stream()
                          .sorted(Comparator.comparing(Event::getLocalizedName, Eu4Utils.COLLATOR))
                          .collect(Collectors.toList());
    }

    public List<Event> getFireOnlyOnceEvents() {
        return this.events.values()
                          .stream()
                          .filter(Event::fireOnlyOnce)
                          .sorted(Comparator.comparing(Event::getLocalizedName, Eu4Utils.COLLATOR))
                          .collect(Collectors.toList());
    }

    public Event getEvent(String id) {
        return this.events.get(id);
    }

    private int getDefinesInt(String category, String key) {
        return (int) ((Map<String, Object>) this.defines.get(category)).get(key);
    }

    private double getDefinesDouble(String category, String key) {
        return (double) ((Map<String, Object>) this.defines.get(category)).get(key);
    }

    private LocalDate getDefinesLocalDate(String category, String key) {
        return LocalDate.parse(((String) ((Map<String, Object>) this.defines.get(category)).get(key)), ClausewitzUtils.DATE_FORMAT);
    }

    public int getMaxGovRank() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_GOV_RANK");
    }

    public int getMaxAspects() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_UNLOCKED_ASPECTS");
    }

    public int getGoldenEraDuration() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "GOLDEN_ERA_YEARS");
    }

    public int getBankruptcyDuration() {
        return getDefinesInt(Eu4Utils.DEFINE_ECONOMY_KEY, "BANKRUPTCY_DURATION");
    }

    public int getNbGreatPowers() {
        return getDefinesInt(Eu4Utils.DEFINE_DIPLOMACY_KEY, "NUM_OF_GREAT_POWERS");
    }

    public int getNomadDevelopmentScale() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "NOMAD_DEVELOPMENT_SCALE");
    }

    public int getLargeColonialNationLimit() {
        return getDefinesInt(Eu4Utils.DEFINE_ECONOMY_KEY, "LARGE_COLONIAL_NATION_LIMIT");
    }

    public int getFortPerDevRatio() {
        return getDefinesInt(Eu4Utils.DEFINE_MILITARY_KEY, "FORT_PER_DEV_RATIO");
    }

    public double getMaxArmyProfessionalism() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_ARMY_PROFESSIONALISM");
    }

    public double getLowArmyProfessionalismMinRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "LOW_ARMY_PROFESSIONALISM_MIN_RANGE");
    }

    public double getLowArmyProfessionalismMaxRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "LOW_ARMY_PROFESSIONALISM_MAX_RANGE");
    }

    public double getHighArmyProfessionalismMinRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "HIGH_ARMY_PROFESSIONALISM_MIN_RANGE");
    }

    public double getHighArmyProfessionalismMaxRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "HIGH_ARMY_PROFESSIONALISM_MAX_RANGE");
    }

    public LocalDate getStartDate() {
        return getDefinesLocalDate(Eu4Utils.DEFINE_GAME_KEY, "START_DATE");
    }

    public LocalDate getEndDate() {
        return getDefinesLocalDate(Eu4Utils.DEFINE_GAME_KEY, "END_DATE");
    }

    public int getEstateAngryThreshold() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_ANGRY_THRESHOLD");
    }

    public int getEstateHappyThreshold() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_HAPPY_THRESHOLD");
    }

    public int getEstateInfluenceLevel1() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_1");
    }

    public int getEstateInfluenceLevel2() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_2");
    }

    public int getEstateInfluenceLevel3() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_3");
    }

    public double getEstateInfluencePerDev() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_PER_DEV");
    }

    public double getEstateMaxInfluenceFromDev() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_MAX_INFLUENCE_FROM_DEV");
    }

    public double getIdeaToTech() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "IDEA_TO_TECH");
    }

    public double getNeighbourBonus() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "NEIGHBOURBONUS");
    }

    public double getNeighbourBonusCap() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "NEIGHBOURBONUS_CAP");
    }

    public double getSpyNetworkTechEffect() {
        return getDefinesDouble(Eu4Utils.DEFINE_DIPLOMACY_KEY, "SPY_NETWORK_TECH_EFFECT");
    }

    public double getSpyNetworkTechEffectMax() {
        return getDefinesDouble(Eu4Utils.DEFINE_DIPLOMACY_KEY, "SPY_NETWORK_TECH_EFFECT_MAX");
    }

    public int getEstatePrivilegesMaxConcurrent() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_PRIVILEGES_MAX_CONCURRENT");
    }

    public double getInnovativenessMax() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "INNOVATIVENESS_MAX");
    }

    public int getMonarchMaxSkill() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MONARCH_MAX_SKILL");
    }

    public int getMonarchMinSkill() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MONARCH_MIN_SKILL");
    }

    public int getMaxExtraPersonalities() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_EXTRA_PERSONALITIES");
    }

    public int getAgeOfAdulthood() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "AGE_OF_ADULTHOOD");
    }

    public int getNumPossibleRivals() {
        return getDefinesInt(Eu4Utils.DEFINE_DIPLOMACY_KEY, "NUM_POSSIBLE_RIVALS");
    }

    public int getMaxChristianReligiousCenters() {
        return getDefinesInt(Eu4Utils.DEFINE_RELIGION_KEY, "MAX_CHRISTIAN_RELIGIOUS_CENTERS");
    }

    public int getMaxActivePolicies() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_ACTIVE_POLICIES");
    }

    public int getBasePossiblePolicies() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "BASE_POSSIBLE_POLICIES");
    }

    public int getFreeIdeaGroupCost() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "FREE_IDEA_GROUP_COST");
    }

    public List<Government> getGovernments() {
        return this.governments.values()
                               .stream()
                               .sorted(Comparator.comparing(Government::getLocalizedName, Eu4Utils.COLLATOR))
                               .collect(Collectors.toList());
    }

    public Government getGovernment(String name) {
        return this.governments.get(name);
    }

    public Collection<GovernmentRank> getGovernmentRanks() {
        return this.governmentRanks.values();
    }

    public GovernmentRank getGovernmentRank(int level) {
        return this.governmentRanks.get(level);
    }

    public Collection<GovernmentName> getGovernmentNames() {
        return this.governmentNames.values();
    }

    public GovernmentName getGovernmentName(String name) {
        return this.governmentNames.get(name);
    }

    public Collection<GovernmentReform> getGovernmentReforms() {
        return this.governmentReforms.values();
    }

    public GovernmentReform getGovernmentReform(String name) {
        return this.governmentReforms.get(name);
    }

    public Collection<Unit> getUnits() {
        return this.units.values();
    }

    public Unit getUnit(String name) {
        return this.units.get(name);
    }

    public Collection<Area> getAreas() {
        return this.areas.values();
    }

    public Area getArea(String name) {
        return this.areas.get(name);
    }

    public Collection<Region> getRegions() {
        return this.regions.values();
    }

    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    public Collection<SuperRegion> getSuperRegions() {
        return this.superRegions.values();
    }

    public SuperRegion getSuperRegion(String name) {
        return this.superRegions.get(name);
    }

    public Collection<TechGroup> getTechGroups() {
        return this.techGroups.values();
    }

    public TechGroup getTechGroup(String name) {
        return this.techGroups.get(name);
    }

    public List<Advisor> getAdvisors() {
        return new ArrayList<>(this.advisors.values());
    }

    public Advisor getAdvisor(String name) {
        return this.advisors.get(name);
    }

    public List<IdeaGroup> getIdeaGroups() {
        return new ArrayList<>(this.ideaGroups.values());
    }

    public IdeaGroup getIdeaGroup(String name) {
        return this.ideaGroups.get(name);
    }

    public List<CasusBelli> getCasusBelli() {
        return new ArrayList<>(this.casusBelli.values());
    }

    public CasusBelli getCasusBelli(String name) {
        return this.casusBelli.get(name);
    }

    public List<ColonialRegion> getColonialRegions() {
        return new ArrayList<>(this.colonialRegions.values());
    }

    public ColonialRegion getColonialRegion(String name) {
        return this.colonialRegions.get(name);
    }

    public List<TradeCompany> getTradeCompanies() {
        return new ArrayList<>(this.tradeCompanies.values());
    }

    public TradeCompany getTradeCompany(String name) {
        return this.tradeCompanies.get(name);
    }

    public List<SubjectType> getSubjectTypes() {
        return new ArrayList<>(this.subjectTypes.values());
    }

    public SubjectType getSubjectType(String name) {
        return this.subjectTypes.get(name);
    }

    public List<FetishistCult> getFetishistCults() {
        return new ArrayList<>(this.fetishistCults.values());
    }

    public FetishistCult getFetishistCult(String name) {
        return this.fetishistCults.get(name);
    }

    public List<ChurchAspect> getChurchAspects() {
        return new ArrayList<>(this.churchAspects.values());
    }

    public ChurchAspect getChurchAspect(String name) {
        return this.churchAspects.get(name);
    }

    public List<MissionTree> getMissionTrees() {
        return new ArrayList<>(this.missionTrees.values());
    }

    public MissionTree getMissionTree(String name) {
        return this.missionTrees.get(name);
    }

    public Mission getMission(String name) {
        if (name == null) {
            return null;
        }

        for (MissionTree missionTree : this.missionTrees.values()) {
            if (missionTree.getMissions().containsKey(name)) {
                return missionTree.getMissions().get(name);
            }
        }

        return null;
    }

    public List<EstatePrivilege> getEstatePrivileges() {
        return new ArrayList<>(this.estatePrivileges.values());
    }

    public EstatePrivilege getEstatePrivilege(String name) {
        return this.estatePrivileges.get(name);
    }

    public List<Estate> getEstates() {
        return new ArrayList<>(this.estates.values());
    }

    public Estate getEstate(String name) {
        return this.estates.get(name);
    }

    public SortedSet<Technology> getTechnologies(Power power) {
        return this.technologies.get(power);
    }

    public Technology getTechnology(Power power, int i) {
        return new ArrayList<>(this.technologies.get(power)).get(i);
    }

    public SortedSet<ProfessionalismModifier> getProfessionalismModifiers() {
        return this.professionalismModifiers;
    }

    public List<RulerPersonality> getRulerPersonalities() {
        return new ArrayList<>(this.rulerPersonalities.values());
    }

    public RulerPersonality getRulerPersonality(String name) {
        return this.rulerPersonalities.get(name);
    }

    public List<LeaderPersonality> getLeaderPersonalities() {
        return new ArrayList<>(this.leaderPersonalities.values());
    }

    public LeaderPersonality getLeaderPersonality(String name) {
        return this.leaderPersonalities.get(name);
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(this.investments.values());
    }

    public Investment getInvestment(String name) {
        return this.investments.get(name);
    }

    public List<Policy> getPolicies() {
        return new ArrayList<>(this.policies.values());
    }

    public Policy getPolicy(String name) {
        return this.policies.get(name);
    }

    public List<Hegemon> getHegemons() {
        return new ArrayList<>(this.hegemons.values());
    }

    public Hegemon getHegemon(String name) {
        return this.hegemons.get(name);
    }

    public List<Faction> getFactions() {
        return new ArrayList<>(this.factions.values());
    }

    public Faction getFaction(String name) {
        return this.factions.get(name);
    }

    public List<Age> getAges() {
        return new ArrayList<>(this.ages.values());
    }

    public Age getAge(String name) {
        return this.ages.get(name);
    }

    public AgeAbility getAgeAbility(String name) {
        if (name == null) {
            return null;
        }

        return this.ages.values()
                        .stream()
                        .map(Age::getAbilities)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(ageAbility -> ageAbility.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public AgeObjective getAgeObjective(String name) {
        if (name == null) {
            return null;
        }

        return this.ages.values()
                        .stream()
                        .map(Age::getObjectives)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(ageObjective -> ageObjective.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public Collection<DefenderOfFaith> getDefenderOfFaith() {
        return this.defenderOfFaith.values();
    }

    public Collection<CenterOfTrade> getCentersOfTrade() {
        return this.centersOfTrade.values();
    }

    public List<Fervor> getFervors() {
        return new ArrayList<>(this.fervors.values());
    }

    public Fervor getFervor(String name) {
        return this.fervors.get(name);
    }

    public List<GreatProject> getGreatProjects() {
        return new ArrayList<>(this.greatProjects.values());
    }

    public GreatProject getGreatProject(String name) {
        return this.greatProjects.get(name);
    }

    public List<HolyOrder> getHolyOrders() {
        return new ArrayList<>(this.holyOrders.values());
    }

    public HolyOrder getHolyOrder(String name) {
        return this.holyOrders.get(name);
    }

    public List<Isolationism> getIsolationisms() {
        return new ArrayList<>(this.isolationisms.values());
    }

    public Isolationism getIsolationism(Integer level) {
        if (level == null) {
            return null;
        }

        for (Isolationism isolationism : this.isolationisms.values()) {
            if (isolationism.getIsolationValue() == level) {
                return isolationism;
            }
        }

        return null;
    }

    public List<NativeAdvancements> getNativeAdvancements() {
        return new ArrayList<>(this.nativeAdvancements.values());
    }

    public NativeAdvancements getNativeAdvancements(String name) {
        return this.nativeAdvancements.get(name);
    }

    public List<NavalDoctrine> getNavalDoctrines() {
        return new ArrayList<>(this.navalDoctrines.values());
    }

    public NavalDoctrine getNavalDoctrine(String name) {
        return this.navalDoctrines.get(name);
    }

    public List<ParliamentIssue> getParliamentIssues() {
        return new ArrayList<>(this.parliamentIssues.values());
    }

    public ParliamentIssue getParliamentIssue(String name) {
        return this.parliamentIssues.get(name);
    }

    public List<PersonalDeity> getPersonalDeities() {
        return new ArrayList<>(this.personalDeities.values());
    }

    public PersonalDeity getPersonalDeity(String name) {
        return this.personalDeities.get(name);
    }

    public List<ReligiousReforms> getReligiousReforms() {
        return new ArrayList<>(this.religiousReforms.values());
    }

    public ReligiousReforms getReligiousReforms(String name) {
        return this.religiousReforms.get(name);
    }

    public Collection<CrownLandBonus> getCrownLandBonuses() {
        return this.crownLandBonuses.values();
    }

    public List<StateEdict> getStateEdicts() {
        return new ArrayList<>(this.stateEdicts.values());
    }

    public StateEdict getStateEdict(String name) {
        return this.stateEdicts.get(name);
    }

    public List<TradePolicy> getTradePolicies() {
        return new ArrayList<>(this.tradePolicies.values());
    }

    public TradePolicy getTradePolicy(String name) {
        return this.tradePolicies.get(name);
    }

    public List<StaticModifier> getStaticModifiers() {
        return new ArrayList<>(this.staticModifiers.values());
    }

    public StaticModifier getStaticModifier(String name) {
        return this.staticModifiers.get(name);
    }

    public List<EventModifier> getEventModifiers() {
        return new ArrayList<>(this.eventModifiers.values());
    }

    public EventModifier getEventModifier(String name) {
        return this.eventModifiers.get(name);
    }

    public List<TriggeredModifier> getProvinceTriggeredModifiers() {
        return new ArrayList<>(this.provinceTriggeredModifiers.values());
    }

    public TriggeredModifier getProvinceTriggeredModifier(String name) {
        return this.provinceTriggeredModifiers.get(name);
    }

    public List<TriggeredModifier> getTriggeredModifiers() {
        return new ArrayList<>(this.triggeredModifiers.values());
    }

    public TriggeredModifier getTriggeredModifier(String name) {
        return this.triggeredModifiers.get(name);
    }

    public List<String> getCountryTags() {
        return new ArrayList<>(this.countryTags.keySet());
    }

    public GameModifier getModifier(String modifier) {
        modifier = ClausewitzUtils.removeQuotes(modifier).toLowerCase();

        return Eu4Utils.coalesce(modifier, this::getStaticModifier, this::getParliamentIssue, this::getEventModifier, this::getProvinceTriggeredModifier,
                                 this::getTriggeredModifier);
    }

    public Stream<FileNode> getFlagsFileNodes() {
        return getFileNodes(this.gfxFolderPath + File.separator + "flags",
                            fileNode -> Files.isRegularFile(fileNode.getPath()),
                            fileNode -> fileNode.getPath().toString().endsWith(".tga"));
    }

    private void readMods(List<String> modsEnabled) throws IOException {
        this.filesNode = new TreeNode<>(null, new FileNode(Paths.get(this.gameFolderPath), false), FileNode::getChildren);

        if (CollectionUtils.isNotEmpty(modsEnabled)) {
            //Compare with path so replace with system separator
            Map<String, Mod> knownMods = new HashMap<>();
            try (Stream<Path> stream = Files.list(Paths.get(this.modFolderPath))) {
                stream.filter(path -> path.getFileName().toString().endsWith(".mod"))
                      .filter(path -> path.toFile().exists() && path.toFile().canRead())
                      .forEach(path -> {
                          Mod mod = new Mod(path.toFile(), ClausewitzParser.parse(path.toFile(), 0));
                          knownMods.put(path.getFileName().toString(), mod);

                          if (!Eu4Utils.MOD_FILE_NAME_PATTERN.matcher(path.getFileName().toString()).matches()) {
                              if (StringUtils.isNotBlank(mod.getRemoteFileId())) {
                                  knownMods.put("ugc_" + ClausewitzUtils.removeQuotes(mod.getRemoteFileId()) + ".mod", mod);
                              }
                          }
                      });
            }

            Map<File, List<String>> map = new LinkedHashMap<>();

            for (String modName : modsEnabled) {
                Mod mod = knownMods.get(ClausewitzUtils.removeQuotes(modName).replaceAll("^mod/", ""));
                if (mod != null) {
                    map.put(mod.getPath(), mod.getReplacePath()
                                              .stream()
                                              .map(ClausewitzUtils::removeQuotes)
                                              .map(s -> Path.of(s).toString())
                                              .collect(Collectors.toList()));
                } else {
                    throw new ModNotFoundException(modName);
                }
            }

            Map<Path, List<String>> mods = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                    if (!key.isAbsolute()) {
                        key = new File(this.modFolderPath + File.separator + key.getPath().replaceFirst("^mod\\\\", ""));
                    }

                    if (key.exists() && key.canRead()) {
                        mods.put(key.toPath(), value);
                    }
            });

            mods.forEach((path, replacePaths) -> { //This technique replace only folders, so don't check for files
                this.filesNode.removeChildrenIf(fileNode -> fileNode.getPath().toFile().isDirectory()
                                                            && replacePaths.contains(fileNode.getRelativePath().toString()));
                this.filesNode.merge(new TreeNode<>(null, new FileNode(path, true), FileNode::getChildren));
            });
        }
    }

    public void loadDefines() throws IOException {
        this.defines = LuaParser.parse(getAbsoluteFile(this.commonFolderPath + File.separator + "defines.lua"));

        getPaths(this.commonFolderPath + File.separator + "defines", this::isRegularTxtFile)
                .forEach(path -> {
                    try {
                        Map<String, Object> map = LuaParser.parse(path.toFile());

                        map.forEach((rootName, values) -> this.defines.compute(rootName, (root, def) -> {
                            if (def == null) {
                                def = new LinkedHashMap<>();
                            }

                            ((Map<String, Object>) def).putAll((Map<String, Object>) values);

                            return def;
                        }));
                    } catch (IOException e) {
                        LOGGER.error("Could not read file {} because: {} !", path, e.getMessage(), e);
                    }
                });

        this.defines = (Map<String, Object>) this.defines.get("NDefines");
    }

    public void loadLocalisations() {
        loadLocalisations(Eu4Language.getByLocale(Locale.getDefault()));
    }

    private void loadLocalisations(Eu4Language eu4Language) {
        this.localisations = new HashMap<>();

        getPaths(this.localisationFolderPath,
                 fileNode -> Files.isRegularFile(fileNode.getPath()),
                 fileNode -> fileNode.getPath().toString().endsWith(eu4Language.fileEndWith + ".yml"))
                .forEach(path -> {
                    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                        String line;
                        reader.readLine(); //Skip first line language

                        while ((line = reader.readLine()) != null) {
                            if (ClausewitzUtils.isBlank(line)) {
                                continue;
                            }

                            int indexOf;
                            if ((indexOf = line.indexOf('#')) >= 0) { //If has comments
                                if (line.indexOf('"') < 0) { //If has no data
                                    continue;
                                }

                                if (line.indexOf('"') < indexOf) { //If data is before comment
                                    line = line.substring(0, indexOf);

                                    if (ClausewitzUtils.isBlank(line)) {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            }

                            String[] keys = line.split(":", 2);
                            String key = keys[0].trim();
                            String value = keys[1];
                            int start = value.indexOf('"') + 1;
                            int end = value.lastIndexOf('"');

                            if (start > end) {
                                continue;
                            }

                            this.localisations.put(key, StringUtils.capitalize(value.substring(start, end).trim()));
                        }
                    } catch (IOException e) {
                        LOGGER.error("Could not read file {} because: {} !", path, e.getMessage(), e);
                    }
                });
    }

    private void readSpriteTypes() {
        this.spriteTypes = new HashMap<>();

        getPaths(this.interfaceFolderPath,
                 fileNode -> Files.isRegularFile(fileNode.getPath()),
                 fileNode -> fileNode.getPath().toString().endsWith(".gfx"))
                .forEach(path -> {
                    ClausewitzItem rootItem = ClausewitzParser.parse(path.toFile(), 0);
                    ClausewitzItem spriteTypesItem = rootItem.getChild("spriteTypes");

                    if (spriteTypesItem != null) {
                        this.spriteTypes.putAll(spriteTypesItem.getChildren("spriteType")
                                                               .stream()
                                                               .map(SpriteType::new)
                                                               .collect(Collectors.toMap(spriteType -> ClausewitzUtils.removeQuotes(spriteType.getName()),
                                                                                         Function.identity(),
                                                                                         (a, b) -> a)));
                    }
                });
    }

    private void readProvinces() throws IOException {
        File provincesDefinitionFile = getAbsoluteFile(this.mapFolderPath + File.separator + "definition.csv");

        if (provincesDefinitionFile != null && provincesDefinitionFile.canRead()) {
            this.provinces = new HashMap<>();
            this.provincesByColor = new HashMap<>();
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), StandardCharsets.ISO_8859_1)) {
                String line;
                reader.readLine(); //Skip csv headers
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        Province province = new Province(line.split(";", -1));
                        this.provinces.put(province.getId(), province);

                        if (province.getColor() != null) {
                            this.provincesByColor.put(province.getColor(), province);
                        }
                    }
                }
            }

            File provincesMapFile = getAbsoluteFile(this.mapFolderPath + File.separator + "default.map");

            if (provincesMapFile != null && provincesMapFile.canRead()) {
                ClausewitzItem provinceMapItem = ClausewitzParser.parse(provincesMapFile, 0);
                ClausewitzList seaList = provinceMapItem.getList("sea_starts");

                if (seaList != null) {
                    seaList.getValuesAsInt().forEach(id -> this.provinces.get(id).setOcean(true));
                }

                ClausewitzList lakesList = provinceMapItem.getList("lakes");

                if (lakesList != null) {
                    lakesList.getValuesAsInt().forEach(id -> this.provinces.get(id).setLake(true));
                }
            }

            File climateFile = getAbsoluteFile(this.mapFolderPath + File.separator + "climate.txt");

            if (climateFile != null && climateFile.canRead()) {
                ClausewitzItem climateItem = ClausewitzParser.parse(climateFile, 0);
                climateItem.getLists()
                           .forEach(list -> {
                               if (list.getName().endsWith("_winter")) {
                                   list.getValuesAsInt().forEach(id -> this.provinces.get(id).setWinter(list.getName()));
                               } else if (Eu4Utils.IMPASSABLE_CLIMATE.equals(list.getName())) {
                                   list.getValuesAsInt().forEach(id -> this.provinces.get(id).setImpassable(true));
                               } else {
                                   list.getValuesAsInt().forEach(id -> this.provinces.get(id).setClimate(list.getName()));
                               }
                           });
            }

            File continentFile = getAbsoluteFile(this.mapFolderPath + File.separator + "continent.txt");

            if (continentFile != null && continentFile.canRead()) {
                ClausewitzItem continentsItem = ClausewitzParser.parse(continentFile, 0);

                this.continents = continentsItem.getListsNot("island_check_provinces")
                                                .stream()
                                                .map(Continent::new)
                                                .collect(Collectors.toMap(Continent::getName, Function.identity(), (a, b) -> a, LinkedHashMap::new));

                this.continents.values().forEach(continent -> {
                    continent.setLocalizedName(this.getLocalisation(continent.getName()));
                    continent.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setContinent(continent));
                });
            }

            File positionsFile = getAbsoluteFile(this.mapFolderPath + File.separator + "positions.txt");

            if (positionsFile != null && positionsFile.canRead()) {
                ClausewitzItem positionsItem = ClausewitzParser.parse(positionsFile, 0);

                positionsItem.getChildren().forEach(child -> {
                    Integer provinceId = NumbersUtils.toInt(child.getName());

                    if (provinceId != null && this.provinces.containsKey(provinceId) && child.hasList("position")) {
                        this.provinces.get(provinceId).setPositions(child.getList("position"));
                    }
                });
            }

            if (this.provincesImage != null && this.provincesImage.canRead()) {
                BufferedImage provinceImage = ImageIO.read(this.provincesImage);

                for (int x = 0; x < provinceImage.getWidth(); x++) {
                    for (int y = 0; y < provinceImage.getHeight(); y++) {
                        int[] rgb = provinceImage.getRaster().getPixel(x, y, (int[]) null);
                        Province province = getProvinceByColor(rgb[0], rgb[1], rgb[2]);
                        Province other;

                        if (province != null && province.isColonizable() && !province.isPort()) {
                            if (x > 0) {
                                int[] leftRgb = provinceImage.getRaster().getPixel(x - 1, y, (int[]) null);
                                if (!Arrays.equals(leftRgb, rgb) && (other = getProvinceByColor(leftRgb[0], leftRgb[1], leftRgb[2])) != null
                                    && other.isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (x < provinceImage.getWidth() - 1) {
                                int[] rightRgb = provinceImage.getRaster().getPixel(x + 1, y, (int[]) null);
                                if (!Arrays.equals(rightRgb, rgb) && (other = getProvinceByColor(rightRgb[0], rightRgb[1], rightRgb[2])) != null
                                    && other.isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y > 0) {
                                int[] topRgb = provinceImage.getRaster().getPixel(x, y - 1, (int[]) null);
                                if (!Arrays.equals(topRgb, rgb) && (other = getProvinceByColor(topRgb[0], topRgb[1], topRgb[2])) != null && other.isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y < provinceImage.getHeight() - 1) {
                                int[] bottomRgb = provinceImage.getRaster().getPixel(x, y + 1, (int[]) null);
                                if (!Arrays.equals(bottomRgb, rgb) && (other = getProvinceByColor(bottomRgb[0], bottomRgb[1], bottomRgb[2])) != null
                                    && other.isOcean()) {
                                    province.setPort(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void readCultures() {
        this.cultureGroups = new HashMap<>();
        getPaths(this.commonFolderPath + File.separator + "cultures", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cultureGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.cultureGroups.putAll(cultureGroupsItem.getChildren()
                                                               .stream()
                                                               .map(CultureGroup::new)
                                                               .collect(Collectors.toMap(AbstractCulture::getName, Function.identity(), (a, b) -> b)));
                });

        this.cultureGroups.values().forEach(cultureGroup -> {
            cultureGroup.setLocalizedName(this.getLocalisation(cultureGroup.getName()));
            cultureGroup.getCultures().forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
        });
    }

    private void readReligion() {
        this.religionGroups = new LinkedHashMap<>();
        getPaths(this.commonFolderPath + File.separator + "religions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem religionGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                    religionGroupsItem.getChildren()
                                      .stream()
                                      .map(ReligionGroup::new)
                                      .forEach(religionGroup -> this.religionGroups.merge(religionGroup.getName(), religionGroup, ReligionGroup::merge));
                });

        this.religionGroups.values().forEach(religionGroup -> {
            religionGroup.setLocalizedName(this.getLocalisation(religionGroup.getName()));
            religionGroup.getReligions().forEach(religion -> {
                religion.setLocalizedName(this.getLocalisation(religion.getName()));

                if (religion.getPapacy() != null) {
                    religion.getPapacy().getConcessions().forEach(papacyConcession -> {
                        papacyConcession.setConcilatoryLocalizedName(papacyConcession.getName() + "_concilatory");
                        papacyConcession.setHarshLocalizedName(papacyConcession.getName() + "_harsh");
                    });
                }
            });
        });
    }

    private void readInstitutions() {
        this.institutions = new LinkedHashMap<>();
        AtomicInteger i = new AtomicInteger();

        getPaths(this.commonFolderPath + File.separator + "institutions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.institutions.putAll(institutionsItem.getChildren()
                                                             .stream()
                                                             .map(Institution::new)
                                                             .collect(Collectors.toMap(Institution::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new)));
                });

        this.institutions.values().forEach(institution -> {
            institution.setLocalizedName(this.getLocalisation(institution.getName()));
            institution.setIndex(i.getAndIncrement());
        });
    }

    private void readTradeGoods() {
        this.tradeGoods = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "tradegoods", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradeGoods.putAll(tradeGoodsItem.getChildren()
                                                         .stream()
                                                         .map(TradeGood::new)
                                                         .collect(Collectors.toMap(TradeGood::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new)));
                });

        getPaths(this.commonFolderPath + File.separator + "prices", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0);
                    pricesItem.getChildren().forEach(priceItem -> {
                        if (this.tradeGoods.containsKey(priceItem.getName())) {
                            this.tradeGoods.get(priceItem.getName()).setPriceItem(priceItem);
                        }
                    });
                });

        this.tradeGoods.values().forEach(tradeGood -> tradeGood.setLocalizedName(this.getLocalisation(tradeGood.getName())));
    }

    private void readTradeNodes() {
        this.tradeNodes = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "tradenodes", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradeNodesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradeNodes.putAll(tradeNodesItem.getChildren()
                                                         .stream()
                                                         .map(TradeNode::new)
                                                         .collect(Collectors.toMap(TradeNode::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new)));
                });

        this.tradeNodes.values().forEach(tradeNode -> tradeNode.setLocalizedName(this.getLocalisation(tradeNode.getName())));
    }

    private void readBuildings() {
        this.buildings = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "buildings", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem buildingsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.buildings.putAll(buildingsItem.getChildrenNot("manufactory")
                                                       .stream()
                                                       .map(item -> new Building(item, this))
                                                       .collect(Collectors.toMap(Building::getName, Function.identity(), (a, b) -> b)));

                    if ((buildingsItem = buildingsItem.getChild("manufactory")) != null) {
                        Building manufactoryBuilding = new Building(buildingsItem, this);
                        this.buildings.values().forEach(building -> {
                            building.setInternalCost(manufactoryBuilding.getCost());
                            building.setInternalTime(manufactoryBuilding.getTime());
                            building.setInternalModifiers(manufactoryBuilding.getModifiers());
                        });
                    }
                });

        this.buildings.values().forEach(building -> building.setLocalizedName(this.getLocalisation("building_" + building.getName())));
    }

    private void readImperialReforms() {
        this.imperialReforms = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "imperial_reforms", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem imperialReformsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.imperialReforms.putAll(imperialReformsItem.getChildren()
                                                                   .stream()
                                                                   .map(item -> new ImperialReform(item, this))
                                                                   .collect(Collectors.toMap(ImperialReform::getName, Function.identity(), (a, b) -> b)));
                });

        this.imperialReforms.values().forEach(imperialReform -> imperialReform.setLocalizedName(this.getLocalisation(imperialReform.getName() + "_title")));
    }

    private void readDecrees() {
        this.decrees = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "decrees", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem decreesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.decrees.putAll(decreesItem.getChildren().stream().map(Decree::new).collect(Collectors.toMap(Decree::getName, Function.identity(), (a, b) -> b)));
                });

        this.decrees.values().forEach(saveDecree -> saveDecree.setLocalizedName(this.getLocalisation(saveDecree.getName() + "_title")));
    }

    private void readGoldenBulls() {
        this.goldenBulls = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "golden_bulls", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem goldenBullsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.goldenBulls.putAll(
                            goldenBullsItem.getChildren().stream().map(GoldenBull::new).collect(Collectors.toMap(GoldenBull::getName, Function.identity(), (a, b) -> b)));
                });

        this.goldenBulls.values().forEach(bull -> bull.setLocalizedName(this.getLocalisation(bull.getName())));
    }

    private void readEvents() {
        this.events = new HashMap<>();

        getPaths("events", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem eventsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.events.putAll(eventsItem.getChildren().stream().map(Event::new).filter(event -> event.getId() != null).collect(Collectors.toMap(Event::getId, Function.identity(), (e1, e2) -> e2)));
                });

        this.events.values().forEach(event -> event.setLocalizedName(this.getLocalisation(ClausewitzUtils.removeQuotes(event.getTitle()))));
    }

    private void readGovernments() {
        this.governments = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "governments", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governments.putAll(governmentsItem.getChildrenNot("pre_dharma_mapping")
                                                           .stream()
                                                           .map(item -> new Government(item, this))
                                                           .collect(Collectors.toMap(Government::getName, Function.identity(), (a, b) -> b)));
                });

        this.governments.values().forEach(government -> government.setLocalizedName(this.getLocalisation(government.getBasicReform())));
    }

    private void readGovernmentRanks() {
        this.governmentRanks = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "government_ranks", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentRanksItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governmentRanks.putAll(governmentRanksItem.getChildren()
                                                                   .stream()
                                                                   .map(GovernmentRank::new)
                                                                   .collect(Collectors.toMap(GovernmentRank::getLevel, Function.identity())));
                });
    }

    private void readGovernmentNames() {
        this.governmentNames = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "government_names", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governmentNames.putAll(governmentsItem.getChildren()
                                                               .stream()
                                                               .map(item -> new GovernmentName(item, this))
                                                               .collect(Collectors.toMap(GovernmentName::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readGovernmentReforms() {
        this.governmentReforms = new HashMap<>();

        List<ClausewitzItem> reformsItems = getPaths(this.commonFolderPath + File.separator + "government_reforms", this::isRegularTxtFile)
                .map(path -> ClausewitzParser.parse(path.toFile(), 0))
                .collect(Collectors.toList());

        AtomicReference<GovernmentReform> defaultReform = new AtomicReference<>();

        reformsItems.stream().filter(item -> item.hasChild("defaults_reform")).findFirst().ifPresent(item -> {
            defaultReform.set(new GovernmentReform(item.getChild("defaults_reform"), this, null));
        });

        this.governmentReforms.putAll(reformsItems.stream()
                                                  .map(item -> item.getChildrenNot("defaults_reform"))
                                                  .flatMap(Collection::stream)
                                                  .map(item -> new GovernmentReform(item, this, defaultReform.get()))
                                                  .collect(Collectors.toMap(GovernmentReform::getName, Function.identity(), (a, b) -> b)));

        this.governmentReforms.values().forEach(governmentReform -> governmentReform.setLocalizedName(this.getLocalisation(governmentReform.getName())));
    }

    private void readUnits() {
        this.units = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "units", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem unitItem = ClausewitzParser.parse(path.toFile(), 0);
                    unitItem.setName(FilenameUtils.removeExtension(path.getFileName().toString()));
                    Unit unit = new Unit(unitItem, this::getLocalisation);
                    this.units.put(unit.getName(), unit);
                });
    }

    private void readAreas() {
        this.areas = new HashMap<>();
        File areasFile = getAbsoluteFile(this.mapFolderPath + File.separator + "area.txt");

        if (areasFile != null && areasFile.canRead()) {
            ClausewitzItem areasItem = ClausewitzParser.parse(areasFile, 0);

            this.areas.putAll(areasItem.getLists().stream().map(Area::new).collect(Collectors.toMap(Area::getName, Function.identity(), (a, b) -> b)));
            this.areas.putAll(areasItem.getChildren().stream().map(Area::new).collect(Collectors.toMap(Area::getName, Function.identity(), (a, b) -> b)));
        }

        this.areas.values().forEach(area -> area.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setArea(area)));
    }

    private void readRegions() {
        this.regions = new HashMap<>();
        File regionsFile = getAbsoluteFile(this.mapFolderPath + File.separator + "region.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            this.regions.putAll(regionsItem.getChildren()
                                           .stream()
                                           .map(item -> new Region(item, this))
                                           .collect(Collectors.toMap(Region::getName, Function.identity(), (a, b) -> b)));
        }

        this.regions.values().stream().filter(region -> region.getAreas() != null).forEach(region -> region.getAreas().forEach(area -> area.setRegion(region)));
    }

    private void readSuperRegions() {
        this.superRegions = new HashMap<>();
        File regionsFile = getAbsoluteFile(this.mapFolderPath + File.separator + "superregion.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            this.superRegions.putAll(regionsItem.getLists()
                                                .stream()
                                                .map(list -> new SuperRegion(list, this))
                                                .collect(Collectors.toMap(SuperRegion::getName, Function.identity(), (a, b) -> b)));
        }

        this.superRegions.values()
                         .stream()
                         .filter(superRegion -> superRegion.getRegions() != null)
                         .forEach(superRegion -> superRegion.getRegions().forEach(region -> region.setSuperRegion(superRegion)));
    }

    private void readTechGroups() {
        this.techGroups = new HashMap<>();
        File techGroupsFile = getAbsoluteFile(this.commonFolderPath + File.separator + "technology.txt");

        if (techGroupsFile != null && techGroupsFile.canRead()) {
            ClausewitzItem techGroupsItem = ClausewitzParser.parse(techGroupsFile, 0);
            this.techGroups.putAll(techGroupsItem.getChild("groups")
                                                 .getChildren()
                                                 .stream()
                                                 .map(TechGroup::new)
                                                 .collect(Collectors.toMap(TechGroup::getName, Function.identity(), (a, b) -> b)));
        }

        this.techGroups.values().forEach(techGroup -> techGroup.setLocalizedName(this.getLocalisation(techGroup.getName())));
    }

    private void readAdvisors() {
        this.advisors = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "advisortypes", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.advisors.putAll(advisorsItem.getChildren()
                                                     .stream()
                                                     .map(Advisor::new)
                                                     .collect(Collectors.toMap(Advisor::getName, Function.identity(), (a, b) -> b)));
                });

        this.advisors.values().forEach(advisor -> advisor.setLocalizedName(this.getLocalisation(advisor.getName())));
    }

    private void readIdeaGroups() {
        this.ideaGroups = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ideas", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem ideasItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.ideaGroups.putAll(ideasItem.getChildren()
                                                    .stream()
                                                    .map(IdeaGroup::new)
                                                    .collect(Collectors.toMap(IdeaGroup::getName, Function.identity(), (a, b) -> b)));
                });

        this.ideaGroups.values().forEach(ideaGroup -> ideaGroup.setLocalizedName(this.getLocalisation(ideaGroup.getName())));
    }

    private void readCasusBelli() {
        this.casusBelli = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "cb_types", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cbItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.casusBelli.putAll(cbItem.getChildren()
                                                 .stream()
                                                 .map(CasusBelli::new)
                                                 .collect(Collectors.toMap(CasusBelli::getName, Function.identity(), (a, b) -> b)));
                });

        this.casusBelli.values().forEach(cb -> cb.setLocalizedName(this.getLocalisation(cb.getName())));
    }

    private void readColonialRegions() {
        this.colonialRegions = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "colonial_regions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem colonialRegionsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.colonialRegions.putAll(colonialRegionsItem.getChildren()
                                                                   .stream()
                                                                   .map(item -> new ColonialRegion(item, this))
                                                                   .collect(Collectors.toMap(ColonialRegion::getName, Function.identity(), (a, b) -> b)));
                });

        this.colonialRegions.values().forEach(colonialRegion -> colonialRegion.setLocalizedName(this.getLocalisation(colonialRegion.getName())));
    }

    private void readTradeCompanies() {
        this.tradeCompanies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "trade_companies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradeCompaniesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradeCompanies.putAll(tradeCompaniesItem.getChildren()
                                                                 .stream()
                                                                 .map(TradeCompany::new)
                                                                 .collect(Collectors.toMap(TradeCompany::getName, Function.identity(), (a, b) -> b)));
                });

        this.tradeCompanies.values().forEach(tradeCompany -> tradeCompany.setLocalizedName(this.getLocalisation(tradeCompany.getName())));
    }

    private void readSubjectTypes() {
        this.subjectTypes = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "subject_types", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem subjectTypesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.subjectTypes.putAll(subjectTypesItem.getChildren()
                                                             .stream()
                                                             .map(item -> new SubjectType(item, this.subjectTypes.values()))
                                                             .collect(Collectors.toMap(SubjectType::getName, Function.identity(), (a, b) -> b)));
                });

        this.subjectTypes.values().forEach(subjectType -> subjectType.setLocalizedName(this.getLocalisation(subjectType.getName() + "_title")));
    }

    private void readFetishistCults() {
        this.fetishistCults = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "fetishist_cults", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cultsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.fetishistCults.putAll(cultsItem.getChildren()
                                                        .stream()
                                                        .map(FetishistCult::new)
                                                        .collect(Collectors.toMap(FetishistCult::getName, Function.identity(), (a, b) -> b)));
                });

        this.fetishistCults.values().forEach(fetishistCult -> fetishistCult.setLocalizedName(this.getLocalisation(fetishistCult.getName())));
    }

    private void readChurchAspects() {
        this.churchAspects = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "church_aspects", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem aspectsItems = ClausewitzParser.parse(path.toFile(), 0);
                    this.churchAspects.putAll(aspectsItems.getChildren()
                                                          .stream()
                                                          .map(ChurchAspect::new)
                                                          .collect(Collectors.toMap(ChurchAspect::getName, Function.identity(), (a, b) -> b)));
                });

        this.churchAspects.values().forEach(fetishistCult -> fetishistCult.setLocalizedName(this.getLocalisation(fetishistCult.getName())));
    }

    private void readMissionTrees() {
        this.missionTrees = new HashMap<>();

        getPaths(this.missionsFolderPath, this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.missionTrees.putAll(advisorsItem.getChildren()
                                                         .stream()
                                                         .map(item -> new MissionTree(item, this))
                                                         .collect(Collectors.toMap(MissionTree::getName, Function.identity(), (a, b) -> b)));
                });

        this.missionTrees.values().forEach(missionTree -> {
            missionTree.setLocalizedName(this.getLocalisation(missionTree.getName()));
            missionTree.getMissions().values().forEach(mission -> {
                mission.setLocalizedName(this.getLocalisation(mission.getName()));
                mission.setRequiredMissions(this);
            });
        });
    }

    private void readEstates() {
        //Read estates modifiers before necessary for privileges
        Map<String, List<ModifierDefinition>> modifierDefinitions = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "estates_preload", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem estatePreloadItem = ClausewitzParser.parse(path.toFile(), 0);
                    estatePreloadItem.getChildren().forEach(item -> modifierDefinitions.put(item.getName(),
                                                                                            item.getChildren("modifier_definition")
                                                                                                .stream()
                                                                                                .map(ModifierDefinition::new)
                                                                                                .collect(Collectors.toList())));
                });

        modifierDefinitions.values()
                           .stream()
                           .flatMap(Collection::stream)
                           .forEach(modifierDefinition -> ModifiersUtils.addModifier(modifierDefinition.getKey(), ModifierType.MULTIPLICATIVE,
                                                                                     ModifierScope.COUNTRY));

        this.estatePrivileges = new HashMap<>();
        getPaths(this.commonFolderPath + File.separator + "estate_privileges", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem estatePrivilegesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.estatePrivileges.putAll(estatePrivilegesItem.getChildren()
                                                                     .stream()
                                                                     .map(EstatePrivilege::new)
                                                                     .collect(Collectors.toMap(EstatePrivilege::getName, Function.identity(), (a, b) -> b)));
                });

        this.estates = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "estates", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem estatesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.estates.putAll(estatesItem.getChildren()
                                                   .stream()
                                                   .map(item -> new Estate(item, modifierDefinitions.get(item.getName()), this))
                                                   .collect(Collectors.toMap(Estate::getName, Function.identity(), (a, b) -> b)));
                });

        this.estates.values().forEach(estate -> estate.setLocalizedName(this.getLocalisation(estate.getName())));
        this.estatePrivileges.values().forEach(estatePrivilege -> estatePrivilege.setLocalizedName(this.getLocalisation(estatePrivilege.getName())));
    }

    private void readTechnologies() {
        this.technologies = new EnumMap<>(Power.class);
        List<Technology> techs = new ArrayList<>();

        getPaths(this.commonFolderPath + File.separator + "technologies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem techItem = ClausewitzParser.parse(path.toFile(), 0);

                    Power power = Power.byName(techItem.getVarAsString("monarch_power"));
                    Modifiers aheadOfTime = new Modifiers(techItem.getChild("ahead_of_time"));

                    techItem.getChildrenNot("ahead_of_time").forEach(item -> techs.add(new Technology(item, power, aheadOfTime)));
                });

        this.technologies = techs.stream().collect(Collectors.groupingBy(Technology::getType, TreeMap::new, Collectors.toCollection(TreeSet::new)));
    }

    private void readRulerPersonalities() {
        this.rulerPersonalities = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ruler_personalities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem rulerPersonalityItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.rulerPersonalities.putAll(rulerPersonalityItem.getChildren()
                                                                       .stream()
                                                                       .map(RulerPersonality::new)
                                                                       .collect(Collectors.toMap(RulerPersonality::getName, Function.identity(), (a, b) -> b)));
                });

        this.rulerPersonalities.values().forEach(rulerPersonality -> rulerPersonality.setLocalizedName(this.getLocalisation(rulerPersonality.getName())));
    }

    private void readLeaderPersonalities() {
        this.leaderPersonalities = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "leader_personalities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem leaderPersonalityItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.leaderPersonalities.putAll(leaderPersonalityItem.getChildren()
                                                                       .stream()
                                                                       .map(LeaderPersonality::new)
                                                                       .collect(Collectors.toMap(LeaderPersonality::getName, Function.identity(), (a, b) -> b)));
                });

        this.leaderPersonalities.values().forEach(leaderPersonality -> leaderPersonality.setLocalizedName(this.getLocalisation(leaderPersonality.getName())));
    }

    private void readProfessionalismModifiers() {
        this.professionalismModifiers = new TreeSet<>();

        getPaths(this.commonFolderPath + File.separator + "professionalism", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.professionalismModifiers.addAll(advisorsItem.getChildren()
                                                                     .stream()
                                                                     .map(ProfessionalismModifier::new)
                                                                     .collect(Collectors.toSet()));
                });
    }

    private void readStaticModifiers() {
        this.staticModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "static_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem modifiersItem = ClausewitzParser.parse(path.toFile(), 0);
                    modifiersItem.getChildrenNot("null_modifier").forEach(item -> {
                        if (StaticModifiers.value(item.getName()) != null) {
                            StaticModifiers.value(item.getName()).setModifiers(new Modifiers(item));
                            StaticModifier staticModifier = new StaticModifier(item);
                            this.staticModifiers.put(staticModifier.name, staticModifier);
                        }
                    });
                });

        this.staticModifiers.values().forEach(staticModifier -> staticModifier.setLocalizedName(this.getLocalisation(staticModifier.getName())));
    }

    private void readInvestments() {
        this.investments = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "tradecompany_investments", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.investments.putAll(investmentsItem.getChildren()
                                                           .stream()
                                                           .map(item -> new Investment(item, this))
                                                           .collect(Collectors.toMap(Investment::getName, Function.identity(), (a, b) -> b)));
                });

        this.investments.values().forEach(investment -> investment.setLocalizedName(this.getLocalisation(investment.getName())));
    }

    private void readPolicies() {
        this.policies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "policies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.policies.putAll(investmentsItem.getChildren()
                                                        .stream()
                                                        .map(Policy::new)
                                                        .collect(Collectors.toMap(Policy::getName, Function.identity(), (a, b) -> b)));
                });

        this.policies.values().forEach(policy -> policy.setLocalizedName(this.getLocalisation(policy.getName())));
    }

    private void readHegemons() {
        this.hegemons = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "hegemons", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.hegemons.putAll(hegemonsItem.getChildren()
                                                     .stream()
                                                     .map(Hegemon::new)
                                                     .collect(Collectors.toMap(Hegemon::getName, Function.identity(), (a, b) -> b)));
                });

        this.hegemons.values().forEach(hegemon -> hegemon.setLocalizedName(this.getLocalisation(hegemon.getName())));
    }

    private void readFactions() {
        this.factions = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "factions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.factions.putAll(hegemonsItem.getChildren()
                                                     .stream()
                                                     .map(Faction::new)
                                                     .collect(Collectors.toMap(Faction::getName, Function.identity(), (a, b) -> b)));

                });

        this.factions.values().forEach(faction -> {
            faction.setLocalizedName(this.getLocalisation(faction.getName()));
            ModifiersUtils.addModifier(ClausewitzUtils.removeQuotes(faction.getName()) + "_influence", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        });
    }

    private void readAges() {
        this.ages = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ages", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem agesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.ages.putAll(agesItem.getChildren()
                                             .stream()
                                             .map(Age::new)
                                             .collect(Collectors.toMap(Age::getName, Function.identity(), (a, b) -> b)));
                });

        this.ages.values().forEach(age -> age.setLocalizedName(this.getLocalisation(age.getName())));
    }

    private void readDefenderOfFaith() {
        this.defenderOfFaith = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "defender_of_faith", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.defenderOfFaith.putAll(defenderOfFaithItem.getChildren()
                                                                   .stream()
                                                                   .map(DefenderOfFaith::new)
                                                                   .collect(Collectors.toMap(DefenderOfFaith::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readCentersOfTrade() {
        this.centersOfTrade = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "centers_of_trade", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem centersOfTradeItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.centersOfTrade.putAll(centersOfTradeItem.getChildren()
                                                                 .stream()
                                                                 .map(CenterOfTrade::new)
                                                                 .collect(Collectors.toMap(CenterOfTrade::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readFervors() {
        this.fervors = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "fervor", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem fervorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.fervors.putAll(fervorsItem.getChildren()
                                                   .stream()
                                                   .map(Fervor::new)
                                                   .collect(Collectors.toMap(Fervor::getName, Function.identity(), (a, b) -> b)));
                });

        this.fervors.values().forEach(fervor -> fervor.setLocalizedName(this.getLocalisation(fervor.getName())));
    }

    private void readGreatProjects() {
        this.greatProjects = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "great_projects", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem greatProjectsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.greatProjects.putAll(greatProjectsItem.getChildren()
                                                               .stream()
                                                               .map(GreatProject::new)
                                                               .collect(Collectors.toMap(GreatProject::getName, Function.identity(), (a, b) -> b)));
                });

        this.greatProjects.values().forEach(greatProject -> greatProject.setLocalizedName(this.getLocalisation(greatProject.getName())));
    }

    private void readHolyOrders() {
        this.holyOrders = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "holy_orders", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem holyOrdersItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.holyOrders.putAll(holyOrdersItem.getChildren()
                                                         .stream()
                                                         .map(HolyOrder::new)
                                                         .collect(Collectors.toMap(HolyOrder::getName, Function.identity(), (a, b) -> b)));
                });

        this.holyOrders.values().forEach(holyOrder -> holyOrder.setLocalizedName(this.getLocalisation(holyOrder.getName())));
    }

    private void readIsolationism() {
        this.isolationisms = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "isolationism", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem isolationismItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.isolationisms.putAll(isolationismItem.getChildren()
                                                              .stream()
                                                              .map(Isolationism::new)
                                                              .collect(Collectors.toMap(Isolationism::getName, Function.identity(), (a, b) -> b)));
                });

        this.isolationisms.values().forEach(isolationism -> isolationism.setLocalizedName(this.getLocalisation(isolationism.getName())));
    }

    private void readNativeAdvancements() {
        this.nativeAdvancements = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "native_advancement", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem nativeAdvancementItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.nativeAdvancements.putAll(nativeAdvancementItem.getChildren()
                                                                        .stream()
                                                                        .map(NativeAdvancements::new)
                                                                        .collect(Collectors.toMap(NativeAdvancements::getName, Function.identity(), (a, b) -> b)));
                });

        this.nativeAdvancements.values().forEach(advancements -> {
            advancements.setLocalizedName(this.getLocalisation(advancements.getName()));
            advancements.getNativeAdvancements()
                        .forEach(nativeAdvancement -> nativeAdvancement.setLocalizedName(this.getLocalisation(nativeAdvancement.getName())));
        });
    }

    private void readNavalDoctrine() {
        this.navalDoctrines = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "naval_doctrines", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem navalDoctrineItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.navalDoctrines.putAll(navalDoctrineItem.getChildren()
                                                                .stream()
                                                                .map(NavalDoctrine::new)
                                                                .collect(Collectors.toMap(NavalDoctrine::getName, Function.identity(), (a, b) -> b)));
                });

        this.navalDoctrines.values().forEach(navalDoctrine -> navalDoctrine.setLocalizedName(this.getLocalisation(navalDoctrine.getName())));
    }

    private void readParliamentIssue() {
        this.parliamentIssues = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "parliament_issues", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem parliamentIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.parliamentIssues.putAll(parliamentIssueItem.getChildren()
                                                                    .stream()
                                                                    .map(ParliamentIssue::new)
                                                                    .collect(Collectors.toMap(ParliamentIssue::getName, Function.identity(), (a, b) -> b)));
                });

        this.parliamentIssues.values().forEach(parliamentIssue -> parliamentIssue.setLocalizedName(this.getLocalisation(parliamentIssue.getName())));
    }

    private void readPersonalDeities() {
        this.personalDeities = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "personal_deities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem personalIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.personalDeities.putAll(personalIssueItem.getChildren()
                                                                 .stream()
                                                                 .map(PersonalDeity::new)
                                                                 .collect(Collectors.toMap(PersonalDeity::getName, Function.identity(), (a, b) -> b)));
                });

        this.personalDeities.values().forEach(personalIssue -> personalIssue.setLocalizedName(this.getLocalisation(personalIssue.getName())));
    }

    private void readReligiousReforms() {
        this.religiousReforms = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "religious_reforms", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem religiousReformItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.religiousReforms.putAll(religiousReformItem.getChildren()
                                                                    .stream()
                                                                    .map(ReligiousReforms::new)
                                                                    .collect(Collectors.toMap(ReligiousReforms::getName, Function.identity(), (a, b) -> b)));
                });

        this.religiousReforms.values().forEach(reforms -> {
            reforms.setLocalizedName(this.getLocalisation(reforms.getName()));
            reforms.getReforms().forEach(religiousReform -> religiousReform.setLocalizedName(this.getLocalisation(religiousReform.getName())));
        });
    }

    private void readCrownLandBonuses() {
        this.crownLandBonuses = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "estate_crown_land", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.crownLandBonuses.putAll(defenderOfFaithItem.getChildren()
                                                                    .stream()
                                                                    .map(CrownLandBonus::new)
                                                                    .collect(Collectors.toMap(CrownLandBonus::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readStateEdicts() {
        this.stateEdicts = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "state_edicts", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem stateEdictsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.stateEdicts.putAll(stateEdictsItem.getChildren()
                                                           .stream()
                                                           .map(StateEdict::new)
                                                           .collect(Collectors.toMap(StateEdict::getName, Function.identity(), (a, b) -> b)));
                });

        this.stateEdicts.values().forEach(stateEdict -> stateEdict.setLocalizedName(this.getLocalisation(stateEdict.getName())));
    }

    private void readTradePolicies() {
        this.tradePolicies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "trading_policies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradePoliciesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradePolicies.putAll(tradePoliciesItem.getChildren()
                                                               .stream()
                                                               .map(TradePolicy::new)
                                                               .collect(Collectors.toMap(TradePolicy::getName, Function.identity(), (a, b) -> b)));

                });

        this.tradePolicies.values().forEach(tradePolicy -> tradePolicy.setLocalizedName(this.getLocalisation(tradePolicy.getName())));
    }

    private void readEventModifiers() {
        this.eventModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "event_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem eventModifierItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.eventModifiers.putAll(eventModifierItem.getChildren()
                                                     .stream()
                                                     .map(EventModifier::new)
                                                     .collect(Collectors.toMap(EventModifier::getName, Function.identity(), (a, b) -> b)));
                });

        this.eventModifiers.values().forEach(eventModifier -> eventModifier.setLocalizedName(this.getLocalisation(eventModifier.getName())));
    }

    private void readProvinceTriggeredModifiers() {
        this.provinceTriggeredModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "province_triggered_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem provinceTriggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.provinceTriggeredModifiers.putAll(provinceTriggeredIssueItem.getChildren()
                                                     .stream()
                                                     .map(TriggeredModifier::new)
                                                     .collect(Collectors.toMap(TriggeredModifier::getName, Function.identity(), (a, b) -> b)));
                });

        this.provinceTriggeredModifiers.values()
                                       .forEach(provinceTriggered -> provinceTriggered.setLocalizedName(this.getLocalisation(provinceTriggered.getName())));
    }

    private void readTriggeredModifiers() {
        this.triggeredModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "triggered_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem triggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.triggeredModifiers.putAll(triggeredIssueItem.getChildren()
                                                                     .stream()
                                                                     .map(TriggeredModifier::new)
                                                                     .collect(Collectors.toMap(GameModifier::getName, Function.identity(), (a, b) -> b)));

                });

        this.triggeredModifiers.values().forEach(triggeredModifier -> triggeredModifier.setLocalizedName(this.getLocalisation(triggeredModifier.getName())));
    }

    private void readCountryTags() {
        this.countryTags = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "country_tags", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem countryTagsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.countryTags.putAll(countryTagsItem.getVariables()
                                                           .stream()
                                                           .collect(Collectors.toMap(var -> var.getName().toUpperCase(), var -> ClausewitzUtils.removeQuotes(var.getValue()), (a, b) -> b)));

                });
    }

    private boolean isRegularTxtFile(FileNode fileNode) {
        return Files.isRegularFile(fileNode.getPath()) && TXT_PATH_MATCHER.matches(fileNode.getPath());
    }
}
