package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.common.LuaUtils;
import com.osallek.eu4parser.common.ModifierScope;
import com.osallek.eu4parser.common.ModifierType;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.common.TreeNode;
import com.osallek.eu4parser.model.Power;
import com.osallek.eu4parser.model.game.localisation.Eu4Language;
import com.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.parser.ParseException;
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
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private final String gameFolderPath;

    private final String modFolderPath;

    private final String mapFolderPath;

    private final String commonFolderPath;

    private final String gfxFolderPath;

    private final String localisationFolderPath;

    private final String interfaceFolderPath;

    private final String missionsFolderPath;

    private File provincesImage;

    private TreeNode<FileNode> filesNode;

    private Map<Integer, Province> provinces;

    private Map<Integer, Province> provincesByColor;

    private Map<String, Continent> continents;

    private Map<String, CultureGroup> cultureGroups;

    private Map<String, ReligionGroup> religionGroups;

    private Map<String, Institution> institutions;

    private Map<String, TradeGood> tradeGoods;

    private Map<String, Building> buildings;

    private Map<String, String> localisations;

    private Map<String, SpriteType> spriteTypes;

    private Map<String, ImperialReform> imperialReforms;

    private Map<Decree, Path> decrees;

    private Map<GoldenBull, Path> goldenBulls;

    private Map<Event, Path> events;

    private Map<Government, Path> governments;

    private Map<GovernmentRank, Path> governmentRanks;

    private Map<GovernmentName, Path> governmentNames;

    private Map<GovernmentReform, Path> governmentReforms;

    private Map<Unit, Path> units;

    private Map<Area, Path> areas;

    private Map<Advisor, Path> advisors;

    private Map<IdeaGroup, Path> ideaGroups;

    private Map<CasusBelli, Path> casusBelli;

    private Map<ColonialRegion, Path> colonialRegions;

    private Map<TradeCompany, Path> tradeCompanies;

    private Map<Region, Path> regions;

    private Map<SuperRegion, Path> superRegions;

    private Map<TechGroup, Path> techGroups;

    private Map<SubjectType, Path> subjectTypes;

    private Map<FetishistCult, Path> fetishistCults;

    private Map<ChurchAspect, Path> churchAspects;

    private Map<MissionTree, Path> missionTrees;

    private Map<EstatePrivilege, Path> estatePrivileges;

    private Map<Estate, Path> estates;

    private Map<Power, SortedMap<Technology, Path>> technologies;

    private SortedMap<ProfessionalismModifier, Path> professionalismModifiers;

    private final Map<String, Map<String, Exp.Constant>> defines;

    private Map<RulerPersonality, Path> rulerPersonalities;

    private Map<Investment, Path> investments;

    private Map<Policy, Path> policies;

    private Map<Hegemon, Path> hegemons;

    private Map<Faction, Path> factions;

    private Map<Age, Path> ages;

    private SortedMap<DefenderOfFaith, Path> defenderOfFaith;

    private SortedMap<CenterOfTrade, Path> centersOfTrade;

    private Map<Fervor, Path> fervors;

    private Map<GreatProject, Path> greatProjects;

    private Map<HolyOrder, Path> holyOrders;

    private SortedMap<Isolationism, Path> isolationisms;

    private Map<NativeAdvancements, Path> nativeAdvancements;

    private Map<NavalDoctrine, Path> navalDoctrines;

    private Map<ParliamentIssue, Path> parliamentIssues;

    private Map<PersonalDeity, Path> personalDeities;

    private Map<ReligiousReforms, Path> religiousReforms;

    private Map<CrownLandBonus, Path> crownLandBonuses;

    private Map<StateEdict, Path> stateEdicts;

    private Map<TradePolicy, Path> tradePolicies;

    private Map<StaticModifier, Path> staticModifiers;

    private Map<EventModifier, Path> eventModifiers;

    private Map<TriggeredModifier, Path> provinceTriggeredModifiers;

    private Map<TriggeredModifier, Path> triggeredModifiers;

    public Game(String gameFolderPath, String modFolderPath, List<String> modEnabled) throws IOException, ParseException {
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
        this.defines = LuaUtils.luaFileToMap(getAbsoluteFile(this.commonFolderPath + File.separator + "defines.lua"));
        this.provincesImage = getAbsoluteFile(this.mapFolderPath + File.separator + "provinces.bmp");

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
        readColonialRegions();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time to read game date: {}ms !", Duration.between(start, Instant.now()).toMillis());
        }
    }

    private TreeNode<FileNode> getTreeNode(String relativePath) {
        return this.filesNode.getRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath().toString()));
    }

    private FileNode getFileNode(String relativePath) {
        return this.filesNode.getDataRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath().toString()));
    }

    @SafeVarargs
    private Stream<Path> getPaths(String relativePath, Predicate<FileNode>... predicates) {
        TreeNode<FileNode> treeNode = getTreeNode(relativePath);
        return treeNode == null ? Stream.empty() : treeNode.getLeaves(predicates)
                                                           .stream()
                                                           .map(TreeNode::getData)
                                                           .map(FileNode::getPath);
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
        return country == null ? null : getAbsoluteFile(this.gfxFolderPath + File.separator + "flags" + File.separator + country.getTag() + ".tga");
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
        return new ArrayList<>(this.decrees.keySet());
    }

    public Decree getDecree(String name) {
        if (name == null) {
            return null;
        }

        for (Decree saveDecree : this.decrees.keySet()) {
            if (saveDecree.getName().equalsIgnoreCase(name)) {
                return saveDecree;
            }
        }

        return null;
    }

    public List<GoldenBull> getGoldenBulls() {
        return new ArrayList<>(this.goldenBulls.keySet());
    }

    public GoldenBull getGoldenBull(String name) {
        if (name == null) {
            return null;
        }

        for (GoldenBull saveGoldenBull : this.goldenBulls.keySet()) {
            if (saveGoldenBull.getName().equalsIgnoreCase(name)) {
                return saveGoldenBull;
            }
        }

        return null;
    }

    public List<Event> getEvents() {
        return this.events.keySet()
                          .stream()
                          .sorted(Comparator.comparing(Event::getLocalizedName, Eu4Utils.COLLATOR))
                          .collect(Collectors.toList());
    }

    public List<Event> getFireOnlyOnceEvents() {
        return this.events.keySet()
                          .stream()
                          .filter(Event::fireOnlyOnce)
                          .sorted(Comparator.comparing(Event::getLocalizedName, Eu4Utils.COLLATOR))
                          .collect(Collectors.toList());
    }

    public Event getEvent(String id) {
        if (id == null) {
            return null;
        }

        for (Event event : this.events.keySet()) {
            if (event.getId().equalsIgnoreCase(id)) {
                return event;
            }
        }

        return null;
    }

    public int getMaxGovRank() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("MAX_GOV_RANK").value.toint();
    }

    public int getMaxAspects() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("MAX_UNLOCKED_ASPECTS").value.toint();
    }

    public int getGoldenEraDuration() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("GOLDEN_ERA_YEARS").value.toint();
    }

    public int getBankruptcyDuration() {
        return this.defines.get(Eu4Utils.DEFINE_ECONOMY_KEY).get("BANKRUPTCY_DURATION").value.toint();
    }

    public int getNbGreatPowers() {
        return this.defines.get(Eu4Utils.DEFINE_DIPLOMACY_KEY).get("NUM_OF_GREAT_POWERS").value.toint();
    }

    public int getNomadDevelopmentScale() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NOMAD_DEVELOPMENT_SCALE").value.toint();
    }

    public int getLargeColonialNationLimit() {
        return this.defines.get(Eu4Utils.DEFINE_ECONOMY_KEY).get("LARGE_COLONIAL_NATION_LIMIT").value.toint();
    }

    public int getFortPerDevRatio() {
        return this.defines.get(Eu4Utils.DEFINE_MILITARY_KEY).get("FORT_PER_DEV_RATIO").value.toint();
    }

    public double getMaxArmyProfessionalism() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("MAX_ARMY_PROFESSIONALISM").value.todouble();
    }

    public double getLowArmyProfessionalismMinRange() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("LOW_ARMY_PROFESSIONALISM_MIN_RANGE").value.todouble();
    }

    public double getLowArmyProfessionalismMaxRange() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("LOW_ARMY_PROFESSIONALISM_MAX_RANGE").value.todouble();
    }

    public double getHighArmyProfessionalismMinRange() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("HIGH_ARMY_PROFESSIONALISM_MIN_RANGE").value.todouble();
    }

    public double getHighArmyProfessionalismMaxRange() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("HIGH_ARMY_PROFESSIONALISM_MAX_RANGE").value.todouble();
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(this.defines.get(Eu4Utils.DEFINE_GAME_KEY).get("START_DATE").value.toString(), ClausewitzUtils.DATE_FORMAT);
    }

    public LocalDate getEndDate() {
        return LocalDate.parse(this.defines.get(Eu4Utils.DEFINE_GAME_KEY).get("END_DATE").value.toString(), ClausewitzUtils.DATE_FORMAT);
    }

    public double getEstateAngryThreshold() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_ANGRY_THRESHOLD").value.todouble();
    }

    public double getEstateHappyThreshold() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_HAPPY_THRESHOLD").value.todouble();
    }

    public double getEstateInfluenceLevel1() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_INFLUENCE_LEVEL_1").value.todouble();
    }

    public double getEstateInfluenceLevel2() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_INFLUENCE_LEVEL_2").value.todouble();
    }

    public double getEstateInfluenceLevel3() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_INFLUENCE_LEVEL_3").value.todouble();
    }

    public double getEstateInfluencePerDev() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_INFLUENCE_PER_DEV").value.todouble();
    }

    public double getEstateMaxInfluenceFromDev() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("ESTATE_MAX_INFLUENCE_FROM_DEV").value.todouble();
    }

    public double getIdeaToTech() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("IDEA_TO_TECH").value.todouble();
    }

    public double getNeighbourBonus() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NEIGHBOURBONUS").value.todouble();
    }

    public double getNeighbourBonusCap() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NEIGHBOURBONUS_CAP").value.todouble();
    }

    public double getSpyNetworkTechEffect() {
        return this.defines.get(Eu4Utils.DEFINE_DIPLOMACY_KEY).get("SPY_NETWORK_TECH_EFFECT").value.todouble();
    }

    public double getSpyNetworkTechEffectMax() {
        return this.defines.get(Eu4Utils.DEFINE_DIPLOMACY_KEY).get("SPY_NETWORK_TECH_EFFECT_MAX").value.todouble();
    }

    public List<Government> getGovernments() {
        return this.governments.keySet()
                               .stream()
                               .sorted(Comparator.comparing(Government::getLocalizedName, Eu4Utils.COLLATOR))
                               .collect(Collectors.toList());
    }

    public Government getGovernment(String name) {
        if (name == null) {
            return null;
        }

        for (Government government : this.governments.keySet()) {
            if (government.getName().equalsIgnoreCase(name)) {
                return government;
            }
        }

        return null;
    }

    public Set<GovernmentRank> getGovernmentRanks() {
        return this.governmentRanks.keySet();
    }

    public GovernmentRank getGovernmentRank(int level) {
        return this.governmentRanks.keySet().stream().filter(governmentRank -> governmentRank.getLevel() == level).findFirst().orElse(null);
    }

    public Set<GovernmentName> getGovernmentNames() {
        return this.governmentNames.keySet();
    }

    public GovernmentName getGovernmentName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (GovernmentName governmentName : this.governmentNames.keySet()) {
            if (governmentName.getName().equalsIgnoreCase(name)) {
                return governmentName;
            }
        }

        return null;
    }

    public Set<GovernmentReform> getGovernmentReforms() {
        return this.governmentReforms.keySet();
    }

    public GovernmentReform getGovernmentReform(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (GovernmentReform governmentReform : this.governmentReforms.keySet()) {
            if (governmentReform.getName().equalsIgnoreCase(name)) {
                return governmentReform;
            }
        }

        return null;
    }

    public Set<Unit> getUnits() {
        return this.units.keySet();
    }

    public Unit getUnit(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (Unit unit : this.units.keySet()) {
            if (unit.getName().equalsIgnoreCase(name)) {
                return unit;
            }
        }

        return null;
    }

    public Set<Area> getAreas() {
        return this.areas.keySet();
    }

    public Area getArea(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (Area area : this.areas.keySet()) {
            if (area.getName().equalsIgnoreCase(name)) {
                return area;
            }
        }

        return null;
    }

    public Set<Region> getRegions() {
        return this.regions.keySet();
    }

    public Region getRegion(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (Region region : this.regions.keySet()) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }

        return null;
    }

    public Set<SuperRegion> getSuperRegions() {
        return this.superRegions.keySet();
    }

    public SuperRegion getSuperRegion(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (SuperRegion superRegion : this.superRegions.keySet()) {
            if (superRegion.getName().equalsIgnoreCase(name)) {
                return superRegion;
            }
        }

        return null;
    }

    public Set<TechGroup> getTechGroups() {
        return this.techGroups.keySet();
    }

    public TechGroup getTechGroup(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (TechGroup techGroup : this.techGroups.keySet()) {
            if (techGroup.getName().equalsIgnoreCase(name)) {
                return techGroup;
            }
        }

        return null;
    }

    public List<Advisor> getAdvisors() {
        return new ArrayList<>(this.advisors.keySet());
    }

    public Advisor getAdvisor(String name) {
        if (name == null) {
            return null;
        }

        for (Advisor advisor : this.advisors.keySet()) {
            if (advisor.getName().equalsIgnoreCase(name)) {
                return advisor;
            }
        }

        return null;
    }

    public List<IdeaGroup> getIdeaGroups() {
        return new ArrayList<>(this.ideaGroups.keySet());
    }

    public IdeaGroup getIdeaGroup(String name) {
        if (name == null) {
            return null;
        }

        for (IdeaGroup ideaGroup : this.ideaGroups.keySet()) {
            if (ideaGroup.getName().equalsIgnoreCase(name)) {
                return ideaGroup;
            }
        }

        return null;
    }

    public List<CasusBelli> getCasusBelli() {
        return new ArrayList<>(this.casusBelli.keySet());
    }

    public CasusBelli getCasusBelli(String name) {
        if (name == null) {
            return null;
        }

        for (CasusBelli cb : this.casusBelli.keySet()) {
            if (cb.getName().equalsIgnoreCase(name)) {
                return cb;
            }
        }

        return null;
    }

    public List<ColonialRegion> getColonialRegions() {
        return new ArrayList<>(this.colonialRegions.keySet());
    }

    public ColonialRegion getColonialRegion(String name) {
        if (name == null) {
            return null;
        }

        for (ColonialRegion colonialRegion : this.colonialRegions.keySet()) {
            if (colonialRegion.getName().equalsIgnoreCase(name)) {
                return colonialRegion;
            }
        }

        return null;
    }

    public List<TradeCompany> getTradeCompanies() {
        return new ArrayList<>(this.tradeCompanies.keySet());
    }

    public TradeCompany getTradeCompany(String name) {
        if (name == null) {
            return null;
        }

        for (TradeCompany tradeCompany : this.tradeCompanies.keySet()) {
            if (tradeCompany.getName().equalsIgnoreCase(name)) {
                return tradeCompany;
            }
        }

        return null;
    }

    public List<SubjectType> getSubjectTypes() {
        return new ArrayList<>(this.subjectTypes.keySet());
    }

    public SubjectType getSubjectType(String name) {
        if (name == null) {
            return null;
        }

        for (SubjectType subjectType : this.subjectTypes.keySet()) {
            if (subjectType.getName().equalsIgnoreCase(name)) {
                return subjectType;
            }
        }

        return null;
    }

    public List<FetishistCult> getFetishistCults() {
        return new ArrayList<>(this.fetishistCults.keySet());
    }

    public FetishistCult getFetishistCult(String name) {
        if (name == null) {
            return null;
        }

        for (FetishistCult fetishistCult : this.fetishistCults.keySet()) {
            if (fetishistCult.getName().equalsIgnoreCase(name)) {
                return fetishistCult;
            }
        }

        return null;
    }

    public List<ChurchAspect> getChurchAspects() {
        return new ArrayList<>(this.churchAspects.keySet());
    }

    public ChurchAspect getChurchAspect(String name) {
        if (name == null) {
            return null;
        }

        for (ChurchAspect churchAspect : this.churchAspects.keySet()) {
            if (churchAspect.getName().equalsIgnoreCase(name)) {
                return churchAspect;
            }
        }

        return null;
    }

    public List<MissionTree> getMissionTrees() {
        return new ArrayList<>(this.missionTrees.keySet());
    }

    public MissionTree getMissionTree(String name) {
        if (name == null) {
            return null;
        }

        for (MissionTree missionTree : this.missionTrees.keySet()) {
            if (missionTree.getName().equalsIgnoreCase(name)) {
                return missionTree;
            }
        }

        return null;
    }

    public Mission getMission(String name) {
        if (name == null) {
            return null;
        }

        for (MissionTree missionTree : this.missionTrees.keySet()) {
            if (missionTree.getMissions().containsKey(name)) {
                return missionTree.getMissions().get(name);
            }
        }

        return null;
    }

    public List<EstatePrivilege> getEstatePrivileges() {
        return new ArrayList<>(this.estatePrivileges.keySet());
    }

    public EstatePrivilege getEstatePrivilege(String name) {
        if (name == null) {
            return null;
        }

        for (EstatePrivilege estatePrivilege : this.estatePrivileges.keySet()) {
            if (estatePrivilege.getName().equalsIgnoreCase(name)) {
                return estatePrivilege;
            }
        }

        return null;
    }

    public List<Estate> getEstates() {
        return new ArrayList<>(this.estates.keySet());
    }

    public Estate getEstate(String name) {
        if (name == null) {
            return null;
        }

        for (Estate estate : this.estates.keySet()) {
            if (estate.getName().equalsIgnoreCase(name)) {
                return estate;
            }
        }

        return null;
    }

    public Set<Technology> getTechnologies(Power power) {
        return this.technologies.get(power).keySet();
    }

    public Technology getTechnology(Power power, int i) {
        return new ArrayList<>(this.technologies.get(power).keySet()).get(i);
    }

    public Set<ProfessionalismModifier> getProfessionalismModifiers() {
        return this.professionalismModifiers.keySet();
    }

    public List<RulerPersonality> getRulerPersonalities() {
        return new ArrayList<>(this.rulerPersonalities.keySet());
    }

    public RulerPersonality getRulerPersonality(String name) {
        if (name == null) {
            return null;
        }

        for (RulerPersonality personality : this.rulerPersonalities.keySet()) {
            if (personality.getName().equalsIgnoreCase(name)) {
                return personality;
            }
        }

        return null;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(this.investments.keySet());
    }

    public Investment getInvestment(String name) {
        if (name == null) {
            return null;
        }

        for (Investment investment : this.investments.keySet()) {
            if (investment.getName().equalsIgnoreCase(name)) {
                return investment;
            }
        }

        return null;
    }

    public List<Policy> getPolicies() {
        return new ArrayList<>(this.policies.keySet());
    }

    public Policy getPolicy(String name) {
        if (name == null) {
            return null;
        }

        for (Policy policy : this.policies.keySet()) {
            if (policy.getName().equalsIgnoreCase(name)) {
                return policy;
            }
        }

        return null;
    }

    public List<Hegemon> getHegemons() {
        return new ArrayList<>(this.hegemons.keySet());
    }

    public Hegemon getHegemon(String name) {
        if (name == null) {
            return null;
        }

        for (Hegemon hegemon : this.hegemons.keySet()) {
            if (hegemon.getName().equalsIgnoreCase(name)) {
                return hegemon;
            }
        }

        return null;
    }

    public List<Faction> getFactions() {
        return new ArrayList<>(this.factions.keySet());
    }

    public Faction getFaction(String name) {
        if (name == null) {
            return null;
        }

        for (Faction faction : this.factions.keySet()) {
            if (faction.getName().equalsIgnoreCase(name)) {
                return faction;
            }
        }

        return null;
    }

    public List<Age> getAges() {
        return new ArrayList<>(this.ages.keySet());
    }

    public Age getAge(String name) {
        if (name == null) {
            return null;
        }

        for (Age age : this.ages.keySet()) {
            if (age.getName().equalsIgnoreCase(name)) {
                return age;
            }
        }

        return null;
    }

    public AgeAbility getAgeAbility(String name) {
        if (name == null) {
            return null;
        }

        return this.ages.keySet().stream()
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

        return this.ages.keySet().stream()
                        .map(Age::getObjectives)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(ageObjective -> ageObjective.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public Set<DefenderOfFaith> getDefenderOfFaith() {
        return this.defenderOfFaith.keySet();
    }

    public Set<CenterOfTrade> getCentersOfTrade() {
        return this.centersOfTrade.keySet();
    }

    public List<Fervor> getFervors() {
        return new ArrayList<>(this.fervors.keySet());
    }

    public Fervor getFervor(String name) {
        if (name == null) {
            return null;
        }

        for (Fervor fervor : this.fervors.keySet()) {
            if (fervor.getName().equalsIgnoreCase(name)) {
                return fervor;
            }
        }

        return null;
    }

    public List<GreatProject> getGreatProjects() {
        return new ArrayList<>(this.greatProjects.keySet());
    }

    public GreatProject getGreatProject(String name) {
        if (name == null) {
            return null;
        }

        for (GreatProject greatProject : this.greatProjects.keySet()) {
            if (greatProject.getName().equalsIgnoreCase(name)) {
                return greatProject;
            }
        }

        return null;
    }

    public List<HolyOrder> getHolyOrders() {
        return new ArrayList<>(this.holyOrders.keySet());
    }

    public HolyOrder getHolyOrder(String name) {
        if (name == null) {
            return null;
        }

        for (HolyOrder holyOrder : this.holyOrders.keySet()) {
            if (holyOrder.getName().equalsIgnoreCase(name)) {
                return holyOrder;
            }
        }

        return null;
    }

    public List<Isolationism> getIsolationisms() {
        return new ArrayList<>(this.isolationisms.keySet());
    }

    public Isolationism getIsolationism(Integer level) {
        if (level == null) {
            return null;
        }

        for (Isolationism isolationism : this.isolationisms.keySet()) {
            if (isolationism.getIsolationValue() == level) {
                return isolationism;
            }
        }

        return null;
    }

    public List<NativeAdvancements> getNativeAdvancements() {
        return new ArrayList<>(this.nativeAdvancements.keySet());
    }

    public NativeAdvancements getNativeAdvancements(String name) {
        if (name == null) {
            return null;
        }

        for (NativeAdvancements advancements : this.nativeAdvancements.keySet()) {
            if (advancements.getName().equalsIgnoreCase(name)) {
                return advancements;
            }
        }

        return null;
    }

    public List<NavalDoctrine> getNavalDoctrines() {
        return new ArrayList<>(this.navalDoctrines.keySet());
    }

    public NavalDoctrine getNavalDoctrine(String name) {
        if (name == null) {
            return null;
        }

        for (NavalDoctrine navalDoctrine : this.navalDoctrines.keySet()) {
            if (navalDoctrine.getName().equalsIgnoreCase(name)) {
                return navalDoctrine;
            }
        }

        return null;
    }

    public List<ParliamentIssue> getParliamentIssues() {
        return new ArrayList<>(this.parliamentIssues.keySet());
    }

    public ParliamentIssue getParliamentIssue(String name) {
        if (name == null) {
            return null;
        }

        for (ParliamentIssue parliamentIssue : this.parliamentIssues.keySet()) {
            if (parliamentIssue.getName().equalsIgnoreCase(name)) {
                return parliamentIssue;
            }
        }

        return null;
    }

    public List<PersonalDeity> getPersonalDeities() {
        return new ArrayList<>(this.personalDeities.keySet());
    }

    public PersonalDeity getPersonalDeity(String name) {
        if (name == null) {
            return null;
        }

        for (PersonalDeity personalDeity : this.personalDeities.keySet()) {
            if (personalDeity.getName().equalsIgnoreCase(name)) {
                return personalDeity;
            }
        }

        return null;
    }

    public List<ReligiousReforms> getReligiousReforms() {
        return new ArrayList<>(this.religiousReforms.keySet());
    }

    public ReligiousReforms getReligiousReforms(String name) {
        if (name == null) {
            return null;
        }

        for (ReligiousReforms reforms : this.religiousReforms.keySet()) {
            if (reforms.getName().equalsIgnoreCase(name)) {
                return reforms;
            }
        }

        return null;
    }

    public Set<CrownLandBonus> getCrownLandBonuses() {
        return this.crownLandBonuses.keySet();
    }

    public List<StateEdict> getStateEdicts() {
        return new ArrayList<>(this.stateEdicts.keySet());
    }

    public StateEdict getStateEdict(String name) {
        if (name == null) {
            return null;
        }

        for (StateEdict stateEdict : this.stateEdicts.keySet()) {
            if (stateEdict.getName().equalsIgnoreCase(name)) {
                return stateEdict;
            }
        }

        return null;
    }

    public List<TradePolicy> getTradePolicies() {
        return new ArrayList<>(this.tradePolicies.keySet());
    }

    public TradePolicy getTradePolicy(String name) {
        if (name == null) {
            return null;
        }

        for (TradePolicy tradePolicy : this.tradePolicies.keySet()) {
            if (tradePolicy.getName().equalsIgnoreCase(name)) {
                return tradePolicy;
            }
        }

        return null;
    }

    public List<StaticModifier> getStaticModifiers() {
        return new ArrayList<>(this.staticModifiers.keySet());
    }

    public StaticModifier getStaticModifier(String name) {
        if (name == null) {
            return null;
        }

        for (StaticModifier staticModifier : this.staticModifiers.keySet()) {
            if (staticModifier.getName().equalsIgnoreCase(name)) {
                return staticModifier;
            }
        }

        return null;
    }

    public List<EventModifier> getEventModifiers() {
        return new ArrayList<>(this.eventModifiers.keySet());
    }

    public EventModifier getEventModifier(String name) {
        if (name == null) {
            return null;
        }

        for (EventModifier eventModifier : this.eventModifiers.keySet()) {
            if (eventModifier.getName().equalsIgnoreCase(name)) {
                return eventModifier;
            }
        }

        return null;
    }

    public List<TriggeredModifier> getProvinceTriggeredModifiers() {
        return new ArrayList<>(this.provinceTriggeredModifiers.keySet());
    }

    public TriggeredModifier getProvinceTriggeredModifier(String name) {
        if (name == null) {
            return null;
        }

        for (TriggeredModifier triggeredModifier : this.provinceTriggeredModifiers.keySet()) {
            if (triggeredModifier.getName().equalsIgnoreCase(name)) {
                return triggeredModifier;
            }
        }

        return null;
    }

    public List<TriggeredModifier> getTriggeredModifiers() {
        return new ArrayList<>(this.triggeredModifiers.keySet());
    }

    public TriggeredModifier getTriggeredModifier(String name) {
        if (name == null) {
            return null;
        }

        for (TriggeredModifier triggeredModifier : this.triggeredModifiers.keySet()) {
            if (triggeredModifier.getName().equalsIgnoreCase(name)) {
                return triggeredModifier;
            }
        }

        return null;
    }

    public GameModifier getModifier(String modifier) {
        modifier = ClausewitzUtils.removeQuotes(modifier).toLowerCase();

        return Eu4Utils.coalesce(modifier, this::getStaticModifier, this::getParliamentIssue, this::getEventModifier, this::getProvinceTriggeredModifier,
                                 this::getTriggeredModifier);
    }

    private void readMods(List<String> modsEnabled) {
        this.filesNode = new TreeNode<>(null, new FileNode(Paths.get(this.gameFolderPath)), FileNode::getChildren);

        if (CollectionUtils.isNotEmpty(modsEnabled)) {
            Map<Path, List<String>> mods = modsEnabled.stream()
                                                      .map(ClausewitzUtils::removeQuotes)
                                                      .map(s -> s.replaceAll("^mod/", ""))
                                                      .map(s -> this.modFolderPath + File.separator + s)
                                                      .map(File::new)
                                                      .filter(File::exists)
                                                      .filter(File::canRead)
                                                      .map(file -> ClausewitzParser.parse(file, 0))
                                                      .filter(Objects::nonNull)
                                                      .collect(Collectors.toMap(item -> new File(ClausewitzUtils.removeQuotes(item.getVarAsString("path"))),
                                                                                //Compare with path so replace with system separator
                                                                                item -> item.getVars("replace_path")
                                                                                            .stream()
                                                                                            .map(ClausewitzVariable::getValue)
                                                                                            .map(ClausewitzUtils::removeQuotes)
                                                                                            .map(s -> Path.of(s).toString())
                                                                                            .collect(Collectors.toList())))
                                                      .entrySet()
                                                      .stream()
                                                      .filter(Objects::nonNull)
                                                      .filter(entry -> entry.getKey().exists() && entry.getKey().canRead())
                                                      .collect(Collectors.toMap(entry -> entry.getKey().toPath(), Map.Entry::getValue));

            mods.forEach((path, replacePaths) -> { //This technique replace only folders, so don't check for files
                this.filesNode.removeChildrenIf(fileNode -> fileNode.getPath().toFile().isDirectory()
                                                            && replacePaths.contains(fileNode.getRelativePath().toString()));
                this.filesNode.merge(new TreeNode<>(null, new FileNode(path), FileNode::getChildren));
            });
        }
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

                            this.localisations.put(key, value.substring(start, end).trim());
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
                    ClausewitzItem rootItem = ClausewitzParser.parse(path.toFile(), 0, ClausewitzUtils.CHARSET);
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
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), ClausewitzUtils.CHARSET)) {
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
                ClausewitzItem provinceMapItem = ClausewitzParser.parse(provincesMapFile, 0, StandardCharsets.UTF_8);
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
                ClausewitzItem climateItem = ClausewitzParser.parse(climateFile, 0, StandardCharsets.UTF_8);
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
                ClausewitzItem continentsItem = ClausewitzParser.parse(continentFile, 0, StandardCharsets.UTF_8);

                this.continents = continentsItem.getListsNot("island_check_provinces")
                                                .stream()
                                                .map(Continent::new)
                                                .collect(Collectors.toMap(Continent::getName, Function.identity(), (a, b) -> a, LinkedHashMap::new));

                this.continents.values().forEach(continent -> {
                    continent.setLocalizedName(this.getLocalisation(continent.getName()));
                    continent.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setContinent(continent));
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
        getPaths(this.commonFolderPath + File.separator + "cultures",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem cultureGroupsItem = ClausewitzParser.parse(path.toFile(), 0, ClausewitzUtils.CHARSET);
                    this.cultureGroups.putAll(cultureGroupsItem.getChildren()
                                                               .stream()
                                                               .map(CultureGroup::new)
                                                               .collect(Collectors.toMap(AbstractCulture::getName, Function.identity())));
                });

        this.cultureGroups.values().forEach(cultureGroup -> {
            cultureGroup.setLocalizedName(this.getLocalisation(cultureGroup.getName()));
            cultureGroup.getCultures().forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
        });
    }

    private void readReligion() {
        this.religionGroups = new HashMap<>();
        getPaths(this.commonFolderPath + File.separator + "religions",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem religionGroupsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    this.religionGroups.putAll(religionGroupsItem.getChildren()
                                                                 .stream()
                                                                 .map(ReligionGroup::new)
                                                                 .collect(Collectors.toMap(ReligionGroup::getName, Function.identity())));
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
        this.institutions = new HashMap<>();
        AtomicInteger i = new AtomicInteger();

        getPaths(this.commonFolderPath + File.separator + "institutions",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    this.institutions.putAll(institutionsItem.getChildren()
                                                             .stream()
                                                             .map(child -> new Institution(child, i.getAndIncrement()))
                                                             .collect(Collectors.toMap(Institution::getName, Function.identity())));
                });

        this.institutions.values().forEach(institution -> institution.setLocalizedName(this.getLocalisation(institution.getName())));
    }

    private void readTradeGoods() {
        this.tradeGoods = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "tradegoods",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    this.tradeGoods.putAll(tradeGoodsItem.getChildren()
                                                         .stream()
                                                         .map(TradeGood::new)
                                                         .collect(Collectors.toMap(TradeGood::getName, Function.identity())));
                });

        getPaths(this.commonFolderPath + File.separator + "prices",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    pricesItem.getChildren().forEach(priceItem -> {
                        if (this.tradeGoods.containsKey(priceItem.getName())) {
                            this.tradeGoods.get(priceItem.getName()).setPriceItem(priceItem);
                        }
                    });
                });

        this.tradeGoods.values().forEach(tradeGood -> tradeGood.setLocalizedName(this.getLocalisation(tradeGood.getName())));
    }

    private void readBuildings() {
        this.buildings = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "buildings",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem buildingsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    this.buildings.putAll(buildingsItem.getChildrenNot("manufactory")
                                                       .stream()
                                                       .map(item -> new Building(item, this))
                                                       .collect(Collectors.toMap(Building::getName, Function.identity())));

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

        getPaths(this.commonFolderPath + File.separator + "imperial_reforms",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem imperialReformsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    this.imperialReforms.putAll(imperialReformsItem.getChildren()
                                                                   .stream()
                                                                   .map(item -> new ImperialReform(item, this))
                                                                   .collect(Collectors.toMap(ImperialReform::getName, Function.identity())));
                });

        this.imperialReforms.values().forEach(imperialReform -> imperialReform.setLocalizedName(this.getLocalisation(imperialReform.getName() + "_title")));
    }

    private void readDecrees() {
        this.decrees = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "imperial_reforms",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem decreesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    decreesItem.getChildren().forEach(item -> this.decrees.put(new Decree(item), path));
                });

        this.decrees.keySet().forEach(saveDecree -> saveDecree.setLocalizedName(this.getLocalisation(saveDecree.getName() + "_title")));
    }

    private void readGoldenBulls() {
        this.goldenBulls = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "golden_bulls",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem goldenBullsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    goldenBullsItem.getChildren().forEach(item -> this.goldenBulls.put(new GoldenBull(item), path));
                });

        this.goldenBulls.keySet().forEach(bull -> bull.setLocalizedName(this.getLocalisation(bull.getName())));
    }

    private void readEvents() {
        this.events = new ConcurrentHashMap<>();

        getPaths("events",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem eventsItem = ClausewitzParser.parse(path.toFile(), 0);
                    eventsItem.getChildren().forEach(item -> this.events.put(new Event(item), path));
                });

        this.events.keySet()
                   .parallelStream()
                   .forEach(event -> event.setLocalizedName(this.getLocalisation(ClausewitzUtils.removeQuotes(event.getTitle()))));
    }

    private void readGovernments() {
        this.governments = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "governments",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    governmentsItem.getChildrenNot("pre_dharma_mapping").forEach(item -> this.governments.put(new Government(item, this), path));
                });

        this.governments.keySet().forEach(government -> government.setLocalizedName(this.getLocalisation(government.getBasicReform())));
    }

    private void readGovernmentRanks() {
        this.governmentRanks = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "government_ranks",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem governmentRanksItem = ClausewitzParser.parse(path.toFile(), 0);
                    governmentRanksItem.getChildren().forEach(item -> this.governmentRanks.put(new GovernmentRank(item), path));
                });
    }

    private void readGovernmentNames() {
        this.governmentNames = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "government_names",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    governmentsItem.getChildren().forEach(item -> this.governmentNames.put(new GovernmentName(item, this), path));
                });
    }

    private void readGovernmentReforms() {
        this.governmentReforms = new HashMap<>();

        Map<ClausewitzItem, Path> reformsItems = getPaths(this.commonFolderPath + File.separator + "government_reforms",
                                                          fileNode -> Files.isRegularFile(fileNode.getPath()))
                .collect(Collectors.toMap(path -> ClausewitzParser.parse(path.toFile(), 0), Function.identity()));

        AtomicReference<GovernmentReform> defaultReform = new AtomicReference<>();

        reformsItems.keySet().stream().filter(item -> item.hasChild("defaults_reform")).findFirst().ifPresent(item -> {
            defaultReform.set(new GovernmentReform(item.getChild("defaults_reform"), this, null));
        });

        reformsItems.forEach((key, value) -> key.getChildrenNot("defaults_reform")
                                                .forEach(item -> this.governmentReforms.put(new GovernmentReform(item, this, defaultReform.get()), value)));

        this.governmentReforms.keySet().forEach(governmentReform -> governmentReform.setLocalizedName(this.getLocalisation(governmentReform.getName())));
    }

    private void readUnits() {
        this.units = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "units",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem unitItem = ClausewitzParser.parse(path.toFile(), 0);
                    unitItem.setName(FilenameUtils.removeExtension(path.getFileName().toString()));
                    this.units.put(new Unit(unitItem, this::getLocalisation), path);
                });
    }

    private void readAreas() {
        this.areas = new HashMap<>();
        File areasFile = getAbsoluteFile(this.mapFolderPath + File.separator + "area.txt");

        if (areasFile != null && areasFile.canRead()) {
            ClausewitzItem areasItem = ClausewitzParser.parse(areasFile, 0);

            areasItem.getLists().forEach(item -> this.areas.put(new Area(item), areasFile.toPath()));
            areasItem.getChildren().forEach(item -> this.areas.put(new Area(item), areasFile.toPath()));
        }

        this.areas.keySet().forEach(area -> area.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setArea(area)));
    }

    private void readRegions() {
        this.regions = new HashMap<>();
        File regionsFile = getAbsoluteFile(this.mapFolderPath + File.separator + "region.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            regionsItem.getChildren().forEach(item -> this.regions.put(new Region(item, this), regionsFile.toPath()));
        }

        this.regions.keySet().stream().filter(region -> region.getAreas() != null).forEach(region -> region.getAreas().forEach(area -> area.setRegion(region)));
    }

    private void readSuperRegions() {
        this.superRegions = new HashMap<>();
        File regionsFile = getAbsoluteFile(this.mapFolderPath + File.separator + "superregion.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            regionsItem.getLists().forEach(item -> this.superRegions.put(new SuperRegion(item, this), regionsFile.toPath()));
        }

        this.superRegions.keySet()
                         .stream()
                         .filter(superRegion -> superRegion.getRegions() != null)
                         .forEach(superRegion -> superRegion.getRegions().forEach(region -> region.setSuperRegion(superRegion)));
    }

    private void readTechGroups() {
        this.techGroups = new HashMap<>();
        File techGroupsFile = getAbsoluteFile(this.commonFolderPath + File.separator + "technology.txt");

        if (techGroupsFile != null && techGroupsFile.canRead()) {
            ClausewitzItem techGroupsItem = ClausewitzParser.parse(techGroupsFile, 0);
            techGroupsItem.getChild("groups").getChildren().forEach(item -> this.techGroups.put(new TechGroup(item), techGroupsFile.toPath()));
        }

        this.techGroups.keySet().forEach(techGroup -> techGroup.setLocalizedName(this.getLocalisation(techGroup.getName())));
    }

    private void readAdvisors() {
        this.advisors = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "advisortypes",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    advisorsItem.getChildren().forEach(item -> this.advisors.put(new Advisor(item), path));
                });

        this.advisors.keySet().forEach(advisor -> advisor.setLocalizedName(this.getLocalisation(advisor.getName())));
    }

    private void readIdeaGroups() {
        this.ideaGroups = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ideas",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem ideasItem = ClausewitzParser.parse(path.toFile(), 0);
                    ideasItem.getChildren().forEach(item -> this.ideaGroups.put(new IdeaGroup(item), path));
                });

        this.ideaGroups.keySet().forEach(ideaGroup -> ideaGroup.setLocalizedName(this.getLocalisation(ideaGroup.getName())));
    }

    private void readCasusBelli() {
        this.casusBelli = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "cb_types",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem cbItem = ClausewitzParser.parse(path.toFile(), 0);
                    cbItem.getChildren().forEach(item -> this.casusBelli.put(new CasusBelli(item), path));
                });

        this.casusBelli.keySet().forEach(casusBelli -> casusBelli.setLocalizedName(this.getLocalisation(casusBelli.getName())));
    }

    private void readColonialRegions() {
        this.colonialRegions = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "colonial_regions",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem colonialRegionsItem = ClausewitzParser.parse(path.toFile(), 0);
                    colonialRegionsItem.getChildren().forEach(item -> this.colonialRegions.put(new ColonialRegion(item, this), path));
                });

        this.colonialRegions.keySet().forEach(colonialRegion -> colonialRegion.setLocalizedName(this.getLocalisation(colonialRegion.getName())));
    }

    private void readTradeCompanies() {
        this.tradeCompanies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "trade_companies",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem tradeCompaniesItem = ClausewitzParser.parse(path.toFile(), 0);
                    tradeCompaniesItem.getChildren().forEach(item -> this.tradeCompanies.put(new TradeCompany(item), path));
                });

        this.tradeCompanies.keySet().forEach(tradeCompany -> tradeCompany.setLocalizedName(this.getLocalisation(tradeCompany.getName())));
    }

    private void readSubjectTypes() {
        this.subjectTypes = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "subject_types",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem subjectTypesItem = ClausewitzParser.parse(path.toFile(), 0);
                    subjectTypesItem.getChildren().forEach(item -> this.subjectTypes.put(new SubjectType(item, this.subjectTypes.keySet()), path));
                });

        this.subjectTypes.entrySet().removeIf(entry -> StringUtils.isBlank(entry.getKey().getSprite()));
        this.subjectTypes.keySet().forEach(subjectType -> subjectType.setLocalizedName(this.getLocalisation(subjectType.getName() + "_title")));
    }

    private void readFetishistCults() {
        this.fetishistCults = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "fetishist_cults",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem cultsItem = ClausewitzParser.parse(path.toFile(), 0);
                    cultsItem.getChildren().forEach(item -> this.fetishistCults.put(new FetishistCult(item), path));
                });

        this.fetishistCults.keySet().forEach(fetishistCult -> fetishistCult.setLocalizedName(this.getLocalisation(fetishistCult.getName())));
    }

    private void readChurchAspects() {
        this.churchAspects = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "church_aspects",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem aspectsItems = ClausewitzParser.parse(path.toFile(), 0);
                    aspectsItems.getChildren().forEach(item -> this.churchAspects.put(new ChurchAspect(item), path));
                });

        this.churchAspects.keySet().forEach(fetishistCult -> fetishistCult.setLocalizedName(this.getLocalisation(fetishistCult.getName())));
    }

    private void readMissionTrees() {
        this.missionTrees = new HashMap<>();

        getPaths(this.missionsFolderPath,
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    advisorsItem.getChildren().forEach(item -> this.missionTrees.put(new MissionTree(item, this), path));
                });

        this.missionTrees.keySet().forEach(missionTree -> {
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

        getPaths(this.commonFolderPath + File.separator + "estates_preload",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
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
        getPaths(this.commonFolderPath + File.separator + "estate_privileges",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem estatePrivilegesItem = ClausewitzParser.parse(path.toFile(), 0);
                    estatePrivilegesItem.getChildren().forEach(item -> this.estatePrivileges.put(new EstatePrivilege(item), path));
                });

        this.estatePrivileges.keySet().forEach(estatePrivilege -> estatePrivilege.setLocalizedName(this.getLocalisation(estatePrivilege.getName())));

        this.estates = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "estates",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem estatesItem = ClausewitzParser.parse(path.toFile(), 0);
                    estatesItem.getChildren().forEach(item -> this.estates.put(new Estate(item, modifierDefinitions.get(item.getName()), this), path));
                });

        this.estates.keySet().forEach(estate -> estate.setLocalizedName(this.getLocalisation(estate.getName())));
    }

    private void readTechnologies() {
        this.technologies = new EnumMap<>(Power.class);
        Map<Technology, Path> techs = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "technologies",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem techItem = ClausewitzParser.parse(path.toFile(), 0);

                    Power power = Power.byName(techItem.getVarAsString("monarch_power"));
                    Modifiers aheadOfTime = new Modifiers(techItem.getChild("ahead_of_time"));

                    techItem.getChildrenNot("ahead_of_time").forEach(item -> techs.put(new Technology(item, power, aheadOfTime), path));
                });

        this.technologies = techs.entrySet()
                                 .stream()
                                 .collect(Collectors.groupingBy(entry -> entry.getKey().getType(),
                                                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new)));
    }

    private void readRulerPersonalities() {
        this.rulerPersonalities = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ruler_personalities",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem rulerPersonalityItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    rulerPersonalityItem.getChildren().forEach(item -> this.rulerPersonalities.put(new RulerPersonality(item), path));
                });

        this.rulerPersonalities.keySet()
                               .forEach(rulerPersonality -> rulerPersonality.setLocalizedName(this.getLocalisation(rulerPersonality.getName())));
    }

    private void readProfessionalismModifiers() {
        this.professionalismModifiers = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "professionalism",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    advisorsItem.getChildren().forEach(item -> this.professionalismModifiers.put(new ProfessionalismModifier(item), path));
                });
    }

    private void readStaticModifiers() {
        this.staticModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "static_modifiers",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem modifiersItem = ClausewitzParser.parse(path.toFile(), 0);
                    modifiersItem.getChildrenNot("null_modifier").forEach(item -> {
                        if (StaticModifiers.value(item.getName()) != null) {
                            StaticModifiers.value(item.getName()).setModifiers(new Modifiers(item));
                            this.staticModifiers.put(new StaticModifier(item), path);
                        }
                    });
                });

        this.staticModifiers.keySet().forEach(staticModifier -> staticModifier.setLocalizedName(this.getLocalisation(staticModifier.getName())));
    }

    private void readInvestments() {
        this.investments = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "tradecompany_investments",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    investmentsItem.getChildren().forEach(item -> this.investments.put(new Investment(item, this), path));
                });

        this.investments.keySet().forEach(investment -> investment.setLocalizedName(this.getLocalisation(investment.getName())));
    }

    private void readPolicies() {
        this.policies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "policies",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    investmentsItem.getChildren().forEach(item -> this.policies.put(new Policy(item), path));
                });

        this.policies.keySet().forEach(policy -> policy.setLocalizedName(this.getLocalisation(policy.getName())));
    }

    private void readHegemons() {
        this.hegemons = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "hegemons",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    hegemonsItem.getChildren().forEach(item -> this.hegemons.put(new Hegemon(item), path));
                });

        this.hegemons.keySet().forEach(hegemon -> hegemon.setLocalizedName(this.getLocalisation(hegemon.getName())));
    }

    private void readFactions() {
        this.factions = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "factions",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    hegemonsItem.getChildren().forEach(item -> this.factions.put(new Faction(item), path));

                });

        this.factions.keySet().forEach(faction -> {
            faction.setLocalizedName(this.getLocalisation(faction.getName()));
            ModifiersUtils.addModifier(ClausewitzUtils.removeQuotes(faction.getName()) + "_influence", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        });
    }

    private void readAges() {
        this.ages = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "ages",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem agesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    agesItem.getChildren().forEach(item -> this.ages.put(new Age(item), path));
                });

        this.ages.keySet().forEach(age -> age.setLocalizedName(this.getLocalisation(age.getName())));
    }

    private void readDefenderOfFaith() {
        this.defenderOfFaith = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "defender_of_faith",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    defenderOfFaithItem.getChildren().forEach(item -> this.defenderOfFaith.put(new DefenderOfFaith(item), path));
                });
    }

    private void readCentersOfTrade() {
        this.centersOfTrade = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "centers_of_trade",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem centersOfTradeItem = ClausewitzParser.parse(path.toFile(), 0);
                    centersOfTradeItem.getChildren().forEach(item -> this.centersOfTrade.put(new CenterOfTrade(item), path));
                });
    }

    private void readFervors() {
        this.fervors = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "fervor",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem fervorsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    fervorsItem.getChildren().forEach(item -> this.fervors.put(new Fervor(item), path));
                });

        this.fervors.keySet().forEach(fervor -> fervor.setLocalizedName(this.getLocalisation(fervor.getName())));
    }

    private void readGreatProjects() {
        this.greatProjects = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "great_projects",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem greatProjectsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    greatProjectsItem.getChildren().forEach(item -> this.greatProjects.put(new GreatProject(item), path));
                });

        this.greatProjects.keySet().forEach(greatProject -> greatProject.setLocalizedName(this.getLocalisation(greatProject.getName())));
    }

    private void readHolyOrders() {
        this.holyOrders = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "holy_orders",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem holyOrdersItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                    holyOrdersItem.getChildren().forEach(item -> this.holyOrders.put(new HolyOrder(item), path));
                });

        this.holyOrders.keySet().forEach(holyOrder -> holyOrder.setLocalizedName(this.getLocalisation(holyOrder.getName())));
    }

    private void readIsolationism() {
        this.isolationisms = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "isolationism",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem isolationismItem = ClausewitzParser.parse(path.toFile(), 0);
                    isolationismItem.getChildren().forEach(item -> this.isolationisms.put(new Isolationism(item), path));
                });

        this.isolationisms.keySet().forEach(isolationism -> isolationism.setLocalizedName(this.getLocalisation(isolationism.getName())));
    }

    private void readNativeAdvancements() {
        this.nativeAdvancements = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "native_advancement",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem nativeAdvancementItem = ClausewitzParser.parse(path.toFile(), 0);
                    nativeAdvancementItem.getChildren().forEach(item -> this.nativeAdvancements.put(new NativeAdvancements(item), path));
                });

        this.nativeAdvancements.keySet().forEach(advancements -> {
            advancements.setLocalizedName(this.getLocalisation(advancements.getName()));
            advancements.getNativeAdvancements()
                        .forEach(nativeAdvancement -> nativeAdvancement.setLocalizedName(this.getLocalisation(nativeAdvancement.getName())));
        });
    }

    private void readNavalDoctrine() {
        this.navalDoctrines = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "naval_doctrines",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem navalDoctrineItem = ClausewitzParser.parse(path.toFile(), 0);
                    navalDoctrineItem.getChildren().forEach(item -> this.navalDoctrines.put(new NavalDoctrine(item), path));
                });

        this.navalDoctrines.keySet().forEach(navalDoctrine -> navalDoctrine.setLocalizedName(this.getLocalisation(navalDoctrine.getName())));
    }

    private void readParliamentIssue() {
        this.parliamentIssues = new LinkedHashMap<>();

        getPaths(this.commonFolderPath + File.separator + "parliament_issues",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem parliamentIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    parliamentIssueItem.getChildren().forEach(item -> this.parliamentIssues.put(new ParliamentIssue(item), path));
                });

        this.parliamentIssues.keySet().forEach(parliamentIssue -> parliamentIssue.setLocalizedName(this.getLocalisation(parliamentIssue.getName())));
    }

    private void readPersonalDeities() {
        this.personalDeities = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "personal_deities",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem personalIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    personalIssueItem.getChildren().forEach(item -> this.personalDeities.put(new PersonalDeity(item), path));
                });

        this.personalDeities.keySet().forEach(personalIssue -> personalIssue.setLocalizedName(this.getLocalisation(personalIssue.getName())));
    }

    private void readReligiousReforms() {
        this.religiousReforms = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "religious_reforms",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem religiousReformItem = ClausewitzParser.parse(path.toFile(), 0);
                    religiousReformItem.getChildren().forEach(item -> this.religiousReforms.put(new ReligiousReforms(item), path));
                });

        this.religiousReforms.keySet().forEach(reforms -> {
            reforms.setLocalizedName(this.getLocalisation(reforms.getName()));
            reforms.getReforms().forEach(religiousReform -> religiousReform.setLocalizedName(this.getLocalisation(religiousReform.getName())));
        });
    }

    private void readCrownLandBonuses() {
        this.crownLandBonuses = new TreeMap<>();

        getPaths(this.commonFolderPath + File.separator + "estate_crown_land",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    defenderOfFaithItem.getChildren("bonus").forEach(item -> this.crownLandBonuses.put(new CrownLandBonus(item), path));
                });
    }

    private void readStateEdicts() {
        this.stateEdicts = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "state_edicts",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem stateEdictsItem = ClausewitzParser.parse(path.toFile(), 0);
                    stateEdictsItem.getChildren().forEach(item -> this.stateEdicts.put(new StateEdict(item), path));
                });

        this.stateEdicts.keySet().forEach(stateEdict -> stateEdict.setLocalizedName(this.getLocalisation(stateEdict.getName())));
    }

    private void readTradePolicies() {
        this.tradePolicies = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "trading_policies",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem tradePoliciesItem = ClausewitzParser.parse(path.toFile(), 0);
                    tradePoliciesItem.getChildren().forEach(item -> this.tradePolicies.put(new TradePolicy(item), path));

                });

        this.tradePolicies.keySet().forEach(tradePolicy -> tradePolicy.setLocalizedName(this.getLocalisation(tradePolicy.getName())));
    }

    private void readEventModifiers() {
        this.eventModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "event_modifiers",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem eventModifierItem = ClausewitzParser.parse(path.toFile(), 0);
                    eventModifierItem.getChildren().forEach(item -> this.eventModifiers.put(new EventModifier(item), path));
                });

        this.eventModifiers.keySet().forEach(eventModifier -> eventModifier.setLocalizedName(this.getLocalisation(eventModifier.getName())));
    }

    private void readProvinceTriggeredModifiers() {
        this.provinceTriggeredModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "province_triggered_modifiers",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem provinceTriggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    provinceTriggeredIssueItem.getChildren().forEach(item -> this.provinceTriggeredModifiers.put(new TriggeredModifier(item), path));
                });

        this.provinceTriggeredModifiers.keySet()
                                       .forEach(provinceTriggered -> provinceTriggered.setLocalizedName(this.getLocalisation(provinceTriggered.getName())));
    }

    private void readTriggeredModifiers() {
        this.triggeredModifiers = new HashMap<>();

        getPaths(this.commonFolderPath + File.separator + "triggered_modifiers",
                 fileNode -> Files.isRegularFile(fileNode.getPath()))
                .forEach(path -> {
                    ClausewitzItem triggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    triggeredIssueItem.getChildren().forEach(item -> this.triggeredModifiers.put(new TriggeredModifier(item), path));

                });

        this.triggeredModifiers.keySet().forEach(triggeredModifier -> triggeredModifier.setLocalizedName(this.getLocalisation(triggeredModifier.getName())));
    }
}
