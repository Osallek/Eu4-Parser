package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.lang3.StringUtils;

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

    public Province(String[] csvLine) {
        this.id = Integer.parseInt(csvLine[0]);

        if (StringUtils.isNoneBlank(csvLine[1], csvLine[2], csvLine[3])) {
            this.color = Eu4Utils.rgbToColor(Integer.parseInt(csvLine[1]), Integer.parseInt(csvLine[2]), Integer.parseInt(csvLine[3]));
        }

        if (csvLine.length >= 5) {
            this.name = csvLine[4];
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

    void setOcean(boolean isOcean) {
        this.isOcean = isOcean;
    }

    public boolean isLake() {
        return isLake;
    }

    void setLake(boolean isLake) {
        this.isLake = isLake;
    }

    public String getClimate() {
        return climate;
    }

    void setClimate(String climate) {
        this.climate = climate;
    }

    public boolean isImpassable() {
        return impassable;
    }

    void setImpassable(boolean impassable) {
        this.impassable = impassable;
    }

    public String getWinter() {
        return winter;
    }

    void setWinter(String winter) {
        this.winter = winter;
    }

    public boolean isColonizable() {
        return !isOcean() && !isLake() && !isImpassable();
    }

    public boolean isPort() {
        return isPort;
    }

    void setPort(boolean isPort) {
        this.isPort = isPort;
    }

    public Area getArea() {
        return area;
    }

    void setArea(Area area) {
        this.area = area;
    }

    public Continent getContinent() {
        return continent;
    }

    void setContinent(Continent continent) {
        this.continent = continent;
    }
}
