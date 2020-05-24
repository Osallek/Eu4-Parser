package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.ClausewitzParser;
import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Save;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Game {

    private final String gameFolderPath;

    private final Save save;

    public Game(String gameFolderPath, Save save) throws IOException {
        this.gameFolderPath = gameFolderPath;
        this.save = save;

        readProvinces();
    }

    public File getProvincesImage() {
        return new File(this.gameFolderPath + File.separator + "map" + File.separator + "provinces.bmp");
    }

    public File getNormalCursorImage() {
        return new File(
                this.gameFolderPath + File.separator + "gfx" + File.separator + "cursors" + File.separator
                + "normal.png");
    }

    public File getSelectedCursorImage() {
        return new File(
                this.gameFolderPath + File.separator + "gfx" + File.separator + "cursors" + File.separator
                + "selected.png");
    }

    private void readProvinces() throws IOException {
        Map<Integer, Province> provinces = new HashMap<>();

        File provincesDefinitionFile = new File(this.gameFolderPath + File.separator + "map"
                                                + File.separator + "definition.csv");

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

        File provincesMapFile = new File(this.gameFolderPath + File.separator + "map"
                                         + File.separator + "default.map");

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

        File climateFile = new File(this.gameFolderPath + File.separator + "map" + File.separator + "climate.txt");

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
}
