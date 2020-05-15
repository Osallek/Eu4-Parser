package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

public class Color {

    private final ClausewitzList list;

    public Color(ClausewitzList list) {
        this.list = list;
    }

    public int getRed() {
        return this.list.getAsInt(0);
    }

    public void setRed(int red) {
        this.list.set(0, red);
    }

    public int getBlue() {
        return this.list.getAsInt(1);
    }

    public void setBlue(int blue) {
        this.list.set(1, blue);
    }

    public int getGreen() {
        return this.list.getAsInt(2);
    }

    public void setGreen(int green) {
        this.list.set(2, green);
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, int red, int blue, int green) {
        return parent.addList(name, red, blue, green);
    }
}
