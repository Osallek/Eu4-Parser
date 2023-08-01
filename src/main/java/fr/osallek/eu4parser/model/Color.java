package fr.osallek.eu4parser.model;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

public class Color {

    private final ClausewitzList list;

    private final boolean isDouble;

    public Color(ClausewitzList list) {
        this.list = list;
        this.isDouble = false;
    }

    public Color(ClausewitzList list, boolean isDouble) {
        this.list = list;
        this.isDouble = isDouble;
    }

    public int getRed() {
        return Math.max(0, Math.min(255, this.isDouble ? (int) (this.list.getAsDouble(0) * 255) : this.list.getAsInt(0)));
    }

    public void setRed(int red) {
        if (this.isDouble) {
            this.list.set(0, (double) red / 255);
        } else {
            this.list.set(0, red);
        }
    }

    public int getGreen() {
        return Math.max(0, Math.min(255, this.isDouble ? (int) (this.list.getAsDouble(1) * 255) : this.list.getAsInt(1)));
    }

    public void setGreen(int green) {
        if (this.isDouble) {
            this.list.set(1, (double) green / 255);
        } else {
            this.list.set(1, green);
        }
    }

    public int getBlue() {
        return Math.max(0, Math.min(255, this.isDouble ? (int) (this.list.getAsDouble(2) * 255) : this.list.getAsInt(2)));
    }

    public void setBlue(int blue) {
        if (this.isDouble) {
            this.list.set(2, (double) blue / 255);
        } else {
            this.list.set(2, blue);
        }
    }

    public java.awt.Color toColor() {
        return new java.awt.Color(getRed(), getGreen(), getBlue());
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, Color color) {
        return addToItem(parent, name, color.getRed(), color.getGreen(), color.getBlue());
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, int red, int green, int blue) {
        return parent.addList(name, red, green, blue);
    }
}
