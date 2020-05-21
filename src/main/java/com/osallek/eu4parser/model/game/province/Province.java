package com.osallek.eu4parser.model.game.province;

public class Province {

    private final int id;

    private final int red;

    private final int green;

    private final int blue;

    private final String name;

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
}
