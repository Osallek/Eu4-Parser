package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.common.LuaUtils;
import com.osallek.eu4parser.common.ModifierScope;
import com.osallek.eu4parser.common.ModifierType;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.model.Power;
import com.osallek.eu4parser.model.game.localisation.Eu4Language;
import com.osallek.eu4parser.model.save.country.Country;
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
import java.text.Collator;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
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
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private final Collator collator;

    private final String gameFolderPath;

    private final String mapFolderPath;

    private final String commonFolderPath;

    private final String gfxFolderPath;

    private final String localisationFolderPath;

    private final String interfaceFolderPath;

    private final String missionsFolderPath;

    private Map<Integer, Province> provinces;

    private Map<Integer, Province> provincesByColor;

    private Map<Continent, Path> continents;

    private Map<Path, List<CultureGroup>> cultureGroups;

    private Map<Path, List<ReligionGroup>> religionGroups;

    private Map<Path, SortedSet<Institution>> institutions;

    private Map<TradeGood, Map.Entry<Path, Path>> tradeGoods;

    private Map<Building, Path> buildings;

    private Map<String, String> localisations;

    private Map<String, SpriteType> spriteTypes;

    private Map<ImperialReform, Path> imperialReforms;

    private Map<Decree, Path> decrees;

    private Map<GoldenBull, Path> goldenBulls;

    private Map<Event, Path> events;

    private Map<Government, Path> governments;

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

    public Game(String gameFolderPath) throws IOException, ParseException {
        this.collator = Collator.getInstance();
        this.collator.setStrength(Collator.NO_DECOMPOSITION);

        this.gameFolderPath = gameFolderPath;
        this.mapFolderPath = this.gameFolderPath + File.separator + "map";
        this.commonFolderPath = this.gameFolderPath + File.separator + "common";
        this.gfxFolderPath = this.gameFolderPath + File.separator + "gfx";
        this.localisationFolderPath = this.gameFolderPath + File.separator + "localisation";
        this.interfaceFolderPath = this.gameFolderPath + File.separator + "interface";
        this.missionsFolderPath = this.gameFolderPath + File.separator + "missions";
        this.defines = LuaUtils.luaFileToMap(this.commonFolderPath + File.separator + "defines.lua");

        Instant start = Instant.now();

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
        readGovernments();
        readGovernmentNames();
        readGovernmentReforms();
        readUnits();
        readTechGroups();
        readAdvisors();
        readIdeaGroups();
        readCasusBelli();
        readTradeCompanies();
        readSubjectTypes();
        readFetishistCults();
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

    public Collator getCollator() {
        return collator;
    }

    public String getGameFolderPath() {
        return gameFolderPath;
    }

    public String getMapFolderPath() {
        return mapFolderPath;
    }

    public String getCommonFolderPath() {
        return commonFolderPath;
    }

    public String getGfxFolderPath() {
        return gfxFolderPath;
    }

    public String getLocalisationFolderPath() {
        return localisationFolderPath;
    }

    public String getInterfaceFolderPath() {
        return interfaceFolderPath;
    }

    public File getProvincesImage() {
        return new File(this.mapFolderPath + File.separator + "provinces.bmp");
    }

    public File getNormalCursorImage() {
        return new File(this.gfxFolderPath + File.separator + "cursors" + File.separator + "normal.png");
    }

    public File getSelectedCursorImage() {
        return new File(this.gfxFolderPath + File.separator + "cursors" + File.separator + "selected.png");
    }

    public File getGoldImage() {
        return getSpriteTypeImageFile("GFX_icon_gold");
    }

    public File getCountryFlagImage(Country country) {
        return country == null ? null : new File(this.gfxFolderPath + File.separator + "flags" + File.separator + country.getTag() + ".tga");
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
        return this.continents.keySet()
                              .stream()
                              .sorted(Comparator.comparing(Continent::getLocalizedName, this.collator))
                              .collect(Collectors.toList());
    }

    public Continent getContinent(String name) {
        if (name == null) {
            return null;
        }

        for (Continent continent : this.continents.keySet()) {
            if (continent.getName().equalsIgnoreCase(name)) {
                return continent;
            }
        }

        return null;
    }

    public Continent getContinent(int i) {
        return new ArrayList<>(this.continents.keySet()).get(i);
    }

    public String getLocalisation(String key) {
        return this.localisations.getOrDefault(key, key);
    }

    public String getLocalisationClean(String key) {
        if (key == null) {
            return null;
        }

        String localisationString = this.localisations.get(key);

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

        File file = new File(getGameFolderPath() + File.separator
                             + ClausewitzUtils.removeQuotes(spriteType.getTextureFile()));

        if (file.exists()) {
            return file;
        }

        //Fix some time files are not rightly registered (I don't know how the game loads them...)
        if (file.toString().endsWith(".tga")) {
            return new File(file.toString().replace(".tga", ".dds"));
        } else if (file.toString().endsWith(".dds")) {
            return new File(file.toString().replace(".dds", ".tga"));
        }

        return null;
    }

    public Collection<CultureGroup> getCultureGroups() {
        return this.cultureGroups.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Culture> getCultures() {
        return this.cultureGroups.values().stream()
                                 .flatMap(Collection::stream)
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .sorted(Comparator.comparing(Culture::getLocalizedName, collator))
                                 .collect(Collectors.toList());
    }

    public Culture getCulture(String name) {
        if (name == null) {
            return null;
        }

        return this.cultureGroups.values().stream()
                                 .flatMap(Collection::stream)
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .filter(culture -> culture.getName().equalsIgnoreCase(name))
                                 .findFirst()
                                 .orElse(null);
    }

    public Collection<ReligionGroup> getReligionGroups() {
        return this.religionGroups.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Religion> getReligions() {
        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .sorted(Comparator.comparing(Religion::getLocalizedName, this.collator))
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
                                .flatMap(Collection::stream)
                                .sorted(Comparator.comparingInt(Institution::getIndex))
                                .collect(Collectors.toList());
    }

    public Institution getInstitution(int i) {
        return getInstitutions().get(i);
    }

    public Institution getInstitution(String name) {
        if (name == null) {
            return null;
        }

        return this.institutions.values().stream()
                                .flatMap(Collection::stream)
                                .filter(institution -> institution.getName().equalsIgnoreCase(name))
                                .findFirst()
                                .orElse(null);
    }

    public List<TradeGood> getTradeGoods() {
        return this.tradeGoods.keySet()
                              .stream()
                              .sorted(Comparator.comparing(TradeGood::getLocalizedName, this.collator))
                              .collect(Collectors.toList());
    }

    public TradeGood getTradeGood(String name) {
        if (name == null) {
            return null;
        }

        for (TradeGood tradeGood : this.tradeGoods.keySet()) {
            if (tradeGood.getName().equalsIgnoreCase(name)) {
                return tradeGood;
            }
        }

        return null;
    }

    public TradeGood getTradeGood(int i) {
        return new ArrayList<>(this.tradeGoods.keySet()).get(i);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(this.buildings.keySet());
    }

    public Building getBuilding(String name) {
        if (name == null) {
            return null;
        }

        for (Building building : this.buildings.keySet()) {
            if (building.getName().equalsIgnoreCase(name)) {
                return building;
            }
        }

        return null;
    }

    public List<ImperialReform> getImperialReforms() {
        return new ArrayList<>(this.imperialReforms.keySet());
    }

    public ImperialReform getImperialReform(String name) {
        if (name == null) {
            return null;
        }

        for (ImperialReform imperialReform : this.imperialReforms.keySet()) {
            if (imperialReform.getName().equalsIgnoreCase(name)) {
                return imperialReform;
            }
        }

        return null;
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
                          .sorted(Comparator.comparing(Event::getLocalizedName, this.collator))
                          .collect(Collectors.toList());
    }

    public List<Event> getFireOnlyOnceEvents() {
        return this.events.keySet()
                          .stream()
                          .filter(Event::fireOnlyOnce)
                          .sorted(Comparator.comparing(Event::getLocalizedName, this.collator))
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
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("BANKRUPTCY_DURATION").value.toint();
    }

    public int getNbGreatPowers() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NUM_OF_GREAT_POWERS").value.toint();
    }

    public int getNomadDevelopmentScale() {
        return this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NOMAD_DEVELOPMENT_SCALE").value.toint();
    }

    public int getLargeColonialNationLimit() {
        return this.defines.get(Eu4Utils.DEFINE_ECONOMY_KEY).get("LARGE_COLONIAL_NATION_LIMIT").value.toint();
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

    public List<Government> getGovernments() {
        return this.governments.keySet()
                               .stream()
                               .sorted(Comparator.comparing(Government::getLocalizedName, this.collator))
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

    public void loadLocalisations() throws IOException {
        loadLocalisations(Eu4Language.getByLocale(Locale.getDefault()));
    }

    private void loadLocalisations(Eu4Language eu4Language) throws IOException {
        File localisationFolder = new File(this.localisationFolderPath);

        if (localisationFolder.canRead()) {
            File[] files = localisationFolder.listFiles((dir, fileName) -> fileName.endsWith(eu4Language.fileEndWith + ".yml"));
            if (files != null) {
                this.localisations = new HashMap<>();

                for (File file : files) {
                    if (Files.isRegularFile(file.toPath())) {
                        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
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
                        }
                    }
                }
            }
        }
    }

    private void readSpriteTypes() {
        File interfaceFolder = new File(this.interfaceFolderPath);

        if (interfaceFolder.canRead()) {
            try (Stream<Path> paths = Files.walk(interfaceFolder.toPath())) {
                this.spriteTypes = new HashMap<>();

                paths.filter(Files::isRegularFile)
                     .filter(path -> path.toString().endsWith(".gfx"))
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
            } catch (IOException e) {
            }
        }

    }

    private void readProvinces() throws IOException {
        File provincesDefinitionFile = new File(this.mapFolderPath + File.separator + "definition.csv");

        if (provincesDefinitionFile.canRead()) {
            this.provinces = new HashMap<>();
            this.provincesByColor = new HashMap<>();
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), ClausewitzUtils.CHARSET)) {
                String line;
                reader.readLine(); //Skip csv headers
                while ((line = reader.readLine()) != null) {
                    Province province = new Province(line.split(";"));
                    this.provinces.put(province.getId(), province);
                    this.provincesByColor.put(Eu4Utils.rgbToColor(province.getRed(), province.getGreen(), province.getBlue()), province);
                }
            }

            File provincesMapFile = new File(this.mapFolderPath + File.separator + "default.map");

            if (provincesMapFile.canRead()) {
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

            File climateFile = new File(this.mapFolderPath + File.separator + "climate.txt");

            if (climateFile.canRead()) {
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

            File continentFile = new File(this.mapFolderPath + File.separator + "continent.txt");

            if (continentFile.canRead()) {
                ClausewitzItem continentsItem = ClausewitzParser.parse(continentFile, 0, StandardCharsets.UTF_8);

                this.continents = new LinkedHashMap<>();
                continentsItem.getListsNot("island_check_provinces").forEach(item -> this.continents.put(new Continent(item), continentFile.toPath()));
                this.continents.keySet().forEach(continent -> {
                    continent.setLocalizedName(this.getLocalisation(continent.getName()));
                    continent.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setContinent(continent));
                });
            }

            if (getProvincesImage().canRead()) {
                BufferedImage provinceImage = ImageIO.read(getProvincesImage());

                for (int x = 0; x < provinceImage.getWidth(); x++) {
                    for (int y = 0; y < provinceImage.getHeight(); y++) {
                        int[] rgb = provinceImage.getRaster().getPixel(x, y, (int[]) null);
                        Province province = getProvinceByColor(rgb[0], rgb[1], rgb[2]);

                        if (province.isColonizable() && !province.isPort()) {
                            if (x > 0) {
                                int[] leftRgb = provinceImage.getRaster().getPixel(x - 1, y, (int[]) null);
                                if (!Arrays.equals(leftRgb, rgb) && getProvinceByColor(leftRgb[0], leftRgb[1], leftRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (x < provinceImage.getWidth() - 1) {
                                int[] rightRgb = provinceImage.getRaster().getPixel(x + 1, y, (int[]) null);
                                if (!Arrays.equals(rightRgb, rgb) && getProvinceByColor(rightRgb[0], rightRgb[1], rightRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y > 0) {
                                int[] topRgb = provinceImage.getRaster().getPixel(x, y - 1, (int[]) null);
                                if (!Arrays.equals(topRgb, rgb) && getProvinceByColor(topRgb[0], topRgb[1], topRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y < provinceImage.getHeight() - 1) {
                                int[] bottomRgb = provinceImage.getRaster().getPixel(x, y + 1, (int[]) null);
                                if (!Arrays.equals(bottomRgb, rgb) && getProvinceByColor(bottomRgb[0], bottomRgb[1], bottomRgb[2]).isOcean()) {
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
        File culturesFolder = new File(this.commonFolderPath + File.separator + "cultures");

        try (Stream<Path> paths = Files.walk(culturesFolder.toPath())) {
            this.cultureGroups = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem cultureGroupsItem = ClausewitzParser.parse(path.toFile(), 0, ClausewitzUtils.CHARSET);
                     this.cultureGroups.put(path, cultureGroupsItem.getChildren()
                                                                   .stream()
                                                                   .map(CultureGroup::new)
                                                                   .collect(Collectors.toList()));
                 });
            this.cultureGroups.values().forEach(groups -> groups.forEach(cultureGroup -> {
                cultureGroup.setLocalizedName(this.getLocalisation(cultureGroup.getName()));
                cultureGroup.getCultures().forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
            }));
        } catch (IOException e) {
        }
    }

    private void readReligion() {
        File culturesFolder = new File(this.commonFolderPath + File.separator + "religions");

        try (Stream<Path> paths = Files.walk(culturesFolder.toPath())) {
            this.religionGroups = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem religionGroupsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     this.religionGroups.put(path, religionGroupsItem.getChildren()
                                                                     .stream()
                                                                     .map(ReligionGroup::new)
                                                                     .collect(Collectors.toList()));
                 });
            this.religionGroups.values().forEach(groups -> groups.forEach(religionGroup -> {
                religionGroup.setLocalizedName(this.getLocalisation(religionGroup.getName()));
                religionGroup.getReligions().forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
            }));
        } catch (IOException e) {
        }
    }

    private void readInstitutions() {
        File institutionsFolder = new File(this.commonFolderPath + File.separator + "institutions");

        try (Stream<Path> paths = Files.walk(institutionsFolder.toPath())) {
            this.institutions = new HashMap<>();
            AtomicInteger i = new AtomicInteger();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     this.institutions.put(path, institutionsItem.getChildren()
                                                                 .stream()
                                                                 .map(child -> new Institution(child, i.getAndIncrement()))
                                                                 .collect(Collectors.toCollection(TreeSet::new)));
                 });
            this.institutions.values().forEach(institutionList -> institutionList.forEach(institution -> {
                institution.setLocalizedName(this.getLocalisation(institution.getName()));
            }));
        } catch (IOException e) {
        }
    }

    private void readTradeGoods() {
        File tradeGoodsFolder = new File(this.commonFolderPath + File.separator + "tradegoods");
        File pricesFolder = new File(this.commonFolderPath + File.separator + "prices");

        try (Stream<Path> paths = Files.walk(tradeGoodsFolder.toPath())) {
            this.tradeGoods = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     tradeGoodsItem.getChildren()
                                   .forEach(tradeGoodItem -> this.tradeGoods.put(new TradeGood(tradeGoodItem),
                                                                                 new AbstractMap.SimpleEntry<>(path, null)));
                 });
        } catch (IOException e) {
        }

        try (Stream<Path> paths = Files.walk(pricesFolder.toPath())) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     pricesItem.getChildren().forEach(priceItem -> {
                         for (Map.Entry<TradeGood, Map.Entry<Path, Path>> entry : this.tradeGoods.entrySet()) {
                             if (entry.getKey().getName().equals(priceItem.getName())) {
                                 entry.getKey().setPriceItem(priceItem);
                                 entry.getValue().setValue(path);
                             }
                         }

                     });
                 });
            this.tradeGoods.keySet()
                           .forEach(tradeGood -> tradeGood.setLocalizedName(this.getLocalisation(tradeGood.getName())));
        } catch (IOException e) {
        }
    }

    private void readBuildings() {
        File tradeGoodsFolder = new File(this.commonFolderPath + File.separator + "buildings");

        try (Stream<Path> paths = Files.walk(tradeGoodsFolder.toPath())) {
            this.buildings = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem buildingsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     buildingsItem.getChildrenNot("manufactory")
                                  .forEach(
                                          tradeGoodItem -> this.buildings.put(new Building(tradeGoodItem, this), path));

                     if ((buildingsItem = buildingsItem.getChild("manufactory")) != null) {
                         Building manufactoryBuilding = new Building(buildingsItem, this);
                         this.buildings.keySet().forEach(building -> {
                             building.setInternalCost(manufactoryBuilding.getCost());
                             building.setInternalTime(manufactoryBuilding.getTime());
                             building.setInternalModifiers(manufactoryBuilding.getModifiers());
                         });
                     }
                 });

            this.buildings.keySet()
                          .forEach(building -> building.setLocalizedName(this.getLocalisation(
                                  "building_" + building.getName())));
        } catch (IOException e) {
        }
    }

    private void readImperialReforms() {
        File imperialReformsFolder = new File(this.commonFolderPath + File.separator + "imperial_reforms");

        try (Stream<Path> paths = Files.walk(imperialReformsFolder.toPath())) {
            this.imperialReforms = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem imperialReformsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     imperialReformsItem.getChildren().forEach(item -> this.imperialReforms.put(new ImperialReform(item, this), path));
                 });

            this.imperialReforms.keySet()
                                .forEach(imperialReform -> imperialReform.setLocalizedName(this.getLocalisation(imperialReform.getName() + "_title")));
        } catch (IOException e) {
        }
    }

    private void readDecrees() {
        File decreesFolder = new File(this.commonFolderPath + File.separator + "decrees");

        try (Stream<Path> paths = Files.walk(decreesFolder.toPath())) {
            this.decrees = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem decreesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     decreesItem.getChildren().forEach(item -> this.decrees.put(new Decree(item), path));
                 });

            this.decrees.keySet().forEach(saveDecree -> saveDecree.setLocalizedName(this.getLocalisation(saveDecree.getName() + "_title")));
        } catch (IOException e) {
        }
    }

    private void readGoldenBulls() {
        File goldenBullsFolder = new File(this.commonFolderPath + File.separator + "golden_bulls");

        try (Stream<Path> paths = Files.walk(goldenBullsFolder.toPath())) {
            this.goldenBulls = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem goldenBullsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     goldenBullsItem.getChildren().forEach(item -> this.goldenBulls.put(new GoldenBull(item), path));
                 });

            this.goldenBulls.keySet()
                            .forEach(bull -> bull.setLocalizedName(this.getLocalisation(bull.getName())));
        } catch (IOException e) {
        }
    }

    private void readEvents() {
        File eventsFolder = new File(this.gameFolderPath + File.separator + "events");

        try (Stream<Path> paths = Files.walk(eventsFolder.toPath())) {
            this.events = new ConcurrentHashMap<>();

            paths.filter(Files::isRegularFile)
                 .parallel()
                 .forEach(path -> {
                     ClausewitzItem eventsItem = ClausewitzParser.parse(path.toFile(), 0);
                     eventsItem.getChildren().forEach(item -> this.events.put(new Event(item), path));
                 });

            this.events.keySet()
                       .parallelStream()
                       .forEach(event -> event.setLocalizedName(this.getLocalisation(ClausewitzUtils.removeQuotes(event.getTitle()))));
        } catch (IOException e) {
        }
    }

    private void readGovernments() {
        File eventsFolder = new File(this.commonFolderPath + File.separator + "governments");

        try (Stream<Path> paths = Files.walk(eventsFolder.toPath())) {
            this.governments = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                     governmentsItem.getChildrenNot("pre_dharma_mapping").forEach(item -> this.governments.put(new Government(item), path));
                 });

            this.governments.keySet().forEach(government -> government.setLocalizedName(this.getLocalisation(government.getBasicReform())));
        } catch (IOException e) {
        }
    }

    private void readGovernmentNames() {
        File govnmentNamesFolder = new File(this.commonFolderPath + File.separator + "government_names");

        try (Stream<Path> paths = Files.walk(govnmentNamesFolder.toPath())) {
            this.governmentNames = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                     governmentsItem.getChildren().forEach(item -> this.governmentNames.put(new GovernmentName(item, this), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readGovernmentReforms() {
        File governmentReformsFolder = new File(this.commonFolderPath + File.separator + "government_reforms");

        try (Stream<Path> paths = Files.walk(governmentReformsFolder.toPath())) {
            this.governmentReforms = new LinkedHashMap<>();

            Map<ClausewitzItem, Path> reformsItems = paths.filter(Files::isRegularFile)
                                                          .collect(Collectors.toMap(path -> ClausewitzParser.parse(path.toFile(), 0), Function.identity()));

            AtomicReference<GovernmentReform> defaultReform = new AtomicReference<>();
            reformsItems.keySet().stream().filter(item -> item.hasChild("defaults_reform")).findFirst().ifPresent(item -> {
                defaultReform.set(new GovernmentReform(item.getChild("defaults_reform"), this, null));
            });

            reformsItems.forEach((key, value) -> key
                    .getChildrenNot("defaults_reform")
                    .forEach(item -> this.governmentReforms.put(new GovernmentReform(item, this, defaultReform.get()), value)));

            this.governmentReforms.keySet().forEach(governmentReform -> governmentReform.setLocalizedName(this.getLocalisation(governmentReform.getName())));
        } catch (IOException e) {
        }
    }

    private void readUnits() {
        File unitsFolder = new File(this.commonFolderPath + File.separator + "units");

        try (Stream<Path> paths = Files.walk(unitsFolder.toPath())) {
            this.units = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem unitItem = ClausewitzParser.parse(path.toFile(), 0);
                     unitItem.setName(FilenameUtils.removeExtension(path.getFileName().toString()));
                     unitItem.getChildren().forEach(item -> this.units.put(new Unit(item, this::getLocalisation), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readAreas() {
        File areasFile = new File(this.mapFolderPath + File.separator + "area.txt");
        this.areas = new HashMap<>();
        ClausewitzItem areasItem = ClausewitzParser.parse(areasFile, 0);
        areasItem.getLists().forEach(item -> this.areas.put(new Area(item), areasFile.toPath()));
        areasItem.getChildren().forEach(item -> this.areas.put(new Area(item), areasFile.toPath()));
        this.areas.keySet().forEach(area -> area.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setArea(area)));
    }

    private void readRegions() {
        File regionsFile = new File(this.mapFolderPath + File.separator + "region.txt");
        this.regions = new HashMap<>();
        ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
        regionsItem.getChildren().forEach(item -> this.regions.put(new Region(item, this), regionsFile.toPath()));
        this.regions.keySet().stream().filter(region -> region.getAreas() != null).forEach(region -> region.getAreas().forEach(area -> area.setRegion(region)));
    }

    private void readSuperRegions() {
        File regionsFile = new File(this.mapFolderPath + File.separator + "superregion.txt");
        this.superRegions = new HashMap<>();
        ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
        regionsItem.getLists().forEach(item -> this.superRegions.put(new SuperRegion(item, this), regionsFile.toPath()));
        this.superRegions.keySet()
                         .stream()
                         .filter(superRegion -> superRegion.getRegions() != null)
                         .forEach(superRegion -> superRegion.getRegions().forEach(region -> region.setSuperRegion(superRegion)));
    }

    private void readTechGroups() {
        File techGroupsFile = new File(this.commonFolderPath + File.separator + "technology.txt");
        this.techGroups = new HashMap<>();
        ClausewitzItem techGroupsItem = ClausewitzParser.parse(techGroupsFile, 0);
        techGroupsItem.getChild("groups").getChildren().forEach(item -> this.techGroups.put(new TechGroup(item), techGroupsFile.toPath()));
        this.techGroups.keySet().forEach(techGroup -> techGroup.setLocalizedName(this.getLocalisation(techGroup.getName())));
    }

    private void readAdvisors() {
        File advisorsFolder = new File(this.commonFolderPath + File.separator + "advisortypes");

        try (Stream<Path> paths = Files.walk(advisorsFolder.toPath())) {
            this.advisors = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.advisors.put(new Advisor(item), path));
                 });

            this.advisors.keySet().forEach(advisor -> advisor.setLocalizedName(this.getLocalisation(advisor.getName())));
        } catch (IOException e) {
        }
    }

    private void readIdeaGroups() {
        File ideaGroupsFolder = new File(this.commonFolderPath + File.separator + "ideas");

        try (Stream<Path> paths = Files.walk(ideaGroupsFolder.toPath())) {
            this.ideaGroups = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.ideaGroups.put(new IdeaGroup(item), path));
                 });

            this.ideaGroups.keySet().forEach(ideaGroup -> ideaGroup.setLocalizedName(this.getLocalisation(ideaGroup.getName())));
        } catch (IOException e) {
        }
    }

    private void readCasusBelli() {
        File casusBelliFolder = new File(this.commonFolderPath + File.separator + "cb_types");

        try (Stream<Path> paths = Files.walk(casusBelliFolder.toPath())) {
            this.casusBelli = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.casusBelli.put(new CasusBelli(item), path));
                 });

            this.casusBelli.keySet().forEach(ideaGroup -> ideaGroup.setLocalizedName(this.getLocalisation(ideaGroup.getName())));
        } catch (IOException e) {
        }
    }

    private void readColonialRegions() {
        File casusBelliFolder = new File(this.commonFolderPath + File.separator + "colonial_regions");

        try (Stream<Path> paths = Files.walk(casusBelliFolder.toPath())) {
            this.colonialRegions = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.colonialRegions.put(new ColonialRegion(item, this), path));
                 });

            this.colonialRegions.keySet().forEach(colonialRegion -> colonialRegion.setLocalizedName(this.getLocalisation(colonialRegion.getName())));
        } catch (IOException e) {
        }
    }

    private void readTradeCompanies() {
        File casusBelliFolder = new File(this.commonFolderPath + File.separator + "trade_companies");

        try (Stream<Path> paths = Files.walk(casusBelliFolder.toPath())) {
            this.tradeCompanies = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.tradeCompanies.put(new TradeCompany(item), path));
                 });

            this.tradeCompanies.keySet().forEach(tradeCompany -> tradeCompany.setLocalizedName(this.getLocalisation(tradeCompany.getName())));
        } catch (IOException e) {
        }
    }

    private void readSubjectTypes() {
        File subjectTypesFolder = new File(this.commonFolderPath + File.separator + "subject_types");

        try (Stream<Path> paths = Files.walk(subjectTypesFolder.toPath())) {
            this.subjectTypes = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.subjectTypes.put(new SubjectType(item, this.subjectTypes.keySet()), path));
                 });

            this.subjectTypes.keySet().forEach(tradeCompany -> tradeCompany.setLocalizedName(this.getLocalisation(tradeCompany.getName())));
        } catch (IOException e) {
        }
    }

    private void readFetishistCults() {
        File fetishistCultsFolder = new File(this.commonFolderPath + File.separator + "fetishist_cults");

        try (Stream<Path> paths = Files.walk(fetishistCultsFolder.toPath())) {
            this.fetishistCults = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.fetishistCults.put(new FetishistCult(item), path));
                 });

            this.fetishistCults.keySet().forEach(fetishistCult -> fetishistCult.setLocalizedName(this.getLocalisation(fetishistCult.getName())));
        } catch (IOException e) {
        }
    }

    private void readMissionTrees() {
        File missionTreesFolder = new File(this.missionsFolderPath);

        try (Stream<Path> paths = Files.walk(missionTreesFolder.toPath())) {
            this.missionTrees = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
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
        } catch (IOException e) {
        }
    }

    private void readEstates() {
        //Read estates modifiers before necessary for privileges
        File estatesPreloadFolder = new File(this.commonFolderPath + File.separator + "estates_preload");

        try (Stream<Path> paths = Files.walk(estatesPreloadFolder.toPath())) {
            List<ModifierDefinition> modifierDefinitions = new ArrayList<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren()
                                 .stream()
                                 .map(item -> item.getChildren("modifier_definition"))
                                 .flatMap(Collection::stream)
                                 .forEach(item -> modifierDefinitions.add(new ModifierDefinition(item)));
                 });

            modifierDefinitions.forEach(
                    modifierDefinition -> ModifiersUtils.addModifier(modifierDefinition.getKey(), ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY));
        } catch (IOException e) {
        }

        File estatePrivilegesFolder = new File(this.commonFolderPath + File.separator + "estate_privileges");

        try (Stream<Path> paths = Files.walk(estatePrivilegesFolder.toPath())) {
            this.estatePrivileges = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.estatePrivileges.put(new EstatePrivilege(item), path));
                 });

            this.estatePrivileges.keySet().forEach(estatePrivilege -> estatePrivilege.setLocalizedName(this.getLocalisation(estatePrivilege.getName())));
        } catch (IOException e) {
        }

        File estatesFolder = new File(this.commonFolderPath + File.separator + "estates");

        try (Stream<Path> paths = Files.walk(estatesFolder.toPath())) {
            this.estates = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.estates.put(new Estate(item, this), path));
                 });

            this.estates.keySet().forEach(estate -> {
                estate.setLocalizedName(this.getLocalisation(estate.getName()));
            });
        } catch (IOException e) {
        }
    }

    private void readTechnologies() {
        File technologiesFolder = new File(this.commonFolderPath + File.separator + "technologies");

        try (Stream<Path> paths = Files.walk(technologiesFolder.toPath())) {
            this.technologies = new EnumMap<>(Power.class);
            Map<Technology, Path> techs = new HashMap<>();

            paths.filter(Files::isRegularFile)
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
        } catch (IOException e) {
        }
    }

    private void readRulerPersonalities() {
        File rulerPersonalitiesFolder = new File(this.commonFolderPath + File.separator + "ruler_personalities");

        try (Stream<Path> paths = Files.walk(rulerPersonalitiesFolder.toPath())) {
            this.rulerPersonalities = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem rulerPersonalityItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     rulerPersonalityItem.getChildren().forEach(item -> this.rulerPersonalities.put(new RulerPersonality(item), path));
                 });

            this.rulerPersonalities.keySet()
                                   .forEach(rulerPersonality -> rulerPersonality.setLocalizedName(this.getLocalisation(rulerPersonality.getName())));
        } catch (IOException e) {
        }
    }

    private void readProfessionalismModifiers() {
        File professionalismModifiersFolder = new File(this.commonFolderPath + File.separator + "professionalism");

        try (Stream<Path> paths = Files.walk(professionalismModifiersFolder.toPath())) {
            this.professionalismModifiers = new TreeMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem advisorsItem = ClausewitzParser.parse(path.toFile(), 0);
                     advisorsItem.getChildren().forEach(item -> this.professionalismModifiers.put(new ProfessionalismModifier(item), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readStaticModifiers() {
        File staticModifiersFolder = new File(this.commonFolderPath + File.separator + "static_modifiers");

        try (Stream<Path> paths = Files.walk(staticModifiersFolder.toPath())) {
            this.staticModifiers = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile).forEach(path -> {
                ClausewitzItem modifiersItem = ClausewitzParser.parse(path.toFile(), 0);
                modifiersItem.getChildrenNot("null_modifier").forEach(item -> {
                    if (StaticModifiers.value(item.getName()) != null) {
                        StaticModifiers.value(item.getName()).setModifiers(new Modifiers(item));
                        this.staticModifiers.put(new StaticModifier(item), path);
                    }
                });
            });

            this.staticModifiers.keySet().forEach(staticModifier -> staticModifier.setLocalizedName(this.getLocalisation(staticModifier.getName())));
        } catch (IOException e) {
        }
    }

    private void readInvestments() {
        File investmentsFolder = new File(this.commonFolderPath + File.separator + "tradecompany_investments");

        try (Stream<Path> paths = Files.walk(investmentsFolder.toPath())) {
            this.investments = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     investmentsItem.getChildren().forEach(item -> this.investments.put(new Investment(item, this), path));
                 });

            this.investments.keySet().forEach(investment -> investment.setLocalizedName(this.getLocalisation(investment.getName())));
        } catch (IOException e) {
        }
    }

    private void readPolicies() {
        File policiesFolder = new File(this.commonFolderPath + File.separator + "policies");

        try (Stream<Path> paths = Files.walk(policiesFolder.toPath())) {
            this.policies = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     investmentsItem.getChildren().forEach(item -> this.policies.put(new Policy(item), path));
                 });

            this.policies.keySet().forEach(policy -> policy.setLocalizedName(this.getLocalisation(policy.getName())));
        } catch (IOException e) {
        }
    }

    private void readHegemons() {
        File hegemonsFolder = new File(this.commonFolderPath + File.separator + "hegemons");

        try (Stream<Path> paths = Files.walk(hegemonsFolder.toPath())) {
            this.hegemons = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     hegemonsItem.getChildren().forEach(item -> this.hegemons.put(new Hegemon(item), path));
                 });

            this.hegemons.keySet().forEach(hegemon -> hegemon.setLocalizedName(this.getLocalisation(hegemon.getName())));
        } catch (IOException e) {
        }
    }

    private void readFactions() {
        File factionsFolder = new File(this.commonFolderPath + File.separator + "factions");

        try (Stream<Path> paths = Files.walk(factionsFolder.toPath())) {
            this.factions = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     hegemonsItem.getChildren().forEach(item -> this.factions.put(new Faction(item), path));
                 });

            this.factions.keySet().forEach(faction -> {
                faction.setLocalizedName(this.getLocalisation(faction.getName()));
                ModifiersUtils.addModifier(ClausewitzUtils.removeQuotes(faction.getName()) + "_influence", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
            });
        } catch (IOException e) {
        }
    }

    private void readAges() {
        File agesFolder = new File(this.commonFolderPath + File.separator + "ages");

        try (Stream<Path> paths = Files.walk(agesFolder.toPath())) {
            this.ages = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem agesItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     agesItem.getChildren().forEach(item -> this.ages.put(new Age(item), path));
                 });

            this.ages.keySet().forEach(age -> age.setLocalizedName(this.getLocalisation(age.getName())));
        } catch (IOException e) {
        }
    }

    private void readDefenderOfFaith() {
        File defenderOfFaithFolder = new File(this.commonFolderPath + File.separator + "defender_of_faith");

        try (Stream<Path> paths = Files.walk(defenderOfFaithFolder.toPath())) {
            this.defenderOfFaith = new TreeMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                     defenderOfFaithItem.getChildren().forEach(item -> this.defenderOfFaith.put(new DefenderOfFaith(item), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readCentersOfTrade() {
        File centersOfTradeFolder = new File(this.commonFolderPath + File.separator + "centers_of_trade");

        try (Stream<Path> paths = Files.walk(centersOfTradeFolder.toPath())) {
            this.centersOfTrade = new TreeMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem centersOfTradeItem = ClausewitzParser.parse(path.toFile(), 0);
                     centersOfTradeItem.getChildren().forEach(item -> this.centersOfTrade.put(new CenterOfTrade(item), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readFervors() {
        File fervorsFolder = new File(this.commonFolderPath + File.separator + "fervor");

        try (Stream<Path> paths = Files.walk(fervorsFolder.toPath())) {
            this.fervors = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem fervorsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     fervorsItem.getChildren().forEach(item -> this.fervors.put(new Fervor(item), path));
                 });

            this.fervors.keySet().forEach(fervor -> fervor.setLocalizedName(this.getLocalisation(fervor.getName())));
        } catch (IOException e) {
        }
    }

    private void readGreatProjects() {
        File greatProjectsFolder = new File(this.commonFolderPath + File.separator + "great_projects");

        try (Stream<Path> paths = Files.walk(greatProjectsFolder.toPath())) {
            this.greatProjects = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem greatProjectsItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     greatProjectsItem.getChildren().forEach(item -> this.greatProjects.put(new GreatProject(item), path));
                 });

            this.greatProjects.keySet().forEach(greatProject -> greatProject.setLocalizedName(this.getLocalisation(greatProject.getName())));
        } catch (IOException e) {
        }
    }

    private void readHolyOrders() {
        File holyOrdersFolder = new File(this.commonFolderPath + File.separator + "holy_orders");

        try (Stream<Path> paths = Files.walk(holyOrdersFolder.toPath())) {
            this.holyOrders = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem holyOrdersItem = ClausewitzParser.parse(path.toFile(), 0, StandardCharsets.UTF_8);
                     holyOrdersItem.getChildren().forEach(item -> this.holyOrders.put(new HolyOrder(item), path));
                 });

            this.holyOrders.keySet().forEach(holyOrder -> holyOrder.setLocalizedName(this.getLocalisation(holyOrder.getName())));
        } catch (IOException e) {
        }
    }

    private void readIsolationism() {
        File isolationismFolder = new File(this.commonFolderPath + File.separator + "isolationism");

        try (Stream<Path> paths = Files.walk(isolationismFolder.toPath())) {
            this.isolationisms = new TreeMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem isolationismItem = ClausewitzParser.parse(path.toFile(), 0);
                     isolationismItem.getChildren().forEach(item -> this.isolationisms.put(new Isolationism(item), path));
                 });

            this.isolationisms.keySet().forEach(isolationism -> isolationism.setLocalizedName(this.getLocalisation(isolationism.getName())));
        } catch (IOException e) {
        }
    }

    private void readNativeAdvancements() {
        File nativeAdvancementFolder = new File(this.commonFolderPath + File.separator + "native_advancement");

        try (Stream<Path> paths = Files.walk(nativeAdvancementFolder.toPath())) {
            this.nativeAdvancements = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem nativeAdvancementItem = ClausewitzParser.parse(path.toFile(), 0);
                     nativeAdvancementItem.getChildren().forEach(item -> this.nativeAdvancements.put(new NativeAdvancements(item), path));
                 });

            this.nativeAdvancements.keySet().forEach(advancements -> {
                advancements.setLocalizedName(this.getLocalisation(advancements.getName()));
                advancements.getNativeAdvancements()
                            .forEach(nativeAdvancement -> nativeAdvancement.setLocalizedName(this.getLocalisation(nativeAdvancement.getName())));
            });
        } catch (IOException e) {
        }
    }

    private void readNavalDoctrine() {
        File navalDoctrineFolder = new File(this.commonFolderPath + File.separator + "naval_doctrines");

        try (Stream<Path> paths = Files.walk(navalDoctrineFolder.toPath())) {
            this.navalDoctrines = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem navalDoctrineItem = ClausewitzParser.parse(path.toFile(), 0);
                     navalDoctrineItem.getChildren().forEach(item -> this.navalDoctrines.put(new NavalDoctrine(item), path));
                 });

            this.navalDoctrines.keySet().forEach(navalDoctrine -> navalDoctrine.setLocalizedName(this.getLocalisation(navalDoctrine.getName())));
        } catch (IOException e) {
        }
    }

    private void readParliamentIssue() {
        File parliamentIssueFolder = new File(this.commonFolderPath + File.separator + "parliament_issues");

        try (Stream<Path> paths = Files.walk(parliamentIssueFolder.toPath())) {
            this.parliamentIssues = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem parliamentIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                     parliamentIssueItem.getChildren().forEach(item -> this.parliamentIssues.put(new ParliamentIssue(item), path));
                 });

            this.parliamentIssues.keySet().forEach(parliamentIssue -> parliamentIssue.setLocalizedName(this.getLocalisation(parliamentIssue.getName())));
        } catch (IOException e) {
        }
    }

    private void readPersonalDeities() {
        File personalDeitiesFolder = new File(this.commonFolderPath + File.separator + "personal_deities");

        try (Stream<Path> paths = Files.walk(personalDeitiesFolder.toPath())) {
            this.personalDeities = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem personalIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                     personalIssueItem.getChildren().forEach(item -> this.personalDeities.put(new PersonalDeity(item), path));
                 });

            this.personalDeities.keySet().forEach(personalIssue -> personalIssue.setLocalizedName(this.getLocalisation(personalIssue.getName())));
        } catch (IOException e) {
        }
    }

    private void readReligiousReforms() {
        File religiousReformFolder = new File(this.commonFolderPath + File.separator + "religious_reforms");

        try (Stream<Path> paths = Files.walk(religiousReformFolder.toPath())) {
            this.religiousReforms = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem religiousReformItem = ClausewitzParser.parse(path.toFile(), 0);
                     religiousReformItem.getChildren().forEach(item -> this.religiousReforms.put(new ReligiousReforms(item), path));
                 });

            this.religiousReforms.keySet().forEach(reforms -> {
                reforms.setLocalizedName(this.getLocalisation(reforms.getName()));
                reforms.getReforms().forEach(religiousReform -> religiousReform.setLocalizedName(this.getLocalisation(religiousReform.getName())));
            });
        } catch (IOException e) {
        }
    }

    private void readCrownLandBonuses() {
        File estateCrownLandFolder = new File(this.commonFolderPath + File.separator + "estate_crown_land");

        try (Stream<Path> paths = Files.walk(estateCrownLandFolder.toPath())) {
            this.crownLandBonuses = new TreeMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                     defenderOfFaithItem.getChildren("bonus").forEach(item -> this.crownLandBonuses.put(new CrownLandBonus(item), path));
                 });
        } catch (IOException e) {
        }
    }

    private void readStateEdicts() {
        File stateEdictsFolder = new File(this.commonFolderPath + File.separator + "state_edicts");

        try (Stream<Path> paths = Files.walk(stateEdictsFolder.toPath())) {
            this.stateEdicts = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem stateEdictsItem = ClausewitzParser.parse(path.toFile(), 0);
                     stateEdictsItem.getChildren().forEach(item -> this.stateEdicts.put(new StateEdict(item), path));
                 });

            this.stateEdicts.keySet().forEach(stateEdict -> stateEdict.setLocalizedName(this.getLocalisation(stateEdict.getName())));
        } catch (IOException e) {
        }
    }

    private void readTradePolicies() {
        File tradePoliciesFolder = new File(this.commonFolderPath + File.separator + "trading_policies");

        try (Stream<Path> paths = Files.walk(tradePoliciesFolder.toPath())) {
            this.tradePolicies = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem tradePoliciesItem = ClausewitzParser.parse(path.toFile(), 0);
                     tradePoliciesItem.getChildren().forEach(item -> this.tradePolicies.put(new TradePolicy(item), path));
                 });

            this.tradePolicies.keySet().forEach(tradePolicy -> tradePolicy.setLocalizedName(this.getLocalisation(tradePolicy.getName())));
        } catch (IOException e) {
        }
    }

    private void readEventModifiers() {
        File eventModifiersFolder = new File(this.commonFolderPath + File.separator + "event_modifiers");

        try (Stream<Path> paths = Files.walk(eventModifiersFolder.toPath())) {
            this.eventModifiers = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem eventModifierItem = ClausewitzParser.parse(path.toFile(), 0);
                     eventModifierItem.getChildren().forEach(item -> this.eventModifiers.put(new EventModifier(item), path));
                 });

            this.eventModifiers.keySet().forEach(eventModifier -> eventModifier.setLocalizedName(this.getLocalisation(eventModifier.getName())));
        } catch (IOException e) {
        }
    }

    private void readProvinceTriggeredModifiers() {
        File provinceTriggeredModifiersFolder = new File(this.commonFolderPath + File.separator + "province_triggered_modifiers");

        try (Stream<Path> paths = Files.walk(provinceTriggeredModifiersFolder.toPath())) {
            this.provinceTriggeredModifiers = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem provinceTriggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                     provinceTriggeredIssueItem.getChildren().forEach(item -> this.provinceTriggeredModifiers.put(new TriggeredModifier(item), path));
                 });

            this.provinceTriggeredModifiers.keySet()
                                           .forEach(provinceTriggered -> provinceTriggered.setLocalizedName(this.getLocalisation(provinceTriggered.getName())));
        } catch (IOException e) {
        }
    }

    private void readTriggeredModifiers() {
        File triggeredModifiersFolder = new File(this.commonFolderPath + File.separator + "triggered_modifiers");

        try (Stream<Path> paths = Files.walk(triggeredModifiersFolder.toPath())) {
            this.triggeredModifiers = new LinkedHashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem triggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                     triggeredIssueItem.getChildren().forEach(item -> this.triggeredModifiers.put(new TriggeredModifier(item), path));
                 });

            this.triggeredModifiers.keySet()
                                   .forEach(triggeredModifier -> triggeredModifier.setLocalizedName(this.getLocalisation(triggeredModifier.getName())));
        } catch (IOException e) {
        }
    }
}
