package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.province.Province;
import com.osallek.eu4parser.model.save.Save;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Game {

    File documents = new javax.swing.JFileChooser().getFileSystemView().getDefaultDirectory();

    private final String gameFolderPath;

    private final Save save;

    private Map<Integer, Province> provinces;

    private Map<Integer, Map<Integer, Map<Integer, Province>>> provincesByColor;

    public Game(String gameFolderPath, Save save) throws IOException {
        this.gameFolderPath = gameFolderPath;
        this.save = save;

        readProvinces();
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    public Province getProvinceByColor(int red, int blue, int green) {
        return provincesByColor.getOrDefault(red, new HashMap<>())
                               .getOrDefault(blue, new HashMap<>())
                               .getOrDefault(green, null);
    }

    private void readProvinces() throws IOException {
        File provincesDefinitionFile = new File(this.gameFolderPath + File.separator + "map"
                                                + File.separator + "definition.csv");

        if (provincesDefinitionFile.canRead()) {
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), ClausewitzUtils.CHARSET)) {
                this.provinces = new HashMap<>();
                this.provincesByColor = new HashMap<>();
                String line;
                reader.readLine(); //Skip csv headers
                while ((line = reader.readLine()) != null) {
                    Province province = new Province(line.split(";"));
                    this.provinces.put(province.getId(), province);

                    if (!this.provincesByColor.containsKey(province.getRed())) {
                        this.provincesByColor.put(province.getRed(), new HashMap<>());
                    }

                    Map<Integer, Map<Integer, Province>> greenMap = this.provincesByColor.get(province.getRed());

                    if (!greenMap.containsKey(province.getGreen())) {
                        greenMap.put(province.getGreen(), new HashMap<>());
                    }

                    Map<Integer, Province> blueMap = greenMap.get(province.getGreen());
                    blueMap.put(province.getBlue(), province);
                }
            }
        }
    }
}
