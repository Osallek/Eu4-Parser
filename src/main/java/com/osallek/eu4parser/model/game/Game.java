package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.common.LuaUtils;
import com.osallek.eu4parser.model.game.localisation.Eu4Language;
import com.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private final Collator collator;

    private final String gameFolderPath;

    private final String mapFolderPath;

    private final String commonFolderPath;

    private final String gfxFolderPath;

    private final String localisationFolderPath;

    private final String interfaceFolderPath;

    private Map<Integer, Province> provinces;

    private Map<Integer, Province> provincesByColor;

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

    private Map<Unit, Path> units;

    private Map<Area, Path> areas;

    private Map<Advisor, Path> advisors;

    private Map<IdeaGroup, Path> ideaGroups;

    private Map<CasusBelli, Path> casusBelli;

    private final Map<String, Map<String, Exp.Constant>> defines;

    public Game(String gameFolderPath) throws IOException, ParseException {
        this.collator = Collator.getInstance();
        this.collator.setStrength(Collator.NO_DECOMPOSITION);

        this.gameFolderPath = gameFolderPath;
        this.mapFolderPath = this.gameFolderPath + File.separator + "map";
        this.commonFolderPath = this.gameFolderPath + File.separator + "common";
        this.gfxFolderPath = this.gameFolderPath + File.separator + "gfx";
        this.localisationFolderPath = this.gameFolderPath + File.separator + "localisation";
        this.interfaceFolderPath = this.gameFolderPath + File.separator + "interface";
        this.defines = LuaUtils.luaFileToMap(this.commonFolderPath + File.separator + "defines.lua");

        loadLocalisations();
        readSpriteTypes();
        readProvinces();
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
        readUnits();
        readAreas();
        readAdvisors();
        readIdeaGroups();
        readCasusBelli();
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
        if (country == null) {
            return null;
        }

        return new File(this.gfxFolderPath + File.separator + "flags" + File.separator + country.getTag() + ".tga");
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
        return ((LuaInteger) this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("MAX_GOV_RANK").value).v;
    }

    public int getMaxAspects() {
        return ((LuaInteger) this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("MAX_UNLOCKED_ASPECTS").value).v;
    }

    public int getGoldenEraDuration() {
        return ((LuaInteger) this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("GOLDEN_ERA_YEARS").value).v;
    }

    public int getBankruptcyDuration() {
        return ((LuaInteger) this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("BANKRUPTCY_DURATION").value).v;
    }

    public int getNbGreatPowers() {
        return ((LuaInteger) this.defines.get(Eu4Utils.DEFINE_COUNTRY_KEY).get("NUM_OF_GREAT_POWERS").value).v;
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

        for (CasusBelli casusBelli : this.casusBelli.keySet()) {
            if (casusBelli.getName().equalsIgnoreCase(name)) {
                return casusBelli;
            }
        }

        return null;
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
                List<ClausewitzList> lists = continentsItem.getListsNot("island_check_provinces");
                for (int i = 0; i < lists.size(); i++) {
                    int finalI = i;
                    lists.get(i).getValuesAsInt().forEach(provinceId -> this.getProvince(provinceId).setContinent(finalI));
                }
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
}
