package fr.osallek.eu4parser.model;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.clausewitzparser.parser.ClausewitzParser;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.TradeGood;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mod {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mod.class);

    private final File file;

    private final ClausewitzItem item;

    private final Map<Path, List<Building>> buildings = new LinkedHashMap<>();

    private final Map<Path, List<TradeGood>> tradeGoods = new LinkedHashMap<>();

    public Mod(File file, ClausewitzItem item) {
        this.file = file;
        this.item = item;
    }

    public void load() {
        if (getPath() != null && getPath().exists() && getPath().isDirectory()) {
            try {
                Path tradeGoodsPath = getPath().toPath().resolve(Eu4Utils.COMMON_FOLDER_PATH).resolve("tradegoods");
                if (tradeGoodsPath.toFile().exists() && tradeGoodsPath.toFile().isDirectory()) {
                    try (Stream<Path> stream = Files.list(tradeGoodsPath)) {
                        stream.forEach(path -> {
                            ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0);
                            this.tradeGoods.put(path,
                                                tradeGoodsItem.getChildren()
                                                              .stream()
                                                              .map(TradeGood::new)
                                                              .collect(Collectors.toList()));
                        });
                    }
                }

                Path pricesPath = getPath().toPath().resolve(Eu4Utils.COMMON_FOLDER_PATH).resolve("prices");
                if (pricesPath.toFile().exists() && pricesPath.toFile().isDirectory()) {
                    try (Stream<Path> stream = Files.list(pricesPath)) {
                        stream.forEach(path -> {
                            ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0);
                            pricesItem.getChildren().forEach(priceItem -> {
                                TradeGood tradeGood = getTradeGood(pricesItem.getName());

                                if (tradeGood != null) {
                                    tradeGood.setPriceItem(priceItem);
                                }
                            });
                        });
                    }
                }

                Path buildingsPath = getPath().toPath().resolve(Eu4Utils.COMMON_FOLDER_PATH).resolve("buildings");

                if (buildingsPath.toFile().exists() && buildingsPath.toFile().isDirectory()) {
                    try (Stream<Path> stream = Files.list(buildingsPath)) {
                        stream.forEach(path -> {
                            ClausewitzItem buildingsItem = ClausewitzParser.parse(path.toFile(), 0);
                            this.buildings.put(path,
                                               buildingsItem.getChildrenNot("manufactory")
                                                            .stream()
                                                            .map(buildingItem -> new Building(buildingItem, this::getTradeGood, null))
                                                            .collect(Collectors.toList()));

                            if ((buildingsItem = buildingsItem.getChild("manufactory")) != null) {
                                Building manufactureBuilding = new Building(buildingsItem, this::getTradeGood, null);
                                this.buildings.values()
                                              .stream()
                                              .flatMap(Collection::stream)
                                              .filter(building -> CollectionUtils.isNotEmpty(building.getManufactoryFor()))
                                              .forEach(building -> building.updateInternal(manufactureBuilding));
                            }
                        });
                    }
                }
            } catch (IOException e) {
                LOGGER.error("An error occurred while reading files of {}: {}!", getName(), e.getMessage(), e);
            }
        }
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getVersion() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("version"));
    }

    public void setVersion(String Version) {
        this.item.setVariable("version", ClausewitzUtils.addQuotes(Version));
    }

    public String getSupportedVersion() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("supported_version"));
    }

    public void setSupportedVersion(String supportedVersion) {
        this.item.setVariable("supported_version", ClausewitzUtils.addQuotes(supportedVersion));
    }

    public String getRemoteFileId() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("remote_file_id"));
    }

    public void setRemoteFileId(String remoteFileId) {
        this.item.setVariable("remote_file_id", ClausewitzUtils.addQuotes(remoteFileId));
    }

    public String getPathString() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("path"));
    }

    public File getPath() {
        return new File(getPathString());
    }

    public void setPath(File path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path.getAbsolutePath()));
    }

    public List<String> getTags() {
        ClausewitzList list = this.item.getList("tags");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).collect(Collectors.toList());
    }

    public void setTags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            this.item.removeList("tags");
        } else {
            tags = tags.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());
            ClausewitzList list = this.item.getList("tags");

            if (list == null) {
                list = this.item.addList("tags", false, tags);
            } else {
                list.clear();
                list.addAll(tags);
            }
        }
    }

    public List<String> getDependencies() {
        ClausewitzList list = this.item.getList("dependencies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).collect(Collectors.toList());
    }

    public void setDependencies(List<String> dependencies) {
        if (CollectionUtils.isEmpty(dependencies)) {
            this.item.removeList("dependencies");
        } else {
            dependencies = dependencies.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());
            ClausewitzList list = this.item.getList("dependencies");

            if (list == null) {
                list = this.item.addList("dependencies", false, dependencies);
            } else {
                list.clear();
                list.addAll(dependencies);
            }
        }
    }

    public List<String> getReplacePath() {
        return this.item.getVarsAsStrings("replace_path").stream().map(ClausewitzUtils::removeQuotes).collect(Collectors.toList());
    }

    public void setReplacePath(List<String> replacePath) {
        replacePath = replacePath.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());
        this.item.removeVariables("replace_path");

        replacePath.forEach(s -> this.item.addVariable("replace_path", ClausewitzUtils.addQuotes(s)));
    }

    public String getPicture() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("picture"));
    }

    public void setPicture(String picture) {
        if (StringUtils.isBlank(picture)) {
            this.item.removeVariable("picture");
        } else {
            this.item.setVariable("picture", ClausewitzUtils.addQuotes(picture));
        }
    }

    public File getFile() {
        return file;
    }

    public Map<Path, List<Building>> getBuildings() {
        return buildings;
    }

    public Map<Path, List<TradeGood>> getTradeGoods() {
        return tradeGoods;
    }

    public TradeGood getTradeGood(String name) {
        return this.tradeGoods.values().stream().flatMap(Collection::stream).filter(tradeGood -> tradeGood.getName().equals(name)).findFirst().orElse(null);
    }

    public void save() throws IOException {
        save(new HashMap<>());
    }

    public void save(Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8)) {
            this.item.write(bufferedWriter, 0, listeners);
        }
    }
}
