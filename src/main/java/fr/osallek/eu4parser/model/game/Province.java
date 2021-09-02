package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Province {

    private final int id;

    private Integer color;

    private String name;

    private boolean isOcean = false;

    private boolean isLake = false;

    private String climate;

    private boolean impassable;

    private String winter;

    private boolean isPort;

    private Area area;

    private Continent continent;

    private double cityX;

    private double cityY;

    private double unitX;

    private double unitY;

    private double nameTextX;

    private double nameTextY;

    private double portX;

    private double portY;

    private double tradeRouteX;

    private double tradeRouteY;

    private double fightingUnitX;

    private double fightingUnitY;

    private double tradeWindX;

    private double tradeWindY;

    private ProvinceHistoryItem defaultHistoryItem;

    private SortedMap<LocalDate, ProvinceHistoryItem> historyItems;

    private FileNode historyFileNode;

    public Province(String[] csvLine) {
        this.id = Eu4Utils.cleanStringAndParseToInt(csvLine[0].trim());

        if (StringUtils.isNoneBlank(csvLine[1], csvLine[2], csvLine[3])) {
            this.color = Eu4Utils.rgbToColor(Eu4Utils.cleanStringAndParseToInt(csvLine[1].trim()), Eu4Utils.cleanStringAndParseToInt(csvLine[2].trim()),
                                             Eu4Utils.cleanStringAndParseToInt(csvLine[3].trim()));
        }

        if (csvLine.length >= 5) {
            this.name = csvLine[4].trim();
        }
    }

    public Province(Province other) {
        this.id = other.id;
        this.color = other.color;
        this.name = other.name;
        this.isOcean = other.isOcean;
        this.isLake = other.isLake;
        this.climate = other.climate;
        this.impassable = other.impassable;
        this.winter = other.winter;
        this.isPort = other.isPort;
        this.area = other.area;
        this.continent = other.continent;
        this.cityX = other.cityX;
        this.cityY = other.cityY;
        this.unitX = other.unitX;
        this.unitY = other.unitY;
        this.nameTextX = other.nameTextX;
        this.nameTextY = other.nameTextY;
        this.portX = other.portX;
        this.portY = other.portY;
        this.tradeRouteX = other.tradeRouteX;
        this.tradeRouteY = other.tradeRouteY;
        this.fightingUnitX = other.fightingUnitX;
        this.fightingUnitY = other.fightingUnitY;
        this.tradeWindX = other.tradeWindX;
        this.tradeWindY = other.tradeWindY;
        this.defaultHistoryItem = other.defaultHistoryItem;
        this.historyItems = other.historyItems;
        this.historyFileNode = other.historyFileNode;
    }

    public void setPositions(ClausewitzList positions) {
        if (positions == null || positions.isEmpty()) {
            return;
        }

        this.cityX = positions.getAsDouble(0);
        this.cityY = positions.getAsDouble(1);
        this.unitX = positions.getAsDouble(2);
        this.unitY = positions.getAsDouble(3);
        this.nameTextX = positions.getAsDouble(4);
        this.nameTextY = positions.getAsDouble(5);
        this.portX = positions.getAsDouble(6);
        this.portY = positions.getAsDouble(7);
        this.tradeRouteX = positions.getAsDouble(8);
        this.tradeRouteY = positions.getAsDouble(9);
        this.fightingUnitX = positions.getAsDouble(10);
        this.fightingUnitY = positions.getAsDouble(11);
        this.tradeWindX = positions.getAsDouble(12);
        this.tradeWindY = positions.getAsDouble(13);
    }

    public void setHistory(ClausewitzItem item, Game game, FileNode historyMod) {
        this.historyFileNode = historyMod;
        this.defaultHistoryItem = new ProvinceHistoryItem(item, game);
        this.historyItems = item.getChildren()
                                .stream()
                                .filter(child -> Eu4Utils.DATE_PATTERN.matcher(child.getName()).matches())
                                .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()), child -> new ProvinceHistoryItem(child, game),
                                                          (o1, o2) -> o1, TreeMap::new));
    }

    public int getId() {
        return id;
    }

    public Integer getColor() {
        return color;
    }

    public Integer getRed() {
        return this.color == null ? null : ((this.color >> 16) & 0xFF);
    }

    public Integer getGreen() {
        return this.color == null ? null : ((this.color >> 8) & 0xFF);
    }

    public Integer getBlue() {
        return this.color == null ? null : ((this.color) & 0xFF);
    }

    public String getName() {
        return name;
    }

    public boolean isOcean() {
        return isOcean;
    }

    public void setOcean(boolean ocean) {
        isOcean = ocean;
    }

    public boolean isLake() {
        return isLake;
    }

    public void setLake(boolean lake) {
        isLake = lake;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public boolean isImpassable() {
        return impassable;
    }

    public void setImpassable(boolean impassable) {
        this.impassable = impassable;
    }

    public String getWinter() {
        return winter;
    }

    public void setWinter(String winter) {
        this.winter = winter;
    }

    public boolean isColonizable() {
        return !isOcean() && !isLake() && !isImpassable();
    }

    public boolean isPort() {
        return isPort;
    }

    public void setPort(boolean port) {
        isPort = port;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public double getCityX() {
        return cityX;
    }

    public void setCityX(double cityX) {
        this.cityX = cityX;
    }

    public double getCityY() {
        return cityY;
    }

    public void setCityY(double cityY) {
        this.cityY = cityY;
    }

    public double getUnitX() {
        return unitX;
    }

    public void setUnitX(double unitX) {
        this.unitX = unitX;
    }

    public double getUnitY() {
        return unitY;
    }

    public void setUnitY(double unitY) {
        this.unitY = unitY;
    }

    public double getNameTextX() {
        return nameTextX;
    }

    public void setNameTextX(double nameTextX) {
        this.nameTextX = nameTextX;
    }

    public double getNameTextY() {
        return nameTextY;
    }

    public void setNameTextY(double nameTextY) {
        this.nameTextY = nameTextY;
    }

    public double getPortX() {
        return portX;
    }

    public void setPortX(double portX) {
        this.portX = portX;
    }

    public double getPortY() {
        return portY;
    }

    public void setPortY(double portY) {
        this.portY = portY;
    }

    public double getTradeRouteX() {
        return tradeRouteX;
    }

    public void setTradeRouteX(double tradeRouteX) {
        this.tradeRouteX = tradeRouteX;
    }

    public double getTradeRouteY() {
        return tradeRouteY;
    }

    public void setTradeRouteY(double tradeRouteY) {
        this.tradeRouteY = tradeRouteY;
    }

    public double getFightingUnitX() {
        return fightingUnitX;
    }

    public void setFightingUnitX(double fightingUnitX) {
        this.fightingUnitX = fightingUnitX;
    }

    public double getFightingUnitY() {
        return fightingUnitY;
    }

    public void setFightingUnitY(double fightingUnitY) {
        this.fightingUnitY = fightingUnitY;
    }

    public double getTradeWindX() {
        return tradeWindX;
    }

    public void setTradeWindX(double tradeWindX) {
        this.tradeWindX = tradeWindX;
    }

    public double getTradeWindY() {
        return tradeWindY;
    }

    public void setTradeWindY(double tradeWindY) {
        this.tradeWindY = tradeWindY;
    }

    public ProvinceHistoryItem getDefaultHistoryItem() {
        return defaultHistoryItem;
    }

    public SortedMap<LocalDate, ProvinceHistoryItem> getHistoryItems() {
        return historyItems;
    }

    public FileNode getHistoryFileNode() {
        return historyFileNode;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Province)) {
            return false;
        }

        Province province = (Province) o;

        return id == province.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
