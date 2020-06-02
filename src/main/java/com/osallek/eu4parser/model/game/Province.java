package com.osallek.eu4parser.model.game;

import com.osallek.eu4parser.common.Eu4Utils;

public class Province {

    private final int id;

    private final int color;

    private final String name;

    private boolean isOcean = false;

    private boolean isLake = false;

    private String climate;

    private boolean impassable;

    private String winter;

    private boolean isPort;

    public Province(String[] csvLine) {
        this.id = Integer.parseInt(csvLine[0]);
        this.color = Eu4Utils.rgbToColor(Integer.parseInt(csvLine[1]), Integer.parseInt(csvLine[2]), Integer.parseInt(csvLine[3]));
        this.name = csvLine[4];
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
    }

    public int getId() {
        return id;
    }

    public int getRed() {
        return (this.color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (this.color >> 8) & 0xFF;
    }

    public int getBlue() {
        return (this.color) & 0xFF;
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
}
