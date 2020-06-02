package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.game.localisation.Eu4Language;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private Collator collator;

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

    private Map<Path, List<Institution>> institutions;

    private Map<TradeGood, Map.Entry<Path, Path>> tradeGoods; //LinkedHashMap to keep the order

    private Map<Building, Path> buildings; //LinkedHashMap to keep the order

    private Map<String, String> localisations;

    private Map<String, SpriteType> spriteTypes;

    public Game(String gameFolderPath) throws IOException {
        this.gameFolderPath = gameFolderPath;
        this.mapFolderPath = this.gameFolderPath + File.separator + "map";
        this.commonFolderPath = this.gameFolderPath + File.separator + "common";
        this.gfxFolderPath = this.gameFolderPath + File.separator + "gfx";
        this.localisationFolderPath = this.gameFolderPath + File.separator + "localisation";
        this.interfaceFolderPath = this.gameFolderPath + File.separator + "interface";

        loadLocalisations();
        readSpriteTypes();
        readProvinces();
        readCultures();
        readReligion();
        readInstitutions();
        readTradeGoods();
        readBuildings();
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
        String localisation = this.localisations.get(key);

        if (localisation == null) {
            return key;
        }

        int indexOf;
        if ((indexOf = localisation.indexOf('§')) >= 0) {
            if (ClausewitzUtils.hasAtLeast(localisation, '§', 2)) {
                String[] splits = localisation.split("§");
                localisation = "";
                for (int i = 0; i < splits.length; i += 2) {
                    localisation += splits[i] + " ";
                }
            } else {
                localisation = localisation.substring(0, indexOf);
            }
        }

        if ((indexOf = localisation.indexOf('$')) >= 0) {
            if (ClausewitzUtils.hasAtLeast(localisation, '$', 2)) {
                String[] splits = localisation.split("\\$");
                localisation = "";
                for (int i = 0; i < splits.length; i += 2) {
                    localisation += splits[i] + " ";
                }
            } else {
                localisation = localisation.substring(0, indexOf);
            }
        }

        return localisation.replace("\\r\\n", "")
                           .replace("\\n", " ")
                           .replaceAll("[^\\p{L}\\p{M}\\p{Alnum}\\p{Space}]", "")
                           .trim();
    }

    public SpriteType getSpriteType(String key) {
        return this.spriteTypes.get(key);
    }

    public Collection<CultureGroup> getCultureGroups() {
        return this.cultureGroups.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Culture> getCultures() {
        return getCultureGroups().stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .sorted(Comparator.comparing(Culture::getLocalizedName, collator))
                                 .collect(Collectors.toList());
    }

    public Culture getCulture(String name) {
        return getCultureGroups().stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .filter(culture -> culture.getName().equals(name))
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
                                  .sorted(Comparator.comparing(Religion::getLocalizedName, collator))
                                  .collect(Collectors.toList());
    }

    public Religion getReligion(String name) {
        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .filter(religion -> religion.getName().equals(name))
                                  .findFirst()
                                  .orElse(null);
    }

    public List<Institution> getInstitutions() {
        return this.institutions.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Institution getInstitution(int i) {
        return getInstitutions().get(i);
    }

    public List<TradeGood> getTradeGoods() {
        return this.tradeGoods.keySet()
                              .stream()
                              .sorted(Comparator.comparing(TradeGood::getLocalizedName, collator))
                              .collect(Collectors.toList());
    }

    public TradeGood getTradeGood(String name) {
        for (TradeGood tradeGood : getTradeGoods()) {
            if (tradeGood.getName().equals(name)) {
                return tradeGood;
            }
        }

        return null;
    }

    public List<Building> getBuildings() {
        return this.buildings.keySet()
                             .stream()
                             .sorted(Comparator.comparing(Building::getLocalizedName, collator))
                             .collect(Collectors.toList());
    }

    public Building getBuilding(String name) {
        for (Building building : getBuildings()) {
            if (building.getName().equals(name)) {
                return building;
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
            File[] files = localisationFolder.listFiles((dir, fileName) -> fileName.endsWith(
                    eu4Language.fileEndWith + ".yml"));
            if (files != null) {
                this.localisations = new HashMap<>();
                this.collator = Collator.getInstance(eu4Language.locale);
                this.collator.setStrength(Collator.NO_DECOMPOSITION);

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
                         ClausewitzItem rootItem = ClausewitzParser.parse(path.toFile(), 0);
                         ClausewitzItem spriteTypesItem = rootItem.getChild("spriteTypes");

                         if (spriteTypesItem != null) {
                             this.spriteTypes.putAll(spriteTypesItem.getChildren("spriteType")
                                                                    .stream()
                                                                    .map(SpriteType::new)
                                                                    .collect(Collectors.toMap(spriteType -> ClausewitzUtils
                                                                                                      .removeQuotes(spriteType
                                                                                                                            .getName()),
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
                                   list.getValuesAsInt()
                                       .forEach(id -> this.provinces.get(id).setWinter(list.getName()));
                               } else if (Eu4Utils.IMPASSABLE_CLIMATE.equals(list.getName())) {
                                   list.getValuesAsInt().forEach(id -> this.provinces.get(id).setImpassable(true));
                               } else {
                                   list.getValuesAsInt()
                                       .forEach(id -> this.provinces.get(id).setClimate(list.getName()));
                               }
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
                                if (!Arrays.equals(leftRgb, rgb)
                                    && getProvinceByColor(leftRgb[0], leftRgb[1], leftRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (x < provinceImage.getWidth() - 1) {
                                int[] rightRgb = provinceImage.getRaster().getPixel(x + 1, y, (int[]) null);
                                if (!Arrays.equals(rightRgb, rgb)
                                    && getProvinceByColor(rightRgb[0], rightRgb[1], rightRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y > 0) {
                                int[] topRgb = provinceImage.getRaster().getPixel(x, y - 1, (int[]) null);
                                if (!Arrays.equals(topRgb, rgb)
                                    && getProvinceByColor(topRgb[0], topRgb[1], topRgb[2]).isOcean()) {
                                    province.setPort(true);
                                }
                            }

                            if (y < provinceImage.getHeight() - 1) {
                                int[] bottomRgb = provinceImage.getRaster().getPixel(x, y + 1, (int[]) null);
                                if (!Arrays.equals(bottomRgb, rgb)
                                    && getProvinceByColor(bottomRgb[0], bottomRgb[1], bottomRgb[2]).isOcean()) {
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
                     ClausewitzItem cultureGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                     this.cultureGroups.put(path, cultureGroupsItem.getChildren()
                                                                   .stream()
                                                                   .map(CultureGroup::new)
                                                                   .collect(Collectors.toList()));
                 });
            this.cultureGroups.values().forEach(groups -> groups.forEach(cultureGroup -> {
                cultureGroup.setLocalizedName(this.getLocalisation(cultureGroup.getName()));
                cultureGroup.getCultures()
                            .forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
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
                     ClausewitzItem religionGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                     this.religionGroups.put(path, religionGroupsItem.getChildren()
                                                                     .stream()
                                                                     .map(ReligionGroup::new)
                                                                     .collect(Collectors.toList()));
                 });
            this.religionGroups.values().forEach(groups -> groups.forEach(religionGroup -> {
                religionGroup.setLocalizedName(this.getLocalisation(religionGroup.getName()));
                religionGroup.getReligions()
                             .forEach(culture -> culture.setLocalizedName(this.getLocalisation(culture.getName())));
            }));
        } catch (IOException e) {
        }
    }

    private void readInstitutions() {
        File institutionsFolder = new File(this.commonFolderPath + File.separator + "institutions");

        try (Stream<Path> paths = Files.walk(institutionsFolder.toPath())) {
            this.institutions = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0);
                     this.institutions.put(path, institutionsItem.getChildren()
                                                                 .stream()
                                                                 .map(Institution::new)
                                                                 .collect(Collectors.toList()));
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
                     ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0);
                     tradeGoodsItem.getChildren()
                                   .forEach(tradeGoodItem -> this.tradeGoods.put(new TradeGood(tradeGoodItem), new AbstractMap.SimpleEntry<>(path, null)));
                 });
        } catch (IOException e) {
        }

        try (Stream<Path> paths = Files.walk(pricesFolder.toPath())) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0);
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
                     ClausewitzItem buildingsItem = ClausewitzParser.parse(path.toFile(), 0);
                     buildingsItem.getChildrenNot("manufactory")
                                  .forEach(tradeGoodItem -> this.buildings.put(new Building(tradeGoodItem, this), path));

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
}
