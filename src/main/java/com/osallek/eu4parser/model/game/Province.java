package com.osallek.eu4parser.model.game;

public class Province {

    private final int id;

    private final int red;

    private final int green;

    private final int blue;

    private final String name;

    private boolean isOcean = false;

    private boolean isLake = false;

    private String climate;

    private boolean impassable;

    private String winter;

    private boolean isPort;

    public Province(String[] csvLine) {
        this.id = Integer.parseInt(csvLine[0]);
        this.red = Integer.parseInt(csvLine[1]);
        this.green = Integer.parseInt(csvLine[2]);
        this.blue = Integer.parseInt(csvLine[3]);
        this.name = csvLine[4];
    }

    public Province(Province other) {
        this.id = other.id;
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
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
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
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
