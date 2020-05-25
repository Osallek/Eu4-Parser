package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.game.culture.Culture;
import com.osallek.eu4parser.model.game.culture.CultureGroup;
import com.osallek.eu4parser.model.game.religion.Religion;
import com.osallek.eu4parser.model.game.religion.ReligionGroup;
import com.osallek.eu4parser.model.save.Save;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private final String gameFolderPath;

    private final String mapFolderPath;

    private final String commonFolderPath;

    private final String gfxFolderPath;

    private final Save save;

    private Map<Path, List<CultureGroup>> cultureGroups;

    private Map<Path, List<ReligionGroup>> religionGroups;

    private Map<Path, List<Institution>> institutions;

    public Game(String gameFolderPath, Save save) throws IOException {
        this.gameFolderPath = gameFolderPath;
        this.mapFolderPath = this.gameFolderPath + File.separator + "map";
        this.commonFolderPath = this.gameFolderPath + File.separator + "common";
        this.gfxFolderPath = this.gameFolderPath + File.separator + "gfx";
        this.save = save;

        readProvinces();
        readCultures();
        readReligion();
        readInstitutions();
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

    public Collection<CultureGroup> getCultureGroups() {
        return this.cultureGroups.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Culture> getCultures() {
        return getCultureGroups().stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .sorted(Comparator.comparing(Culture::getName))
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
                                  .sorted(Comparator.comparing(Religion::getName))
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

    private void readProvinces() throws IOException {
        Map<Integer, Province> provinces = new HashMap<>();

        File provincesDefinitionFile = new File(this.mapFolderPath + File.separator + "definition.csv");

        if (provincesDefinitionFile.canRead()) {
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), ClausewitzUtils.CHARSET)) {
                String line;
                reader.readLine(); //Skip csv headers
                while ((line = reader.readLine()) != null) {
                    Province province = new Province(line.split(";"));
                    provinces.put(province.getId(), province);
                }
            }
        }

        File provincesMapFile = new File(this.mapFolderPath + File.separator + "default.map");

        if (provincesMapFile.canRead()) {
            ClausewitzItem provinceMapItem = ClausewitzParser.parse(provincesMapFile, 0, StandardCharsets.UTF_8);
            ClausewitzList seaList = provinceMapItem.getList("sea_starts");

            if (seaList != null) {
                seaList.getValuesAsInt().forEach(id -> provinces.get(id).setOcean(true));
            }

            ClausewitzList lakesList = provinceMapItem.getList("lakes");

            if (lakesList != null) {
                lakesList.getValuesAsInt().forEach(id -> provinces.get(id).setOcean(true));
            }
        }

        File climateFile = new File(this.mapFolderPath + File.separator + "climate.txt");

        if (climateFile.canRead()) {
            ClausewitzItem climateItem = ClausewitzParser.parse(climateFile, 0, StandardCharsets.UTF_8);
            climateItem.getLists()
                       .forEach(list -> {
                           if (list.getName().endsWith("_winter")) {
                               list.getValuesAsInt().forEach(id -> provinces.get(id).setWinter(list.getName()));
                           } else if (Eu4Utils.IMPASSABLE_CLIMATE.equals(list.getName())) {
                               list.getValuesAsInt().forEach(id -> provinces.get(id).setImpassable(true));
                           } else {
                               list.getValuesAsInt().forEach(id -> provinces.get(id).setClimate(list.getName()));
                           }
                       });
        }

        provinces.forEach((id, province) -> this.save.getProvince(id).mergeWithGame(province));

        this.save.updateProvinceByColor();
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
        } catch (IOException e) {
        }
    }

    private void readInstitutions() {
        File culturesFolder = new File(this.commonFolderPath + File.separator + "institutions");

        try (Stream<Path> paths = Files.walk(culturesFolder.toPath())) {
            this.institutions = new HashMap<>();

            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0);
                     this.institutions.put(path, institutionsItem.getChildren()
                                                                 .stream()
                                                                 .map(Institution::new)
                                                                 .collect(Collectors.toList()));
                 });
        } catch (IOException e) {
        }
    }
}
