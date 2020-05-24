package com.osallek.eu4parser.model.game;

import com.osallek.eu4parser.common.Eu4Utils;

public class Province {

    private final int id;

    private final int red;

    private final int green;

    private final int blue;

    private final String name;

    private boolean isOcean = false;

    private String climate;

    private boolean impassable;

    private String winter;

    public Province(String[] csvLine) {
        this.id = Integer.parseInt(csvLine[0]);
        this.red = Integer.parseInt(csvLine[1]);
        this.green = Integer.parseInt(csvLine[2]);
        this.blue = Integer.parseInt(csvLine[3]);
        this.name = csvLine[4];
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

    void setOcean(boolean ocean) {
        isOcean = ocean;
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
}
